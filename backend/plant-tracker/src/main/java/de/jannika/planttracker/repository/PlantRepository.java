package de.jannika.planttracker.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import de.jannika.planttracker.model.Plant;
import de.jannika.planttracker.enums.PlantLocation;
import de.jannika.planttracker.enums.PlantType;
import de.jannika.planttracker.mapper.PlantRowMapper;

/**
 * Repository class handling CRUD operations for the Plant entity using JdbcTemplate.
 * 
 * @note This class was written manually, but an LLM (Gemini/ChatGPT) was used in summer 2026 
 *       as an assistant to refine SQL formatting, optimize query layouts, 
 *       and ensure safe null-parameter handling during object mapping.
 */

@Repository
public class PlantRepository {

    private final JdbcTemplate jdbcTemplate;
    
    public PlantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Plant> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM  jaboit02_Plant",
            new PlantRowMapper()
        );
    }

    public Plant findById(Integer plantId) {

        String query = "SELECT * FROM  jaboit02_Plant WHERE plantId = " + plantId.toString() + " ;";
        List<Plant> plants = jdbcTemplate.query(
            query,
            new PlantRowMapper()
        );
        if (plants.isEmpty()) {
            return null;
        } else  {
            return plants.getFirst();
        }
    }

    public void createPlant(
        String plantName, 
        PlantType plantType, 
        Boolean plantOutdoor, 
        PlantLocation plantLocation, 
        Integer lightNeeded, 
        Integer waterNeeded, 
        LocalDate lastWatered, 
        LocalDate lastRepotted, 
        LocalDate lastFertilized) {
        

        String query = "INSERT INTO  jaboit02_Plant (" // LLM USAGE: Clean up query formatting
            + "plantName, plantType, plantOutdoor, plantLocation, "
            + "lightNeeded, waterNeeded, lastWatered, lastRepotted, lastFertilized) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        jdbcTemplate.update(
            query,
            // LLM USAGE: Safe handling of null parameters added/improved
            plantName,
            plantType != null ? plantType.name() : null, 
            plantOutdoor,
            plantLocation != null ? plantLocation.name() : null,
            lightNeeded,
            waterNeeded,
            lastWatered != null ? java.sql.Date.valueOf(lastWatered) : null, 
            lastRepotted != null ? java.sql.Date.valueOf(lastRepotted) : null,
            lastFertilized != null ? java.sql.Date.valueOf(lastFertilized) : null
        );

        return;
    }

    public void updatePlantName(Integer plantId, String plantName) { 
        String query = "UPDATE  jaboit02_Plant SET plantName = ? WHERE plantId = ?;";
        jdbcTemplate.update(query, plantName, plantId);
    }

    public void updatePlantAlive(Integer plantId, Boolean plantAlive) { 
        String query = "UPDATE  jaboit02_Plant SET plantAlive = ? WHERE plantId = ?;";
        jdbcTemplate.update(query, plantAlive, plantId);
    }

    public void updatePlantType(Integer plantId, String plantTypeAsString) { 
        PlantType plantType = PlantType.fromStringToPlantType(plantTypeAsString);
        String query = "UPDATE  jaboit02_Plant SET plantType = ? WHERE plantId = ?;";
        jdbcTemplate.update(query, plantType, plantId);
    }

    public void updatePlantOutdoor(Integer plantId, Boolean plantOutdoor) { 
        String query = "UPDATE  jaboit02_Plant SET plantOutdoor = ? WHERE plantId = ?;";
        jdbcTemplate.update(query, plantOutdoor, plantId);
    }

    public void updatePlantLocation(Integer plantId, String plantLocationAsString) { 
        PlantLocation plantLocation = PlantLocation.fromStringToPlantLocation(plantLocationAsString);
        String query = "UPDATE  jaboit02_Plant SET plantLocation = ? WHERE plantId = ?;";
        jdbcTemplate.update(query, plantLocation.name(), plantId);
    }

    public void updateLightNeeded(Integer plantId, Integer lightNeeded) {        
        String query = "UPDATE  jaboit02_Plant SET lightNeeded = ? WHERE plantId = ?;";
        jdbcTemplate.update(query, lightNeeded, plantId);
    }

    public void updateWaterNeeded(Integer plantId, Integer waterNeeded) {        
        String query = "UPDATE  jaboit02_Plant SET waterNeeded = ? WHERE plantId = ?;";
        jdbcTemplate.update(query, waterNeeded, plantId);
    }

    public void updateLastWatered(Integer plantId) {
        Date currentDate = new java.sql.Date(System.currentTimeMillis());
        String query = "UPDATE  jaboit02_Plant SET lastWatered = ? WHERE plantId = ?;";
        jdbcTemplate.update(query, currentDate, plantId);
    }

    public void updateLastRepotted(Integer plantId) {
        Date currentDate = new java.sql.Date(System.currentTimeMillis());
        String query = "UPDATE  jaboit02_Plant SET lastRepotted = ? WHERE plantId = ?;";
        jdbcTemplate.update(query, currentDate, plantId);
    }

    public void updateLastFertilized(Integer plantId) {
        Date currentDate = new java.sql.Date(System.currentTimeMillis());
        String query = "UPDATE  jaboit02_Plant SET lastFertilized = ? WHERE plantId = ?;";
        jdbcTemplate.update(query, currentDate, plantId);
    }

    public boolean deletePlant(Integer plantId) { 
        String deletePlantOutputsQuery = "DELETE FROM  jaboit02_PlantOutput WHERE plantId = ?;";
        jdbcTemplate.update(deletePlantOutputsQuery, plantId);

        String deletePlantQuery = "DELETE FROM  jaboit02_Plant WHERE plantId = ?;";
        Integer rowsAffected = jdbcTemplate.update(deletePlantQuery, plantId);
        if (rowsAffected > 0) {
            return true;
        }
        return false;
    }

}