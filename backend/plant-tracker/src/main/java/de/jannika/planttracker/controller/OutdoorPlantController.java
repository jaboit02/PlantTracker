package de.jannika.planttracker.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.jannika.planttracker.model.Plant;
import de.jannika.planttracker.model.OutdoorPlant;
import de.jannika.planttracker.model.OutdoorPlantComplete;
import de.jannika.planttracker.service.OutdoorPlantService;

/**
 * REST Controller providing API endpoints for managing OutdoorPlants.
 * Coordinates data transfers between the frontend and the OutdoorPlantService layer.
 */

@RestController
@RequestMapping("/outdoorplants")
public class OutdoorPlantController {

    private final OutdoorPlantService outdoorPlantService;
    private final URI baseURL = URI.create("/plants/");

    public OutdoorPlantController(OutdoorPlantService outdoorPlantService) {
        this.outdoorPlantService = outdoorPlantService;
    }
    
    @GetMapping()
    public ResponseEntity<List<OutdoorPlantComplete>> getAllOutdoorPlants() {
        return ResponseEntity.ok(outdoorPlantService.getAllOutdoorPlants());
    }

    @GetMapping("/plant/{plantId}")
    public ResponseEntity<OutdoorPlantComplete> getOutdoorPlantByPlantId(@PathVariable Integer plantId) {
        return ResponseEntity.ok(outdoorPlantService.getOutdoorPlantsByPlantId(plantId));
    }

    @GetMapping("/gardenBed/{gardenBedId}")
    public ResponseEntity<List<OutdoorPlantComplete>> getOutdoorPlantsByGardenBedId(@PathVariable Integer gardenBedId) {
        return ResponseEntity.ok(outdoorPlantService.getAllOutdoorPlantsByGardenBedId(gardenBedId));
    }

    @GetMapping("/plantsInGardenBed/{gardenBedId}")
    public ResponseEntity<List<Plant>> getPlantsByGardenBedId(@PathVariable Integer gardenBedId) {
        return ResponseEntity.ok(outdoorPlantService.getAllPlantsInGardenBed(gardenBedId));
    }

    @PostMapping()
    public ResponseEntity<Void> createOutdoorPlant(@RequestBody OutdoorPlant outdoorPlant) {
        outdoorPlantService.createOutdoorPlant(outdoorPlant);
        return ResponseEntity.created(baseURL).build();
    }

    @DeleteMapping("/{plantId}/{gardenBedId}")
    public ResponseEntity<Void> deleteOutdoorPlant(@PathVariable Integer plantId, @PathVariable Integer gardenBedId) {
        outdoorPlantService.deleteOutdoorPlant(plantId, gardenBedId);
        return ResponseEntity.noContent().build();
    }


}
