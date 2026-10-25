package de.jannika.planttracker.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import de.jannika.planttracker.model.Weather;

/**
 * Repository class handling CRUD operations for the Weather entity using JdbcTemplate.
 */

@Repository
public class WeatherRepository {

    private final JdbcTemplate jdbcTemplate;
    
    public WeatherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Check if weather logs already exist for this day (from time range 00:00:00 to 23:59:59)
    public boolean existsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        String query = "SELECT COUNT(*) FROM  jaboit02_Weather WHERE weatherDate BETWEEN ? AND ?;";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, startOfDay, endOfDay);
        return count != null && count > 0;
    }

    public void save(Weather weather) {
        String query = "INSERT INTO  jaboit02_Weather (weatherDate, temperature, precipitation, cloudCover) "
                    + "VALUES (?, ?, ?, ?);";
                    
        jdbcTemplate.update(query, 
            weather.getWeatherDate(), 
            weather.getTemperature(), 
            weather.getPrecipitation(), 
            weather.getCloudCover()
        );
    }
    
}
