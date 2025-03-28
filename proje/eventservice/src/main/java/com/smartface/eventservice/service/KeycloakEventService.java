package com.smartface.eventservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartface.eventservice.model.entity.KeycloakEvent;
import com.smartface.eventservice.model.request.KeycloakEventRequest;
import com.smartface.eventservice.repository.KeycloakEventRepository;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakEventService {
	private static final String LOCK_KEY = "postgresql-event-lock";
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final KeycloakEventRepository keycloakEventRepository;
	private final RedisLockService redisLockService;

	public void saveEvent(KeycloakEventRequest keycloakEventRequest) {
		try {

			/**
			 * In order to transfer events to Kafka in order,
			 * if there are unprocessed event records in the DB, 
			 * it is necessary to start from the DB first. 
			 * Therefore, after all event records in the DB are processed with batch, 
			 * new events will be transferred directly to Kafka.
			 */

			if (keycloakEventRepository.count() > 0L) {
				saveEventToDatabase(keycloakEventRequest);
			} else {
				saveEventToKafka(keycloakEventRequest.getEventJson());
			}
		} catch (Throwable e) {
			log.error("KAFKA_EVENT_ERROR", e);
			saveEventToDatabase(keycloakEventRequest);
		}
	}

	@Retry(name = "saveEventToKafka", fallbackMethod = "fallbackForKafka")
	public void saveEventToKafka(String eventJson) {
		log.info("Event sending to Kafka :" + eventJson);
		kafkaTemplate.send("keycloak-events", "KEYCLOAK_EVENT", eventJson);
		log.info("Event sent to Kafka");
	}

	@Retry(name = "saveEventToDatabase", fallbackMethod = "fallbackForDatabase")
	public void saveEventToDatabase(KeycloakEventRequest keycloakEventRequest) {
		log.info("Event sending to database:" + keycloakEventRequest.getEventId());
		KeycloakEvent event = new KeycloakEvent();
		event.setInsertDate(LocalDateTime.now());
		event.setStatus("NEW");
		event.setEventId(keycloakEventRequest.getEventId());
		event.setEventType(keycloakEventRequest.getEventType());
		event.setEventJson(keycloakEventRequest.getEventJson());
		keycloakEventRepository.save(event);
	}

	public void fallbackForKafka(Throwable ex) throws Throwable {
		log.error("Fallback for Kafka: ", ex);
		throw ex;
	}

	public void fallbackForDatabase(Throwable ex) throws Throwable {
		log.error("Fallback for database: ", ex);
		throw ex;
	}


	@Scheduled(fixedRate = 10000)
	@Transactional
	public void recoverFromDatabase() {
		if (redisLockService.getLock(LOCK_KEY)) {
			try {
				List<KeycloakEvent> events = keycloakEventRepository.findUnprocessedEvents();
				for (KeycloakEvent event : events) {
					try {
						saveEventToKafka(event.getEventJson());
						keycloakEventRepository.delete(event);
					} catch (Throwable t) {
						log.error("Failed to process event:"+ event.getId(), t);
						break;
					}
				}
			} catch (Throwable t) {
				log.error("Failed to process events", t);
			} finally {
				redisLockService.releaseLock(LOCK_KEY);
			}
		} else {
			log.info("Could not get lock, another instance is processing events.");
		}
	}
}