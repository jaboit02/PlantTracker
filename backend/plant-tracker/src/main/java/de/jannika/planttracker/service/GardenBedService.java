package de.jannika.planttracker.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import de.jannika.planttracker.enums.GardenBedLocation;
import de.jannika.planttracker.model.GardenBed;
import de.jannika.planttracker.repository.GardenBedRepository;

/**
 * Service class handling business logic and orchestration for GardenBed entities.
 * Acts as the bridge between the controller layer and the data access layer.
 */

@Service
public class GardenBedService {
    private final GardenBedRepository gardenBedRepository;

    public GardenBedService(GardenBedRepository gardenBedRepository) {
        this.gardenBedRepository = gardenBedRepository;
    }

    public List<GardenBed> getAllGardenBeds() {

        return gardenBedRepository.findAll();
    }

    public GardenBed getGardenBed(Integer gardenBedId) {
        return gardenBedRepository.findById(gardenBedId);
    }

    public void createGardenBed(GardenBed gardenBed) {
        final String gardenBedName = gardenBed.getGardenBedName();
        final GardenBedLocation gardenBedLocation = gardenBed.getGardenBedLocation();
        final Boolean roofisPresent = gardenBed.getRoofIsPresent();
        final Integer sunlightAvailable = gardenBed.getSunlightAvailable();
        final LocalDate lastWatered = gardenBed.getLastWatered();

        gardenBedRepository.createGardenBed(
            gardenBedName, 
            gardenBedLocation, 
            roofisPresent, 
            sunlightAvailable, 
            lastWatered);

        return;
    }

    public void renameGardenBed(Integer gardenBedId, String gardenBedName) {
        gardenBedRepository.updateGardenBedName(gardenBedId, gardenBedName);
        return;
    }

    public void moveGardenBed(Integer gardenBedId, String gardenBedLocationAsString) {
        gardenBedRepository.updateGardenBedLocation(gardenBedId, gardenBedLocationAsString);
        return;
    }

    public void roofAboveGardenBed(Integer gardenBedId, Boolean roofIsPresent) {
        gardenBedRepository.updateGardenBedRoofIsPresent(gardenBedId, roofIsPresent);
        return;
    }

    public void sunlightAboveGardenBed(Integer gardenBedId, Integer sunlightAvailable) {
        gardenBedRepository.updateGardenBedSunlightAvailable(gardenBedId, sunlightAvailable);
        return;
    }

    public void waterGardenBed(Integer gardenBedId) {
        gardenBedRepository.updateLastWatered(gardenBedId);
        return;
    }

    public Boolean deleteGardenBed(Integer gardenBedId) {
        return gardenBedRepository.deleteGardenBed(gardenBedId);
    }
}
