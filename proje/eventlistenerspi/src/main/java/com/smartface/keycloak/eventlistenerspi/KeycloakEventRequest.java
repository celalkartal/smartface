package com.smartface.keycloak.eventlistenerspi;

public class KeycloakEventRequest {
	private String eventJson;
	private String eventId;
	private String eventType;

	public String getEventJson() {
		return eventJson;
	}

	public void setEventJson(String eventJson) {
		this.eventJson = eventJson;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

}