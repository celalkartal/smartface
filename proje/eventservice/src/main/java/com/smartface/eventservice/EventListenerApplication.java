package com.smartface.eventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventListenerApplication.class, args);
	}

}
