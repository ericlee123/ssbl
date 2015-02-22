package com.hunnymustard.ssbl.server.config;

import java.io.IOException;
import java.util.logging.LogManager;

public class LoggingConfig {

	public static void configure() throws SecurityException, IOException {
		LogManager.getLogManager().readConfiguration(LoggingConfig.class.getClassLoader().getResourceAsStream("logging.properties"));
	}
}
