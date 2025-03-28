package com.smartface.keycloak.eventlistenerspi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author CelalKartal The responsibility of this class is to listen for
 *         keycloak events and send them to an external system or save them to
 *         the database if they are not sent to this external system.
 */
public class CustomEventListenerProvider implements EventListenerProvider {
	private static final String EVENTSERVICE_EVENT_URL = System.getenv("EVENTCONSUMERSERVICE_EVENT_URL");
	private static final Logger logger = Logger.getLogger(CustomEventListenerProvider.class.getName());

	private final KeycloakSession keycloakSession;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public CustomEventListenerProvider(KeycloakSession keycloakSession) {
		this.keycloakSession = keycloakSession;
		this.restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();

	}

	/**
	 * In this method, a USER event will be processed. If the service does not catch
	 * this event or if an error is received from the service or if it does not
	 * return one of the 2xx successful HTTP codes as a response, save the event to
	 * the database.
	 */
	@Override
	public void onEvent(Event event) {
		String eventJson = null;
		KeycloakEventRequest request = new KeycloakEventRequest();
		try {
			request.setEventId(event.getId());
			request.setEventType("Event");
			request.setEventJson(event.toString());
			eventJson = objectMapper.writeValueAsString(event);
			request.setEventJson(eventJson);
			ResponseEntity<Void> response = restTemplate.postForEntity(EVENTSERVICE_EVENT_URL, request, Void.class);
			logger.info(EVENTSERVICE_EVENT_URL + " response:" + (response != null ? response.getStatusCode() : "null"));
			if (response == null || !response.getStatusCode().is2xxSuccessful()) {
				saveEventOnError(request);
			}
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "An error occurred while accessing "+ EVENTSERVICE_EVENT_URL );
			saveEventOnError(request);
		}
	}

	/**
	 * In this method, a ADMIN event will be processed. If the service does not
	 * catch this event or if an error is received from the service or if it does
	 * not return one of the 2xx successful HTTP codes as a response, save the event
	 * to the database.
	 */
	@Override
	public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
		String eventJson = null;
		KeycloakEventRequest request = new KeycloakEventRequest();
		try {
			request.setEventId(adminEvent.getId());
			request.setEventType("AdminEvent");
			request.setEventJson(
					adminEvent.getRepresentation() != null ? adminEvent.getRepresentation() : adminEvent.toString());
			eventJson = objectMapper.writeValueAsString(adminEvent);
			request.setEventJson(eventJson);
			ResponseEntity<Void> response = restTemplate.postForEntity(EVENTSERVICE_EVENT_URL, request, Void.class);
			logger.info(EVENTSERVICE_EVENT_URL + " response:" + (response != null ? response.getStatusCode() : "null"));
			if (response == null || !response.getStatusCode().is2xxSuccessful()) {
				saveEventOnError(request);
			}
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "An error occurred while accessing "+ EVENTSERVICE_EVENT_URL );
			saveEventOnError(request);
		}

	}

	@Override
	public void close() {
	}

	/**
	 * If an access error occurs while sending the event, save the event to the
	 * database.
	 * 
	 * @param keycloakEventRequest
	 */
	public void saveEventOnError(KeycloakEventRequest keycloakEventRequest) {
		try (Connection conn = DatabaseUtil.getConnection()) {
			String sql = "INSERT INTO smartface.keycloak_events "
					+ "(inser_date, status, event_id, event_type, event_json) "
					+ "VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
				stmt.setString(2, "NEW");
				stmt.setObject(3, keycloakEventRequest.getEventId());
				stmt.setString(4, keycloakEventRequest.getEventType());
				stmt.setString(5, keycloakEventRequest.getEventJson());
				stmt.executeUpdate();
				logger.info("Event saved successfully.");
			}
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "An error was received while adding the event to the database.", e);
		}
	}

}