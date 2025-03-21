/**
 * 
 */
package com.smartface.keycloak.eventlistenerspi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
	//These environments are defined in the keycloak-deployment.yaml file.
	private static String KC_DB_URL = System.getenv("KC_DB_URL");
	private static String KC_DB_USERNAME = System.getenv("KC_DB_USERNAME");
	private static String KC_DB_PASSWORD = System.getenv("KC_DB_PASSWORD");

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(KC_DB_URL, KC_DB_USERNAME, KC_DB_PASSWORD);
	}

}
