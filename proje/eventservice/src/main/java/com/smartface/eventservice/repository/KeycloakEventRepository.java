package com.smartface.eventservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.smartface.eventservice.model.entity.KeycloakEvent;

@Repository
public interface KeycloakEventRepository extends JpaRepository<KeycloakEvent, Long> {

	//FOR UPDATE SKIP LOCKED ile birden fazla instance ilgili kayıtlara erişmek isterse, lock koyar.
	@Query(value = "SELECT * FROM smartface.keycloak_events WHERE status = 'NEW' ORDER BY inser_date ASC FOR UPDATE SKIP LOCKED", nativeQuery = true)
	List<KeycloakEvent> findUnprocessedEvents();
}