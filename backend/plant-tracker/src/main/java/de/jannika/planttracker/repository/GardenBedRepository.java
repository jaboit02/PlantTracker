package de.jannika.planttracker.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import de.jannika.planttracker.model.GardenBed;
import de.jannika.planttracker.enums.GardenBedLocation;
import de.jannika.planttracker.mapper.GardenBedRowMapper;

/**
 * Repository class handling CRUD operations for the GardenBed entity using JdbcTemplate.
 */

@Repository
public class GardenBedRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public GardenBedRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GardenBed> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM jaboit02_GardenBed",
            new GardenBedRowMapper()
        );
    }

    public GardenBed findById(Integer gardenBedId) {

        String query = "SELECT * FROM jaboit02_GardenBed WHERE gardenBedId = " + gardenBedId.toString() + " ;";
        List<GardenBed> gardenBeds = jdbcTemplate.query(
            query,
            new GardenBedRowMapper()
        );
        if (gardenBeds.isEmpty()) {
            return null;
        } else  {
            return gardenBeds.getFirst();
        }
    }

    public void createGardenBed(
        String gardenBedName, 
        GardenBedLocation gardenBedLocation,
        Boolean roofisPresent,
        Integer sunlightAvailable,
        LocalDate lastWatered
        ) {
        
        String query = "INSERT INTO jaboit02_GardenBed (" 
            + "gardenBedName, gardenBedLocation, roofisPresent, "
            + "sunlightAvailable, lastWatered) "
            + "VALUES (?, ?, ?, ?, ?);";

        jdbcTemplate.update(
            query,
            gardenBedName,
            gardenBedLocation != null ? gardenBedLocation.name() : null, // Bug fixing: handling of null parameters
            roofisPresent,
            sunlightAvailable,
            lastWatered != null ? java.sql.Date.valueOf(lastWatered) : null // Bug fixing: handling of null parameters
        );

        return;
    }

    public void updateGardenBedName(Integer gardenBedId, String gardenBedName) {
        String query = "UPDATE jaboit02_GardenBed SET gardenBedName = ? WHERE gardenBedId = ?;";
        jdbcTemplate.update(query, gardenBedName, gardenBedId);
    }

    public void updateGardenBedLocation(Integer gardenBedId, String gardenBedLocationAsString) {
        GardenBedLocation gardenBedLocation = GardenBedLocation.fromStringToGardenBedLocation(gardenBedLocationAsString);
        String query = "UPDATE jaboit02_GardenBed SET gardenBedLocation = ? WHERE gardenBedId = ?;";
        jdbcTemplate.update(query, gardenBedLocation.name(), gardenBedId);
    }

    public void updateGardenBedRoofIsPresent(Integer gardenBedId, Boolean roofIsPresent) {
        String query = "UPDATE jaboit02_GardenBed SET roofIsPresent = ? WHERE gardenBedId = ?;";
        jdbcTemplate.update(query, roofIsPresent, gardenBedId);
    }

    public void updateGardenBedSunlightAvailable(Integer gardenBedId, Integer sunlightAvailable) {
        String query = "UPDATE jaboit02_GardenBed SET sunlightAvailable = ? WHERE gardenBedId = ?;";
        jdbcTemplate.update(query, sunlightAvailable, gardenBedId);
    }

    public void updateLastWatered(Integer gardenBedId) {
        Date currentDate = new java.sql.Date(System.currentTimeMillis());
        String query = "UPDATE jaboit02_GardenBed SET lastWatered = ? WHERE gardenBedId = ?;";
        jdbcTemplate.update(query, currentDate, gardenBedId);
    }

    public boolean deleteGardenBed(Integer gardenBedId) { 
        String deleteGardenBedQuery = "DELETE FROM jaboit02_GardenBed WHERE gardenBedId = ?;";
        Integer rowsAffected = jdbcTemplate.update(deleteGardenBedQuery, gardenBedId);
        if (rowsAffected > 0) {
            return true;
        }
        return false;
    }
}