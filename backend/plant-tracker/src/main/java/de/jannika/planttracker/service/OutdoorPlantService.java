package de.jannika.planttracker.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import de.jannika.planttracker.model.GardenBed;
import de.jannika.planttracker.model.OutdoorPlant;
import de.jannika.planttracker.model.OutdoorPlantComplete;
import de.jannika.planttracker.model.Plant;
import de.jannika.planttracker.repository.GardenBedRepository;
import de.jannika.planttracker.repository.OutdoorPlantRepository;
import de.jannika.planttracker.repository.PlantRepository;

/**
 * Service class handling business logic and orchestration for OutdoorPlant entities.
 * Acts as the bridge between the controller layer and the data access layer.
 */

@Service
public class OutdoorPlantService {

    private final OutdoorPlantRepository outdoorPlantRepository;
    private final PlantRepository plantRepository;
    private final GardenBedRepository gardenBedRepository;

    public OutdoorPlantService(
            OutdoorPlantRepository outdoorPlantRepository,
            PlantRepository plantRepository, 
            GardenBedRepository gardenBedRepository) {
        this.outdoorPlantRepository = outdoorPlantRepository;
        this.plantRepository = plantRepository;
        this.gardenBedRepository = gardenBedRepository;
    }

    private List<OutdoorPlantComplete> convertOutdoorPlantsToOutdoorPlantsComplete(List<OutdoorPlant> outdoorPlants) {
        List<OutdoorPlantComplete> outdoorPlantsComplete = new ArrayList<>();
        for (OutdoorPlant outdoorPlant : outdoorPlants) {
            Plant plant = plantRepository.findById(outdoorPlant.getPlantId());
            GardenBed gardenBed = gardenBedRepository.findById(outdoorPlant.getGardenBedId());
            OutdoorPlantComplete outdoorPlantComplete = new OutdoorPlantComplete(plant, gardenBed);
            outdoorPlantsComplete.add(outdoorPlantComplete);
        }
        return outdoorPlantsComplete;
    }

    private OutdoorPlantComplete convertOutdoorPlantsToOutdoorPlantsComplete(OutdoorPlant outdoorPlant) {
        Plant plant = plantRepository.findById(outdoorPlant.getPlantId());
        GardenBed gardenBed = gardenBedRepository.findById(outdoorPlant.getGardenBedId());
        OutdoorPlantComplete outdoorPlantComplete = new OutdoorPlantComplete(plant, gardenBed);
        return outdoorPlantComplete;
    }

    public List<OutdoorPlantComplete> getAllOutdoorPlants() {
        List<OutdoorPlant> outdoorPlants =  outdoorPlantRepository.findAll();
        List<OutdoorPlantComplete> outdoorPlantsComplete = convertOutdoorPlantsToOutdoorPlantsComplete(outdoorPlants);
        return outdoorPlantsComplete;
    }
    

    public OutdoorPlantComplete getOutdoorPlantsByPlantId(Integer plantId) {
        OutdoorPlant outdoorPlant = outdoorPlantRepository.findByPlantId(plantId);
        OutdoorPlantComplete outdoorPlantComplete = convertOutdoorPlantsToOutdoorPlantsComplete(outdoorPlant);
        return outdoorPlantComplete;
    }

    public List<OutdoorPlantComplete> getAllOutdoorPlantsByGardenBedId(Integer gardenBedId) {
        List<OutdoorPlant> outdoorPlants =   outdoorPlantRepository.findAllByGardenBedId(gardenBedId);
        List<OutdoorPlantComplete> outdoorPlantsComplete = convertOutdoorPlantsToOutdoorPlantsComplete(outdoorPlants);
        return outdoorPlantsComplete;
    }

    public List<Plant> getAllPlantsInGardenBed(Integer gardenBedId) {
        List<Plant> plants = new ArrayList<>();
        List<OutdoorPlant> outdoorPlants = outdoorPlantRepository.findAllByGardenBedId(gardenBedId);
        
        if (outdoorPlants == null) {
            return plants;
        }
        for (OutdoorPlant outdoorPlant : outdoorPlants) {
            Integer plantId = outdoorPlant.getPlantId();
            Plant plant = plantRepository.findById(plantId);
            if (plant != null 
                && (plant.getPlantAlive() == null || plant.getPlantAlive())) {
                
                    plants.add(plant);
            }
        }
        return plants;         
    }

    public void createOutdoorPlant(OutdoorPlant outdoorPlant) {
        // Validate Ids to existing Plants and GardenBeds
        Integer plantId = outdoorPlant.getPlantId();
        Integer gardenBedId = outdoorPlant.getGardenBedId();

        if (plantId == null || gardenBedId == null) {
            throw new IllegalArgumentException("Plant " + plantId + " or GardenBed " + gardenBedId + " not found.");
        }

        // Create OutdoorPlant
        outdoorPlantRepository.createOutdoorPlant(plantId, gardenBedId);
        return;
    }

    public boolean deleteOutdoorPlant(Integer plantId, Integer gardenBedId) {
        return outdoorPlantRepository.deleteOutdoorPlant(plantId, gardenBedId);
    }
}
