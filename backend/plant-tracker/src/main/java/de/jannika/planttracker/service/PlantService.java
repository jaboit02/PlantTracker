package de.jannika.planttracker.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import de.jannika.planttracker.enums.PlantLocation;
import de.jannika.planttracker.enums.PlantType;
import de.jannika.planttracker.model.Plant;
import de.jannika.planttracker.repository.PlantRepository;
import de.jannika.planttracker.validation.PlantValidaton;

/**
 * Service class handling business logic and orchestration for Plant entities.
 * Acts as the bridge between the controller layer and the data access layer.
 */

@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final PlantValidaton plantValidaton;

    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
        this.plantValidaton = new PlantValidaton();
    }

    public List<Plant> getAllPlants() {

        return plantRepository.findAll();
    }

    public Plant getPlant(Integer plantId) {
        return plantRepository.findById(plantId);
    }

    public void createPlant(Plant plant) {
        String plantName = plant.getPlantName(); 
        PlantType plantType = plant.getPlantType();
        Boolean plantOutdoor = plant.getPlantOutdoor();
        PlantLocation plantLocation = plant.getPlantLocation();
        Integer lightNeeded = plant.getLightNeeded(); 
        Integer waterNeeded = plant.getWaterNeeded();
        LocalDate lastWatered = plant.getLastWatered();
        LocalDate lastRepotted = plant.getLastRepotted();
        LocalDate lastFertilized = plant.getLastFertilized();
        // Validation
        plantValidaton.validatePlant(
            plantName, 
            plantType, 
            plantOutdoor, 
            plantLocation, 
            lightNeeded, 
            waterNeeded, 
            lastWatered, 
            lastRepotted, 
            lastFertilized);

        plantRepository.createPlant(
            plantName, 
            plantType, 
            plantOutdoor, 
            plantLocation, 
            lightNeeded, 
            waterNeeded, 
            lastWatered, 
            lastRepotted, 
            lastFertilized);

        return;
    }
    
    public void renamePlant(Integer plantId, String plantName) { 
        plantRepository.updatePlantName(plantId, plantName);
        return;
    }

    public void deceasePlant(Integer plantId) {
        plantRepository.updatePlantAlive(plantId, false);
        return;
    }

    public void changePlantType(Integer plantId, String plantTypeAsString) { 
        plantRepository.updatePlantType(plantId, plantTypeAsString);
        return;
    }

    public void plantWasMoved(Integer plantId, Boolean plantOutdoor, String plantLocationAsString) { 
        // If plant was moved, change flag accordingly 
        if ("Indoor".equalsIgnoreCase(plantLocationAsString)) {
            plantOutdoor = false;
            plantLocationAsString = "Indoor";
        } 
        else if ("Garden".equalsIgnoreCase(plantLocationAsString)) {
            plantOutdoor = true;
            plantLocationAsString = "Garden";
        } 
        else if ("Balcony".equalsIgnoreCase(plantLocationAsString)) {
            plantOutdoor = true; 
            plantLocationAsString = "Balcony";
        }
        
        plantRepository.updatePlantLocation(plantId, plantLocationAsString);
        plantRepository.updatePlantOutdoor(plantId, plantOutdoor);
        return;
    }

    public void changePlantLightNeeded(Integer plantId, Integer lightNeeded) {                
        plantRepository.updateLightNeeded(plantId, lightNeeded);
        return;
    }

    public void changePlantWaterNeeded(Integer plantId, Integer waterNeeded) {        
        plantRepository.updateWaterNeeded(plantId, waterNeeded);
        return;
    }

    public void plantWasWatered(Integer plantId) {
        plantRepository.updateLastWatered(plantId);
        return;
    }

    public void plantWasRepotted(Integer plantId) {
        plantRepository.updateLastRepotted(plantId);
        return;
    }

    public void plantWasFertilized(Integer plantId) {
       plantRepository.updateLastFertilized(plantId);
    return;
    }

    public Boolean deletePlant(Integer plantId) {
        return plantRepository.deletePlant(plantId);
    }

}
