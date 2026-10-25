package de.jannika.planttracker.controller;

import de.jannika.planttracker.model.*;
import de.jannika.planttracker.service.PlantService;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller providing API endpoints for core Plant operations.
 * Manages client requests for plant lifecycles, maintenance updates, and state changes.
 */

@RestController
@RequestMapping("/plants")
public class PlantController {

    private final PlantService plantService;
    private final URI baseURL = URI.create("/plants/");

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping()
    public ResponseEntity<List<Plant>> getAllPlants() {
        return ResponseEntity.ok(plantService.getAllPlants());
    }

    @GetMapping("/{plantId}")
    public ResponseEntity<Plant> getPlant(@PathVariable Integer plantId) {
        return ResponseEntity.ok(plantService.getPlant(plantId));
    }

    @PostMapping()
    public ResponseEntity<Void> createPlant(@RequestBody Plant plant) {
        plantService.createPlant(plant);
        return ResponseEntity.created(baseURL).build();
    }

    @PutMapping("/{plantId}/rename")
    public ResponseEntity<Void> renamePlant(
            @PathVariable Integer plantId, 
            @RequestParam String name) {
        plantService.renamePlant(plantId, name);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{plantId}/deceased")
    public ResponseEntity<Map<String, String>> deceasedPlant(@PathVariable Integer plantId) {
        plantService.deceasePlant(plantId);
        return ResponseEntity.ok(Map.of("message", "Plant is now deceased. May they decompose in peace, like my sanity."));
    }

    @PutMapping("/{plantId}/type")
    public ResponseEntity<Void> changePlantType(
            @PathVariable Integer plantId, 
            @RequestParam String type) {
        plantService.changePlantType(plantId, type);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{plantId}/moved")
    public ResponseEntity<Void> changePlantLocation(
            @PathVariable Integer plantId, 
            @RequestParam String location,
            @RequestParam Boolean outdoor ) {
        plantService.plantWasMoved(plantId, outdoor, location);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{plantId}/light-needed")
    public ResponseEntity<Void> changePlantLightNeeded(
            @PathVariable Integer plantId, 
            @RequestParam Integer lightNeeded) {
        plantService.changePlantLightNeeded(plantId, lightNeeded);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{plantId}/water-needed")
    public ResponseEntity<Void> changePlantWaterNeeded(
            @PathVariable Integer plantId, 
            @RequestParam Integer waterNeeded) {
        plantService.changePlantWaterNeeded(plantId, waterNeeded);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{plantId}/watered")
    public ResponseEntity<Void> plantWasWatered(@PathVariable Integer plantId) {
        plantService.plantWasWatered(plantId);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

    @PutMapping("/{plantId}/repotted")
    public ResponseEntity<Void> plantWasRepotted(@PathVariable Integer plantId) {
        plantService.plantWasRepotted(plantId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{plantId}/fertilized")
    public ResponseEntity<Void> plantWasFertilized(@PathVariable Integer plantId) {
        plantService.plantWasFertilized(plantId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{plantId}")
    public ResponseEntity<String> deletePlant(@PathVariable Integer plantId) {
        Boolean plantDeleteSuccessful = plantService.deletePlant(plantId);
        if (!plantDeleteSuccessful) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
