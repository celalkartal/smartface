package com.smartface.eventservice.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "keycloak_events", schema = "smartface")
@Getter
@Setter
@ToString
public class KeycloakEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "INSER_DATE", nullable = false)
	private LocalDateTime insertDate;

	@Column(name = "STATUS", nullable = false, length = 20)
	private String status;

	@Column(name = "EVENT_ID", nullable = false, unique = true, length = 255)
	private String eventId;

	@Column(name = "EVENT_TYPE", nullable = false, length = 255)
	private String eventType;

	@Column(name = "EVENT_JSON", nullable = false, length = 4000)
	private String eventJson;
}