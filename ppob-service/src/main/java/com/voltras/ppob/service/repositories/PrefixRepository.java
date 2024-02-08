package com.voltras.ppob.service.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voltras.ppob.service.entities.Prefix;

@Repository
public interface PrefixRepository extends JpaRepository<Prefix, UUID> {

}