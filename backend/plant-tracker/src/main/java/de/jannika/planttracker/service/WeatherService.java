package de.jannika.planttracker.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.jannika.planttracker.model.Weather;
import de.jannika.planttracker.repository.WeatherRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Service for synchronizing weather data with the Open-Meteo API (see https://open-meteo.com/en/docs).
 * 
 * @note The initial scaffolding and API integration of this service were generated 
 *       using an LLM (ChatGPT or Gemini) in summer (June/July) 2026. 
 *       The code was subsequently manually reviewed, refined, and adapted for the repository.
 */

@Service
public class WeatherService {

    // Read values from .env
    @Value("${weather.latitude}")
    private String LATITUDE;

    @Value("${weather.longitude}")
    private String LONGITUDE;

    @Value("${weather.api.url}")
    private String API_URL;

    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    // Check if Weather for this date is already in database
    public boolean hasWeatherDataForDate(LocalDate date) {
        return weatherRepository.existsByDate(date);
    }

    // Fetches pre-aggregated daily telemetry summaries from the Open-Meteo Forecast endpoint for a specific date
    public void syncHistoricalWeather(LocalDate date) {

        // URL configured to query daily calculations: maximum temperature, total rainfall sum, and mean cloud cover
        String url = API_URL
        +"?latitude="+LATITUDE
        +"&longitude="+LONGITUDE
        +"&daily=temperature_2m_max,rain_sum,cloud_cover_mean&timezone=auto"
        +"&start_date="+date.toString() // Should only retrieve info for a single day --> start_date = end_date
        +"&end_date="+date.toString();
        
        try {
            // Fetch raw JSON response payload string from Open-Meteo REST service
            String jsonResponse = restTemplate.getForObject(url, String.class);
            
            // Parse text tree mapping structure into readable JSON node references
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode daily = root.path("daily");

            // Isolate pre-calculated tracking metric value collection arrays
            JsonNode tempsMaxArray = daily.path("temperature_2m_max");
            JsonNode rainSumArray = daily.path("rain_sum");
            JsonNode cloudsMeanArray = daily.path("cloud_cover_mean");

            // Verify arrays exist and hold data before pulling the values at index 0 (representing the target day)
            if (tempsMaxArray.isArray() && tempsMaxArray.size() > 0) {
                
                // Extract metrics directly without running resource-heavy aggregation calculation loops
                float maxDaytimeTemperature = (float) tempsMaxArray.get(0).asDouble();
                float totalPrecipitation = (float) rainSumArray.get(0).asDouble();
                int averageDaytimeCloudCover = cloudsMeanArray.get(0).asInt();

                // Set database timestamp anchor to midnight of the target synchronization day
                LocalDateTime targetMidnightTimestamp = date.atStartOfDay();

                // Instantiate weather model data record parameters
                Weather weather = new Weather(
                    targetMidnightTimestamp, 
                    maxDaytimeTemperature, 
                    totalPrecipitation, 
                    averageDaytimeCloudCover, 
                    false, // Pass placeholders for Lombok; database computed columns auto-evaluate states on insertion
                    false // Pass placeholders for Lombok; database computed columns auto-evaluate states on insertion
                );

                // Commit the finalized values row into database table 
                weatherRepository.save(weather);
                System.out.println("Weather history successfully synchronized for Stuttgart on: " + date);
            } else {
                System.err.println("Open-Meteo API response returned empty daily array blocks for date: " + date);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse or save weather engine stream data for " + date + ": " + e.getMessage());
        }
    }
    
}
