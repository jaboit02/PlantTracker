package de.jannika.planttracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import de.jannika.planttracker.mapper.HarvestRowMapper;
import de.jannika.planttracker.model.Harvest;

/**
 * Repository class handling CRUD operations for the PlantOutput entity using JdbcTemplate.
 * 
 * @note This repository was built manually, but an LLM (Gemini/ChatGPT) was utilized in summer 2026 
 *       to resolve a specific DATETIME2 format compatibility bug within the 
 *       {@code createHarvest} transaction logic.
 */

@Repository
public class PlantOutputRepository {

    private final JdbcTemplate jdbcTemplate;
    public PlantOutputRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<Harvest> findAllHarvests () {
        return jdbcTemplate.query(
            "SELECT * FROM  jaboit02_View_Harvest",
            new HarvestRowMapper()
        );

    }

    public List<Harvest> findAllHarvestsForPlant (Integer plantId) {
        return jdbcTemplate.query(
            "SELECT * FROM  jaboit02_View_Harvest WHERE plantId = ?",
            new HarvestRowMapper(),
            plantId
        );

    }

    public void createHarvest (Integer plantId, LocalDateTime outputDate, Integer outputAmount) {
        java.time.LocalDateTime finalDateTime;

        // LLM BUG FIX: (Due to Issues with DATETIME2 Format)
        // If no date was provided at all, fallback to the current system timestamp
        if (outputDate == null) {
            finalDateTime = java.time.LocalDateTime.now();
        } else {
            // Extract only the calendar day (LocalDate) for comparison
            java.time.LocalDate chosenDate = outputDate.toLocalDate();
            java.time.LocalDate today = java.time.LocalDate.now();

            // If the selected day matches today, use NOW (captures the real, current time)
            if (chosenDate.equals(today)) {
                finalDateTime = java.time.LocalDateTime.now();
            } else {
                // If it's a past or future date, preserve the user's explicit selection
                finalDateTime = outputDate;
            }
        }

        // Truncate milliseconds/nanoseconds to match with DATETIME2 
        java.time.LocalDateTime safeDate = finalDateTime.withNano(0);
                
        String query = "INSERT INTO  jaboit02_PlantOutput "
        + "(plantId, outputDate, outputAmount) "
        + "VALUES (?, ?, ?)";

        jdbcTemplate.update(query, plantId, safeDate, outputAmount);
    }

    public void updatePlantOutputAmount (Integer plantOutputId, Integer outputAmount) {
        String query = "UPDATE  jaboit02_PlantOutput "
        + "SET outputAmount = ? "
        + "WHERE plantOutputId = ? ;";

        jdbcTemplate.update(query, outputAmount, plantOutputId);
    }

    public boolean deletePlantOutput (Integer plantOutputId) {
        String deletePlantQuery = "DELETE FROM  jaboit02_PlantOutput "
        + "WHERE plantOutputId = ? ;";

        Integer rowsAffected = jdbcTemplate.update(deletePlantQuery, plantOutputId);
        if (rowsAffected > 0) {
            return true;
        }
        return false;
    }


}
