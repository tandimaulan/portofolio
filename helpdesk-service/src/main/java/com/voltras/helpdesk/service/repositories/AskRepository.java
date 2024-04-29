package com.voltras.helpdesk.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voltras.helpdesk.service.entities.Ask;

@Repository
public interface AskRepository extends JpaRepository<Ask, Long> {
}