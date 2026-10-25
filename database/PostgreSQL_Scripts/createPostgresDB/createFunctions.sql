-- ==============================================================================
-- Script Name:  createFunctions.sql
-- Description:  PostgreSQL PL/pgSQL functions to calculate watering intervals 
--               and predictive next-watering dates based on weather history.
-- 
-- Architecture Timeline & AI Usage:
--   - June 2026:      The core logic and algorithm were authored 
--                     manually. An LLM (Gemini) was used as a review tool 
--                     to check for syntax errors, troubleshoot bugs, and provide 
--                     performance optimization advice.
--   - October 2026:   The manually designed script was translated and migrated 
--                     from Microsoft SQL Server (T-SQL) syntax to PostgreSQL using Gemini.
-- ==============================================================================

----------------------------------------------------------------
-- Functions
----------------------------------------------------------------

-- 1. Function to calculate base interval between watering plants 
----------------------------------------------------------------
CREATE OR REPLACE FUNCTION jaboit02_calculateBaseIntervalBetweenWatering(waterNeeded INT)
RETURNS INT
AS $$
DECLARE
    baseIntervalBetweenWatering INT;
BEGIN
    -- Base interval (days between watering depending only on water consumption)
    baseIntervalBetweenWatering := CEIL(7.0 / waterNeeded);
    
    IF baseIntervalBetweenWatering < 1 THEN
        baseIntervalBetweenWatering := 1;
    END IF;
    
    RETURN baseIntervalBetweenWatering;
END;
$$ LANGUAGE plpgsql;

----------------------------------------------------------------
-- 2. Function to calculate recommended interval between watering plants 
----------------------------------------------------------------
CREATE OR REPLACE FUNCTION jaboit02_calculateRecommendedIntervalBetweenWatering(
    isOutdoor BOOLEAN,
    baseIntervalBetweenWatering INT,
    averageTemperature DECIMAL(5,2),
    numberOfSunnyDaysRecently INT
)
RETURNS INT
AS $$
DECLARE
    influenceOfTemperature INT;
    influenceOfLocation INT;
    recommendedIntervalBetweenWatering INT;
BEGIN 
    -- Temperature influence
    influenceOfTemperature :=
        CASE 
            WHEN averageTemperature <= 10 THEN 2 
            WHEN averageTemperature <= 20 THEN 1 
            WHEN averageTemperature <= 25 THEN 0 
            WHEN averageTemperature <= 30 THEN -1 
            ELSE -2 
        END;

    -- Outdoor plants dry faster	
    influenceOfLocation :=
        CASE
            WHEN isOutdoor = FALSE THEN 1
            ELSE 0
        END;
		
    -- Calculate recommended interval
    recommendedIntervalBetweenWatering := 
        baseIntervalBetweenWatering 
        + influenceOfTemperature 
        + influenceOfLocation 
        - CEIL(numberOfSunnyDaysRecently::NUMERIC / baseIntervalBetweenWatering);

    IF recommendedIntervalBetweenWatering < 1 THEN
        recommendedIntervalBetweenWatering := 1;
    END IF;
    
    RETURN recommendedIntervalBetweenWatering;
END;
$$ LANGUAGE plpgsql;

----------------------------------------------------------------
-- 3. Function to calculate when next to water plants 
----------------------------------------------------------------
CREATE OR REPLACE FUNCTION jaboit02_calculateWaterNext(    
    param_lastWatered DATE,
    param_waterNeeded INT,
    param_isOutdoor BOOLEAN
)
RETURNS DATE
AS $$
DECLARE
    v_lastWatered DATE;
    v_isOutdoor BOOLEAN;
    baseIntervalBetweenWatering INT;
    averageTemperature DECIMAL(5,2);
    numberOfSunnyDaysRecently INT;
    recommendedIntervalBetweenWatering INT;
BEGIN
    -- Enforce explicit casting to prevent strict PL/pgSQL assignment exceptions
    v_lastWatered := param_lastWatered::DATE;
    v_isOutdoor   := param_isOutdoor::BOOLEAN;

    -- Calculate base interval between watering
    baseIntervalBetweenWatering := jaboit02_calculateBaseIntervalBetweenWatering(param_waterNeeded);

    -- Retrieve information about weather (Temperature on average within the base interval)
    SELECT COALESCE(AVG(temperature), 20.0)
    INTO averageTemperature
    FROM jaboit02_Weather
    WHERE weatherDate >= v_lastWatered - (INTERVAL '1 day' * baseIntervalBetweenWatering)
      AND weatherDate < v_lastWatered;

    -- Number of sunny days within the base interval
    SELECT COUNT(*)
    INTO numberOfSunnyDaysRecently
    FROM jaboit02_Weather
    WHERE weatherDate >= v_lastWatered - (INTERVAL '1 day' * baseIntervalBetweenWatering)
      AND weatherDate < v_lastWatered
      AND isSunny = TRUE;

    -- Calculate recommended interval between watering
    recommendedIntervalBetweenWatering := jaboit02_calculateRecommendedIntervalBetweenWatering (
        v_isOutdoor, 
        baseIntervalBetweenWatering, 
        averageTemperature,
        numberOfSunnyDaysRecently 
    );

    -- Safe PostgreSQL Date Arithmetic: adding integer days directly to a DATE
    RETURN param_lastWatered + recommendedIntervalBetweenWatering; 

END;
$$ LANGUAGE plpgsql;

----------------------------------------------------------------
-- 4. Wrapper Function retrieve additional plant information for calculations 
----------------------------------------------------------------
CREATE OR REPLACE FUNCTION jaboit02_calculateWaterNextForPlantId(param_plantId INT)
RETURNS DATE
AS $$
DECLARE
    v_lastWatered DATE;
    v_waterNeeded INT;
    v_isOutdoor BOOLEAN;
BEGIN
    SELECT lastWatered, waterNeeded, plantOutdoor
    INTO v_lastWatered, v_waterNeeded, v_isOutdoor
    FROM jaboit02_Plant
    WHERE plantId = param_plantId;

    RETURN jaboit02_calculateWaterNext(v_lastWatered, v_waterNeeded, v_isOutdoor);
END;
$$ LANGUAGE plpgsql;
