package com.smartface.eventservice.model.request;

import lombok.Data;

@Data
public class KeycloakEventRequest {
	private String eventJson;
	private String eventId;
	private String eventType;
}
