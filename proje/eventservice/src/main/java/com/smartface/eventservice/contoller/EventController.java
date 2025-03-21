package com.smartface.eventservice.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartface.eventservice.model.request.KeycloakEventRequest;
import com.smartface.eventservice.service.KeycloakEventService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class EventController {
	private final KeycloakEventService keycloakEventService;

	public EventController(KeycloakEventService kafkaProducerService) {
		this.keycloakEventService = kafkaProducerService;
	}

	@PostMapping("/keycloak/event")
	public ResponseEntity<Void> receiveEvent(@RequestBody KeycloakEventRequest keycloakEventRequest) {
		log.info("Keycloak Event Recieved!");
		keycloakEventService.saveEvent(keycloakEventRequest);
		return ResponseEntity.ok().build();
	}
}