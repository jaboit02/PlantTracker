package de.jannika.planttracker.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import de.jannika.planttracker.model.Harvest;
import de.jannika.planttracker.model.PlantOutput;
import de.jannika.planttracker.repository.PlantOutputRepository;

/**
 * Service class handling business logic and orchestration for Plantoutput (=Harvest) entities.
 * Acts as the bridge between the controller layer and the data access layer.
 */

@Service
public class PlantOutputService {

    public final PlantOutputRepository plantOutputRepository;

    public PlantOutputService(PlantOutputRepository plantOutputRepository) {
        this.plantOutputRepository = plantOutputRepository;
    }

    public List<Harvest> getAllHarvests() {
        return plantOutputRepository.findAllHarvests();
    }

    public List<Harvest> getAllHarvestsForPlant(Integer plantId) {
        return plantOutputRepository.findAllHarvestsForPlant(plantId);

    }

    public void createHarvest(@RequestBody PlantOutput plantOutput) {
        Integer plantId = plantOutput.getPlantId();
         java.time.LocalDateTime outputDate = plantOutput.getOutputDate(); 
        Integer outputAmount = plantOutput.getOutputAmount();

        plantOutputRepository.createHarvest(plantId, outputDate, outputAmount);
        return;

    }

    public void changeHarvestAmount(@RequestBody PlantOutput plantOutput) {
        Integer plantOutputId = plantOutput.getPlantOutputId();
        Integer outputAmount = plantOutput.getOutputAmount();

        plantOutputRepository.updatePlantOutputAmount(plantOutputId, outputAmount);
    }

    public void deleteHarvest(@PathVariable Integer plantOutputId) {
        plantOutputRepository.deletePlantOutput(plantOutputId);
    }

    
}
