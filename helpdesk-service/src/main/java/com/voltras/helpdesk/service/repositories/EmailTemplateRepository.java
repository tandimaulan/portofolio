package com.voltras.helpdesk.service.repositories;

import javax.validation.constraints.NotBlank;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.helpdesk.service.entities.EmailTemplate;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
	EmailTemplate findByTemplateName(String templateName);

	EmailTemplate findByTemplateNameAndLang(@NotBlank String templateName, @NotBlank String lang);
}