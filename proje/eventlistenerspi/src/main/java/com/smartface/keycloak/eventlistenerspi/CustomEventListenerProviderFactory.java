package com.smartface.keycloak.eventlistenerspi;

import org.keycloak.Config.Scope;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class CustomEventListenerProviderFactory implements EventListenerProviderFactory {

	@Override
	public String getId() {
		return "smartface-event-listener";
	}

	@Override
	public EventListenerProvider create(KeycloakSession session) {
		return new CustomEventListenerProvider(session);
	}

	@Override
	public void init(Scope config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
