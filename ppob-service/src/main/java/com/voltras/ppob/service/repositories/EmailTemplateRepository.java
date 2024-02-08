package com.voltras.ppob.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.ppob.service.entities.EmailTemplate;


public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long>{
	EmailTemplate findByTemplateName(String templateName);
}
