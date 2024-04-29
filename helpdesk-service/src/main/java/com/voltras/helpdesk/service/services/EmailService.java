package com.voltras.helpdesk.service.services;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.helpdesk.service.components.LogHelper;
import com.voltras.helpdesk.service.components.RavenConnector;
import com.voltras.helpdesk.service.entities.EmailTemplate;
import com.voltras.helpdesk.service.models.EmailMimeType;
import com.voltras.helpdesk.service.repositories.EmailTemplateRepository;
import com.voltras.kismisconnector.ConnectorException;
import com.voltras.raven.model.MessageAttachment;
import com.voltras.raven.model.Receiver;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;
import jakarta.el.ValueExpression;
import jakarta.validation.constraints.NotBlank;

@Service
public class EmailService {
	@Autowired
	private RavenConnector raven;
	@Autowired
	SystemParameterService systemParam;
	@Autowired
	private EmailTemplateRepository repo;
	@Autowired
	private LogHelper logger;

	public void send(List<String> targets, List<String> ccs, List<String> bccs, String templateName,
			Map<String, String> parameters, EmailMimeType mimeType, String sender) {
		send(targets, ccs, bccs, templateName, parameters, null, null, mimeType, sender);
	}

	public void send(List<String> targets, List<String> ccs, List<String> bccs, String templateName,
			Map<String, String> parameters, String filename, String attachment, EmailMimeType mimeType, String sender) {
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

	public String generateFromTemplate(@NotBlank String template, Map<String, Object> params) {
		String parsed;
		if (params != null && !params.isEmpty()) {
			ExpressionFactory factory = ExpressionFactory.newInstance();
			ELContext context = new StandardELContext(factory);

			ValueExpression valueExpression = factory.createValueExpression(params, Map.class);
			context.getVariableMapper().setVariable("param", valueExpression);
			ValueExpression parsedExpression = factory.createValueExpression(context, template, Object.class);

			parsed = parsedExpression.getValue(context).toString();
		} else {
			parsed = template;
		}
		return parsed;
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

	public Boolean send(String sender, @NotEmpty List<@NotBlank String> targets, String content,
			@NotBlank String subject, String mimeType, List<@NotBlank String> ccs, List<@NotBlank String> bccs,
			Map<String, @NotNull @Valid MessageAttachment> attachments) throws ConnectorException, URISyntaxException {
		if (sender == null)
			sender = systemParam.get("default.sender");

		ccs = ccs == null ? new ArrayList<>() : ccs;
		bccs = bccs == null ? new ArrayList<>() : bccs;
		var totalReceiver = Math.max(targets.size(), Math.max(ccs.size(), bccs.size()));

		List<Receiver> receivers = new ArrayList<Receiver>();
		for (var i = 0; i < totalReceiver; i++) {
			Receiver receiver = new Receiver();
			receiver.setTarget(i >= targets.size() ? null : targets.get(i));
			receiver.setTargetAdditional(i >= ccs.size() ? null : ccs.get(i));
			receiver.setTargetHidden(i >= bccs.size() ? null : bccs.get(i));
			receivers.add(receiver);
		}

		sendToRaven(subject, content, targets, ccs, bccs, attachments, mimeType, sender);
		return true;
	}

}
