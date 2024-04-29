package com.voltras.helpdesk.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voltras.helpdesk.service.entities.AskCategory;

@Repository
public interface AskCategoryRepository extends JpaRepository<AskCategory, Long> {
}
