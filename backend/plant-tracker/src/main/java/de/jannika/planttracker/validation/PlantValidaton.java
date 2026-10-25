package de.jannika.planttracker.validation;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import de.jannika.planttracker.enums.PlantLocation;
import de.jannika.planttracker.enums.PlantType;

/**
 * Validation utility for enforcing business rules and data integrity on Plant entities.
 * Throws web-layer exceptions if structural or logical constraints are violated.
 */

public class PlantValidaton {

    public void validatePlant(
        String plantName, 
        PlantType plantType, 
        Boolean plantOutdoor, 
        PlantLocation plantLocation, 
        Integer lightNeeded, 
        Integer waterNeeded, 
        LocalDate lastWatered, 
        LocalDate lastRepotted, 
        LocalDate lastFertilized) {
        
        // Validation
        if (lightNeeded < 1 || lightNeeded > 3) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "A Plant needs either shade (=1) or partial Shade (=2) or direct sunlight (=3).");
        }
        if (waterNeeded < 1 || waterNeeded > 7) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "A Plant cannot be watered less than once a week or more than 7 times a week.");
        }
        if (plantName.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "A Plant needs a name or they will feel unloved and die.");

        }

        return; 
    }
}
