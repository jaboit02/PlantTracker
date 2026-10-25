-- ==============================================================================
-- Script Name:  createTriggers.sql
-- Description:  Database triggers for automating and synchronizing 
--               watering events, and dynamically adjusting next watering dates 
--               based on environmental events (like rainfall).
-- 
-- Architecture Timeline & AI Usage:
--   - June 2026:      The entire automation architecture, operational logic, and 
--                     event-driven flows were authored manually. An LLM 
--                     (Gemini) was used to troubleshoot complex T-SQL multi-table 
--                     joins and provide formatting advice.
--   - October 2026:   The manually designed trigger logic was translated and migrated 
--                     to PostgreSQL using Gemini. 
--                     The conversion adapted the architecture to PL/pgSQL function-based 
--                     triggers (using BEFORE/AFTER hooks and RETURNS TRIGGER) and 
--                     safeguarded PostgreSQL's native trigger cascading behaviors.
-- ==============================================================================

----------------------------------------------------------------
-- Triggers
----------------------------------------------------------------

----------------------------------------------------------------
-- 1. TRIGGER: UpdateWaterNextBasedOnAPlantsLastWatered
----------------------------------------------------------------

-- Trigger function
CREATE OR REPLACE FUNCTION func_UpdateWaterNextBasedOnAPlantsLastWatered()
RETURNS TRIGGER AS $$
BEGIN
    -- For fresh INSERTS: Cast explicitly to ensure PostgreSQL matches the function signature
    IF (TG_OP = 'INSERT') THEN
        NEW.waterNext := jaboit02_calculateWaterNext(NEW.lastWatered::DATE, NEW.waterNeeded::INT, NEW.plantOutdoor::BOOLEAN);
    
    -- For UPDATES
    ELSIF (TG_OP = 'UPDATE') THEN
        IF (NEW.lastWatered IS DISTINCT FROM OLD.lastWatered) OR (NEW.waterNeeded IS DISTINCT FROM OLD.waterNeeded) THEN
            NEW.waterNext := jaboit02_calculateWaterNext(NEW.lastWatered::DATE, NEW.waterNeeded::INT, NEW.plantOutdoor::BOOLEAN);
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Bind trigger to table
CREATE TRIGGER trg_UpdateWaterNextBasedOnAPlantsLastWatered
BEFORE INSERT OR UPDATE ON jaboit02_Plant
FOR EACH ROW
EXECUTE FUNCTION func_UpdateWaterNextBasedOnAPlantsLastWatered();


----------------------------------------------------------------
-- 2. TRIGGER: UpdateWaterNextBasedOnAGardenBedsLastWatered
----------------------------------------------------------------

-- Trigger function
CREATE OR REPLACE FUNCTION func_UpdateWaterNextBasedOnAGardenBedsLastWatered()
RETURNS TRIGGER AS $$
BEGIN
    IF (NEW.lastWatered IS DISTINCT FROM OLD.lastWatered) THEN
        -- Update lastWatered of Plants within Gardenbed
        UPDATE jaboit02_Plant plant
        SET lastWatered = NEW.lastWatered
        FROM jaboit02_OutdoorPlant outplant
        WHERE outplant.plantId = plant.plantId
          AND outplant.gardenBedId = NEW.gardenBedId;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Bind trigger to table
CREATE TRIGGER trg_UpdateWaterNextBasedOnAGardenBedsLastWatered
AFTER UPDATE ON jaboit02_GardenBed
FOR EACH ROW
EXECUTE FUNCTION func_UpdateWaterNextBasedOnAGardenBedsLastWatered();


----------------------------------------------------------------
-- 3. TRIGGER: UpdateLastWateredBasedOnRain
----------------------------------------------------------------

-- Trigger function
CREATE OR REPLACE FUNCTION func_UpdateLastWateredBasedOnRain()
RETURNS TRIGGER AS $$
BEGIN
    IF (NEW.precipitation > 0.2) = TRUE THEN
        -- Update garden beds that do not have a roof
        -- PROTECTION: Only update if the incoming rain date is NEWER than the bed's current date
        UPDATE jaboit02_GardenBed
        SET lastWatered = NEW.weatherDate::DATE
        WHERE roofIsPresent = FALSE
          AND lastWatered < NEW.weatherDate::DATE;

        -- Update plants located in the garden that are not assigned to any garden bed
        -- PROTECTION: Bypasses case-sensitivity via UPPER() and protects against past dates
        UPDATE jaboit02_Plant plant
        SET lastWatered = NEW.weatherDate::DATE
        WHERE UPPER(plant.plantLocation) = 'GARDEN'
          AND plant.lastWatered < NEW.weatherDate::DATE
          AND NOT EXISTS (
              SELECT 1
              FROM jaboit02_OutdoorPlant outplant
              WHERE outplant.plantId = plant.plantId
          );
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Bind trigger to table
CREATE TRIGGER trg_UpdateLastWateredBasedOnRain
AFTER INSERT OR UPDATE ON jaboit02_Weather
FOR EACH ROW
EXECUTE FUNCTION func_UpdateLastWateredBasedOnRain();

