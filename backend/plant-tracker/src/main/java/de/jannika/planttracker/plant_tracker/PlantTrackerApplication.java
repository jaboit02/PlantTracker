package de.jannika.planttracker.plant_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Central entry point for the PlantTracker Spring Boot application.
 * Configures component scanning and bootstraps the entire application context.
 * 
 * @note This is standard Spring Boot infrastructure code.
 */

@SpringBootApplication(scanBasePackages = "de.jannika.planttracker")
public class PlantTrackerApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(PlantTrackerApplication.class, args);
	}

}
