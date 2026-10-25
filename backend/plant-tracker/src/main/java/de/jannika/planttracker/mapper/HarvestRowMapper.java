package de.jannika.planttracker.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;

import de.jannika.planttracker.enums.GardenBedLocation;
import de.jannika.planttracker.enums.PlantLocation;
import de.jannika.planttracker.enums.PlantType;
import de.jannika.planttracker.model.Harvest;
/**
 * Data mapping utility that translates SQL ResultSet rows into Harvest model objects.
 * Extends the project's data architecture for specialized plant tracking.
 * 
 * @note This class was authored manually, following the established RowMapper structural 
 *       patterns implemented across the codebase. No direct AI generation was used.
 */

public class HarvestRowMapper implements RowMapper<Harvest> {

    @Override
    public Harvest mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Harvest(
            rs.getInt("plantId"),
            rs.getString("plantName"),
            PlantType.valueFromDbToEnum(rs.getString("plantType")),
            PlantLocation.valueFromDbToEnum(rs.getString("plantLocation")),
            
            rs.getInt("gardenBedId"),
            rs.getString("gardenBedName"),
            GardenBedLocation.valueFromDbToEnum(rs.getString("gardenBedLocation")),
            
            rs.getInt("plantOutputId"),
            convertToLocalDate(rs.getDate("harvestDate")),
            rs.getInt("harvestAmount")
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
