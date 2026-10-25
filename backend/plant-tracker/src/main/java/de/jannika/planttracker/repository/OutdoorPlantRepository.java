package de.jannika.planttracker.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import de.jannika.planttracker.mapper.OutdoorPlantRowMapper;
import de.jannika.planttracker.model.OutdoorPlant;

/**
 * Repository class handling CRUD operations for the OutdoorPlant entity using JdbcTemplate.
 */

@Repository
public class OutdoorPlantRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    
    public OutdoorPlantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OutdoorPlant> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM   jaboit02_OutdoorPlant",
            new OutdoorPlantRowMapper()
        );
    }
    
    public OutdoorPlant findByPlantId(Integer plantId) {

        String query = "SELECT * FROM   jaboit02_OutdoorPlant WHERE plantId = " + plantId.toString() + " ;";
        List<OutdoorPlant> outdoorPlant = jdbcTemplate.query(
            query,
            new OutdoorPlantRowMapper()
        );
        if (outdoorPlant.isEmpty()) {
            return null;
        } else  {
            return outdoorPlant.getFirst();
        }
    }

    public List<OutdoorPlant> findAllByGardenBedId(Integer gardenBedId) {

        String query = "SELECT * FROM   jaboit02_OutdoorPlant WHERE gardenBedId = " + gardenBedId.toString() + " ;";
        List<OutdoorPlant> outdoorPlant = jdbcTemplate.query(
            query,
            new OutdoorPlantRowMapper()
        );
        if (outdoorPlant.isEmpty()) {
            return null;
        } else  {
            return outdoorPlant;
        }
    } 

    public void createOutdoorPlant(Integer plantId, Integer gardenBedId) {

        String query = "INSERT INTO   jaboit02_OutdoorPlant (" 
            + "plantId, gardenBedId) "
            + "VALUES (?, ?);";

        jdbcTemplate.update(
            query,
            plantId,
            gardenBedId
        );
    }

    public Boolean deleteOutdoorPlant(Integer plantId, Integer gardenBedId) {
        String query = "DELETE FROM   jaboit02_OutdoorPlant "
            + "WHERE plantId = ? " 
            + "AND gardenBedId = ?";

        Integer rowsAffected = jdbcTemplate.update(
            query,
            plantId,
            gardenBedId
        );

        if (rowsAffected > 0) {
            return true;
        }
        return false;
    }
}
