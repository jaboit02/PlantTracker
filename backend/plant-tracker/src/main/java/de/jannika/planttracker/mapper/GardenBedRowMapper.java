package de.jannika.planttracker.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.springframework.jdbc.core.RowMapper;

import de.jannika.planttracker.enums.GardenBedLocation;
import de.jannika.planttracker.model.GardenBed;

/**
 * Data mapping utility that translates SQL ResultSet rows into GardenBed model objects.
 * Extends the project's data architecture for specialized plant tracking.
 * 
 * @note This class was authored manually, following the established RowMapper structural 
 *       patterns implemented across the codebase. No direct AI generation was used.
 */

public class GardenBedRowMapper implements RowMapper<GardenBed> {
    
    @Override
    public GardenBed mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new GardenBed(
                rs.getInt("gardenBedId"),
                rs.getString("gardenBedName"),
                GardenBedLocation.valueFromDbToEnum(rs.getString("gardenBedLocation")),
                rs.getBoolean("roofIsPresent"),
                rs.getInt("sunlightAvailable"),
                convertToLocalDate(rs.getDate("lastWatered"))
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
