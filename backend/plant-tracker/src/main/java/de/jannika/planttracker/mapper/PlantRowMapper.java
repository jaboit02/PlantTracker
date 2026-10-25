package de.jannika.planttracker.mapper;

import de.jannika.planttracker.enums.PlantLocation;
import de.jannika.planttracker.enums.PlantType;
import de.jannika.planttracker.model.Plant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Data mapping utility that translates SQL ResultSet rows into Plant model objects.
 * Implements Spring's RowMapper interface for clean relational-to-object extraction.
 * 
 * @note The mapping structure was initially generated using an LLM (Gemini/ChatGPT) in summer 2026 
 *       to serve as a structural implementation reference. It was subsequently refined 
 *       manually, adding custom defensive null-handling for database date conversions.
 */

public class PlantRowMapper implements RowMapper<Plant> {
    
    // LLM GENERATED: To get an example how RowMapper can be implemented
    @Override
    public Plant mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new Plant(
                rs.getInt("plantId"),
                rs.getString("plantName"),
                convertToLocalDate(rs.getDate("plantCreatedAt")),
                rs.getBoolean("plantAlive"),
                PlantType.valueFromDbToEnum(rs.getString("plantType")),
                rs.getBoolean("plantOutdoor"),
                PlantLocation.valueFromDbToEnum(rs.getString("plantLocation")),
                rs.getInt("lightNeeded"),
                rs.getInt("waterNeeded"),
                convertToLocalDate(rs.getDate("lastWatered")),
                convertToLocalDate(rs.getDate("lastRepotted")),
                convertToLocalDate(rs.getDate("lastFertilized")),
                convertToLocalDate(rs.getDate("waterNext")) 
        );
    }

    // Convert to data type date if null is returned from DB
    public LocalDate convertToLocalDate (Date date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate();
    }
}