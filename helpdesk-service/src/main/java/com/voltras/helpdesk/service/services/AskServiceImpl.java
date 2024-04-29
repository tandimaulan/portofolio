package com.voltras.helpdesk.service.services;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.helpdesk.api.exceptions.RequirementMismatchException;
import com.voltras.helpdesk.api.models.Category;
import com.voltras.helpdesk.api.services.AskService;
import com.voltras.helpdesk.service.components.LogHelper;
import com.voltras.helpdesk.service.entities.Ask;
import com.voltras.helpdesk.service.entities.AskAttachment;
import com.voltras.helpdesk.service.repositories.AskCategoryRepository;
import com.voltras.helpdesk.service.repositories.AskRepository;
import com.voltras.helpdesk.service.repositories.EmailTemplateRepository;
import com.voltras.kismisconnector.ConnectorException;
import com.voltras.raven.model.MessageAttachment;
import com.voltras.voltrasspring.common.VoltrasSpringMultipartRequest;
import com.voltras.voltrasspring.exceptions.exceptions.VoltrasSpringRuntimeException;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.Publish;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Service("askService")
public class AskServiceImpl implements AskService, RpcBasicService {

	@Autowired
	private AskCategoryRepository askCategoryRepo;
	@Autowired
	private AskRepository askRepo;
	@Autowired
	private EmailTemplateRepository emailTemplateRepo;

	@Autowired
	EmailService emailService;
	@Autowired
	LogHelper logger;
	@Autowired
	private SystemParameterService systemParam;
	@Autowired
	private VoltrasSpringMultipartRequest multiPartRequest;

	@Override
	@Publish(allowAll = true)
	public List<Category> getCategories() {
		return askCategoryRepo.findAll().stream()
				.map(askCategory -> new Category(askCategory.getName(), askCategory.getNeedBody(),
						askCategory.getNeedBookingCode(), askCategory.getNeedBankName(), askCategory.getNeedBankCode(),
						askCategory.getNeedAccountName(), askCategory.getNeedAccountNumber(),
						askCategory.getNeedBranchBank(), askCategory.getNeedPhoneNumber()))
				.collect(Collectors.toList());
	}

	@Override
	@Publish(allowAll = true)
	public Boolean ask(@NotEmpty String sender, @NotNull @Valid Category category, String body, String type,
			String subject, String bookingCode, String bankName, String bankCode, String accountName,
			String accountNumber, String branchBank, String phoneNumber) throws RequirementMismatchException {
		validateAsk(category, body, bookingCode, bankName, bankCode, accountName, accountNumber, branchBank,
				phoneNumber);
		List<AskAttachment> askAttachments = new ArrayList<>();
		Map<String, MessageAttachment> messageAttachments = new HashMap<>();
		if (multiPartRequest.getFiles() != null && !multiPartRequest.getFiles().isEmpty()) {
			for (var file : multiPartRequest.getFiles()) {
				try {
					var base64Attachment = Base64.encodeBase64String(file.getBytes());
					askAttachments.add(
							new AskAttachment(file.getName(), file.getSize(), base64Attachment, file.getContentType()));
					var messageAttachment = new MessageAttachment();
					messageAttachment.setUseBaseSixtyFour(true);
					messageAttachment.setFileDataBaseSixtyFour(base64Attachment);
					messageAttachments.put(file.getName(), messageAttachment);
				} catch (Exception e) {
				}
			}
		}
		Map<String, Object> bodyParams = getBodyParams(category, body, bookingCode, bankName, bankCode, accountName,
				accountNumber, branchBank, phoneNumber);
		var emailTemplate = emailTemplateRepo.findByTemplateName(category.name());
		var emailBody = emailService.generateFromTemplate(emailTemplate.getContent(), bodyParams);
		var target = systemParam.get("email.hd");
		var ask = askRepo.saveAndFlush(new Ask(sender, subject, type, bookingCode, bankName, bankCode, accountName,
				accountNumber, branchBank, phoneNumber, emailBody, askAttachments.isEmpty() ? null : askAttachments));

		Boolean response;
		try {
			response = emailService.send(sender, List.of(target), emailBody, subject,
					emailTemplate.getMimeType().toString(), null, null,
					messageAttachments.isEmpty() ? null : messageAttachments);

		} catch (ConnectorException | URISyntaxException e) {
			logger.error("[{}.sentEmail] {}: {}", this.getClass().getCanonicalName(), e.getClass().getCanonicalName(),
					e.getCause());
			throw new VoltrasSpringRuntimeException(e);
		} catch (Exception e) {
			Throwable cause = e instanceof VoltrasSpringRuntimeException ? e.getCause() : e;
			logger.error("[{}.sentEmail] {}: {}", this.getClass().getCanonicalName(), e.getClass().getCanonicalName(),
					cause);
			throw new VoltrasSpringRuntimeException(e);
		}
		ask.setIsSend(response);
		askRepo.saveAndFlush(ask);
		return true;
	}

	private Map<String, Object> getBodyParams(Category category, String body, String bookingCode, String bankName,
			String bankCode, String accountName, String accountNumber, String branchBank, String phoneNumber) {
		Map<String, Object> bodyParams = new HashMap<>();
		if (category.needAccountName()) {
			bodyParams.put("accountName", accountName);
		}
		if (category.needAccountNumber()) {
			bodyParams.put("accountNumber", accountNumber);
		}
		if (category.needBankCode()) {
			bodyParams.put("bankCode", bankCode);
		}
		if (category.needBankName()) {
			bodyParams.put("bankName", bankName);
		}
		if (category.needBody()) {
			bodyParams.put("body", body);
		}
		if (category.needBranchBank()) {
			bodyParams.put("branchBank", branchBank);
		}
		if (category.needPhoneNumber()) {
			bodyParams.put("phoneNumber", phoneNumber);
		}
		return bodyParams;
	}

	private void validateAsk(Category category, String body, String bookingCode, String bankName, String bankCode,
			String accountName, String accountNumber, String branchBank, String phoneNumber)
			throws RequirementMismatchException {
		if (category.needAccountName() && (accountName == null || accountName.isBlank())) {
			logger.error("[{}.validateAsk] Blank Bank Account Name", this.getClass().getCanonicalName());
		} else if (category.needAccountNumber() && (accountNumber == null || accountNumber.isBlank())) {
			logger.error("[{}.validateAsk] Blank Bank Account Number", this.getClass().getCanonicalName());
		} else if (category.needBankCode() && (bankCode == null || bankCode.isBlank())) {
			logger.error("[{}.validateAsk] Blank Bank Code", this.getClass().getCanonicalName());
		} else if (category.needBankName() && (bankName == null || bankName.isBlank())) {
			logger.error("[{}.validateAsk] Blank Bank Name", this.getClass().getCanonicalName());
		} else if (category.needBody() && (body == null || body.isBlank())) {
			logger.error("[{}.validateAsk] Blank Body", this.getClass().getCanonicalName());
		} else if (category.needBookingCode() && (bookingCode == null || bookingCode.isBlank())) {
			logger.error("[{}.validateAsk] Blank Booking Code", this.getClass().getCanonicalName());
		} else if (category.needBranchBank() && (branchBank == null || branchBank.isBlank())) {
			logger.error("[{}.validateAsk] Blank Branch Bank", this.getClass().getCanonicalName());
		} else if (category.needPhoneNumber() && (phoneNumber == null || phoneNumber.isBlank())) {
			logger.error("[{}.validateAsk] Blank Phone Number", this.getClass().getCanonicalName());
		} else {
			return;
		}

		throw new RequirementMismatchException();
	}
}
