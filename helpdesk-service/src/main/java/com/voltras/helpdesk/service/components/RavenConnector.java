package com.voltras.helpdesk.service.components;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voltras.helpdesk.service.utils.JsonUtil;
import com.voltras.kismisconnector.ConnectorException;
import com.voltras.kismisconnector.KismisConnector;
import com.voltras.raven.model.MessageAttachment;
import com.voltras.raven.model.Receiver;
import com.voltras.raven.request.InputMessageRequest;
import com.voltras.raven.response.InputMessageResponse;

@Component
public class RavenConnector {

	@Autowired
	@Qualifier("ravenConnector")
	private KismisConnector connector;

	@Autowired
	private LogHelper logger;

	public void send(String subject, String content, String mimeType, List<String> targets, List<String> ccs,
			List<String> bccs, Map<String, MessageAttachment> attachments, String sender)
			throws ConnectorException, URISyntaxException {
		InputMessageRequest request = generateDefaultRequest(subject, content, mimeType, attachments, sender);
		List<Receiver> receivers = new ArrayList<Receiver>();
		if (targets == null) {
			targets = new ArrayList<>();
		}

		if (ccs == null) {
			ccs = new ArrayList<>();
		}

		if (bccs == null) {
			bccs = new ArrayList<>();
		}

		var totalReceiver = Math.max(targets.size(), Math.max(ccs.size(), bccs.size()));

		for (var i = 0; i < totalReceiver; i++) {
			Receiver receiver = new Receiver();
			receiver.setTarget(i >= targets.size() ? null : targets.get(i));
			receiver.setTargetAdditional(i >= ccs.size() ? null : ccs.get(i));
			receiver.setTargetHidden(i >= bccs.size() ? null : bccs.get(i));
			receivers.add(receiver);
		}

		request.setListReceiver(receivers);
		request.setTargetRelease(new Date());
		try {
			logger.info("request: {}", JsonUtil.parseToString(request));
		} catch (JsonProcessingException e) {
		}
		connector.send(request, InputMessageResponse.class);
	}

	private InputMessageRequest generateDefaultRequest(String subject, String content, String mimeType,
			Map<String, MessageAttachment> attachments, String sender) {
		InputMessageRequest request = new InputMessageRequest();
		request.setSubject(subject);
		request.setContent(content);
		request.setTargetRelease(new Date());
		request.setMimeType(mimeType);
		request.setSender(sender);
		request.setAccountKey("VSendMail");
		request.setMarkAsProcessed(false);
		request.setAdvancedMapAttachments(attachments);

		return request;
	}
}
