package com.voltras.ppob.service.services;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.StandardELContext;
import javax.el.ValueExpression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.kismisconnector.ConnectorException;
import com.voltras.ppob.service.components.LogHelper;
import com.voltras.ppob.service.components.RavenConnector;
import com.voltras.ppob.service.entities.EmailTemplate;
import com.voltras.ppob.service.models.EmailMimeType;
import com.voltras.ppob.service.repositories.EmailTemplateRepository;
import com.voltras.raven.model.MessageAttachment;

@Service
public class EmailService {
	@Autowired
	private RavenConnector raven;
	@Autowired
	private EmailTemplateRepository repo;
	@Autowired
	private LogHelper logger;

	public void send(List<String> targets, List<String> ccs, List<String> bccs, String templateName,
			Map<String, String> parameters, EmailMimeType mimeType, String sender) {
		send(targets, ccs, bccs, templateName, parameters, null, null, mimeType, sender);
	}

	public void send(List<String> targets, List<String> ccs, List<String> bccs, String templateName,
			Map<String, String> parameters,  String filename, String attachment, EmailMimeType mimeType, String sender) {
		try {
			var template = repo.findByTemplateName(templateName);
			var evaluatedTemplate = evaluate(template, parameters);
			var subject = evaluatedTemplate.getSubject();
			var content = evaluatedTemplate.getContent();
			var attachments = prepareAttachment(filename, attachment);
			sendToRaven(subject, content, targets, ccs, bccs, attachments, mimeType.toValue(), sender);
		} catch (Exception e) {
//			logger.error("[{}.send] {}", this.getClass().getCanonicalName(), e.getClass().getCanonicalName());
			return;
		}
	}

	private EmailTemplate evaluate(EmailTemplate template, Map<String, String> parameters) {
		ExpressionFactory factory = ExpressionFactory.newInstance();
		ELContext context = new StandardELContext(factory);

		ValueExpression valueExpression = factory.createValueExpression(parameters, Map.class);
		context.getVariableMapper().setVariable("data", valueExpression);
		ValueExpression finve = factory.createValueExpression(context, template.getSubject(), String.class);
		var evaluatedSubject = finve.getValue(context).toString();
		finve = factory.createValueExpression(context, template.getContent(), String.class);
		var evaluatedContent = finve.getValue(context).toString();

		var evaluatedTemplate = new EmailTemplate(null, template.getTemplateName(), evaluatedSubject, evaluatedContent,
				template.getLang(), null);

		return evaluatedTemplate;
	}

	private Map<String, MessageAttachment> prepareAttachment(String filename, String attachment) {
		if (filename == null || attachment == null) {
			return null;
		}

		Map<String, MessageAttachment> attachments = new HashMap<>();
		var msgAttachment = new MessageAttachment();
		msgAttachment.setUseBaseSixtyFour(true);
		msgAttachment.setFileDataBaseSixtyFour(attachment.replace("\n", "").replace("\r", ""));
		msgAttachment.setHeader("application/pdf");
		attachments.put(filename, msgAttachment);

		return attachments;
	}

	private void sendToRaven(String subject, String content, List<String> targets, List<String> ccs, List<String> bccs,
			Map<String, MessageAttachment> attachments, String mimeType, String sender) {
		try {
			raven.send(subject, content, mimeType, targets, ccs, bccs, attachments, sender);
		} catch (ConnectorException e) {
			logger.error("[{}.sendToRaven] ConnectorException: {}", this.getClass().getCanonicalName(), e.getCause());
			return;
		} catch (URISyntaxException e) {
			logger.error("[{}.sendToRaven] {}", this.getClass().getCanonicalName(), e.getClass().getCanonicalName());
			return;
		}
	}

}
