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
import org.springframework.web.bind.annotation.RestController;

import de.jannika.planttracker.model.Harvest;
import de.jannika.planttracker.model.PlantOutput;
import de.jannika.planttracker.service.PlantOutputService;

/**
 * REST Controller providing API endpoints for tracking harvest outputs and plant yields.
 * Exposes endpoints to query, create, update, or remove yield records via the PlantOutputService.
 */

@RestController
@RequestMapping("/harvest")
public class PlantOutputController {

    public final PlantOutputService plantOutputService;
    private final URI baseURL = URI.create("/harvest/");

    public PlantOutputController(PlantOutputService plantOutputService) {
        this.plantOutputService = plantOutputService;
    }
    
    @GetMapping() 
    public ResponseEntity<List<Harvest>> getAllHarvests() {
        return ResponseEntity.ok(plantOutputService.getAllHarvests());
    }

    @GetMapping("/plant/{plantId}")
    public ResponseEntity<List<Harvest>> getAllHarvestsForPlant(@PathVariable Integer plantId) {
        return ResponseEntity.ok(plantOutputService.getAllHarvestsForPlant(plantId));
    }

    @PostMapping()
    public ResponseEntity<Void> createHarvest(@RequestBody PlantOutput plantOutput) {
        plantOutputService.createHarvest(plantOutput);
        return ResponseEntity.created(baseURL).build();
    }

    @PutMapping()
    public ResponseEntity<Void> changeHarvestAmount(@RequestBody PlantOutput plantOutput) {
        plantOutputService.changeHarvestAmount(plantOutput);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{plantOutputId}")
    public ResponseEntity<Void> deleteHarvest(@PathVariable Integer plantOutputId) {
        plantOutputService.deleteHarvest(plantOutputId);
        return ResponseEntity.noContent().build();
    }
}
