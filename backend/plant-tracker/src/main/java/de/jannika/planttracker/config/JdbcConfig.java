package de.jannika.planttracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Configuration class for setting up core JDBC infrastructure components.
 * Registers the central {@link JdbcTemplate} bean used throughout the data access layer.
 * 
 * @note This is standard infrastructure boilerplate code that was fully generated 
 *       using an LLM (ChatGPT) in summer 2026.
 */

@Configuration
public class JdbcConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}