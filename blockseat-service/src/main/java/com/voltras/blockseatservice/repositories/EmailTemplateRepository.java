package com.voltras.blockseatservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.blockseatservice.entities.EmailTemplate;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long>{
	EmailTemplate findByTemplateName(String templateName);
}
