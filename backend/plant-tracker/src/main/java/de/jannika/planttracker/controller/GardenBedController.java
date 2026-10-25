package de.jannika.planttracker.controller;

import java.net.URI;
import java.util.List;
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

import de.jannika.planttracker.model.GardenBed;
import de.jannika.planttracker.service.GardenBedService;

/**
 * REST Controller providing API endpoints for managing GardenBed entities.
 * Handles HTTP requests and coordinates operations with the underlying GardenBedService.
 */

@RestController
@RequestMapping("/gardenbeds")
public class GardenBedController {

    private final GardenBedService gardenBedService;
    private final URI baseURL = URI.create("/plants/");

    public GardenBedController(GardenBedService gardenBedService) {
        this.gardenBedService = gardenBedService;
    }

    @GetMapping()
    public ResponseEntity<List<GardenBed>> getAllGardenBeds() {
        return ResponseEntity.ok(gardenBedService.getAllGardenBeds());
    }

    @GetMapping("/{gardenBedId}")
    public ResponseEntity<GardenBed> getGardenBed(@PathVariable Integer gardenBedId) {
        return ResponseEntity.ok(gardenBedService.getGardenBed(gardenBedId));
    }

    @PostMapping()
    public ResponseEntity<Void> createGardenBed(@RequestBody GardenBed gardenBed) {
        gardenBedService.createGardenBed(gardenBed);
        return ResponseEntity.created(baseURL).build();
    }

    @PutMapping("/{gardenBedId}/rename")
    public ResponseEntity<Void> renameGardenBed(
            @PathVariable Integer gardenBedId, 
            @RequestParam String name) {
        gardenBedService.renameGardenBed(gardenBedId, name);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{gardenBedId}/move")
    public ResponseEntity<Void> moveGardenBed(
            @PathVariable Integer gardenBedId, 
            @RequestParam String location) {
        gardenBedService.moveGardenBed(gardenBedId, location);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{gardenBedId}/roof")
    public ResponseEntity<Void> roofAboveGardenBed(
            @PathVariable Integer gardenBedId, 
            @RequestParam Boolean roof) {
        gardenBedService.roofAboveGardenBed(gardenBedId, roof);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{gardenBedId}/sunlight")
    public ResponseEntity<Void> sunlightAboveGardenBed(
            @PathVariable Integer gardenBedId, 
            @RequestParam Integer sunlight) {
        gardenBedService.sunlightAboveGardenBed(gardenBedId, sunlight);
        return ResponseEntity.noContent().build();

    }

    @PutMapping("/{gardenBedId}/watered")
    public ResponseEntity<Void> waterGardenBed(
            @PathVariable Integer gardenBedId) {
        gardenBedService.waterGardenBed(gardenBedId);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{gardenBedId}")
    public ResponseEntity<Void> deleteGardenBed(
            @PathVariable Integer gardenBedId) {
        gardenBedService.deleteGardenBed(gardenBedId);
        return ResponseEntity.noContent().build();

    }



}
