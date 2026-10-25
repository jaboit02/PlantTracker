package de.jannika.planttracker.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import de.jannika.planttracker.model.OutdoorPlant;

/**
 * Data mapping utility that translates SQL ResultSet rows into OutdoorPlant model objects.
 * Extends the project's data architecture for specialized plant tracking.
 * 
 * @note This class was authored manually, following the established RowMapper structural 
 *       patterns implemented across the codebase. No direct AI generation was used.
 */

public class OutdoorPlantRowMapper implements RowMapper<OutdoorPlant>{
    
    @Override
    public OutdoorPlant mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new OutdoorPlant(
                rs.getInt("plantId"),
                rs.getInt("gardenBedId")
        );
    }
}
