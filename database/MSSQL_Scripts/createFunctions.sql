-- ==============================================================================
-- Script Name:  createFunctions.sql
-- Description:  Database functions to calculate watering intervals and next 
--               watering dates based on telemetry and weather history.
-- 
-- AI Usage:
--   - June 2026:      An LLM (Gemini) was used to troubleshoot the core T-SQL logic, 
--                     optimizing database performance by replacing single-select loops 
--                     with efficient range-based SELECT queries.
--   - October 2026:   Gemini assisted in porting and migrating these functions 
--                     from Microsoft SQL Server (T-SQL) syntax over to PostgreSQL.
-- ==============================================================================

----------------------------------------------------------------
-- Functions
----------------------------------------------------------------
-- Function to calculate base interval between watering plants 
----------------------------------------------------------------
CREATE FUNCTION dbo.jaboit02_calculateBaseIntervalBetweenWatering
(
    	@waterNeeded INT
)
RETURNS INT
AS
BEGIN
	DECLARE @baseIntervalBetweenWatering INT;
	-- Base interval (days between watering depending only on water consumption)
	SET @baseIntervalBetweenWatering = CEILING (7.0 / @waterNeeded);
	IF @baseIntervalBetweenWatering < 1 
		SET @baseIntervalBetweenWatering = 1;
	RETURN @baseIntervalBetweenWatering;
END;
GO
----------------------------------------------------------------
-- Function to calculate recommended interval between watering plants 
----------------------------------------------------------------
CREATE FUNCTION dbo.jaboit02_calculateRecommendedIntervalBetweenWatering
(
	@isOutdoor BIT,
	@baseIntervalBetweenWatering INT,
	@averageTemperature DECIMAL (5,2),
	@numberOfSunnyDaysRecently INT
)
RETURNS INT
AS
BEGIN 
	DECLARE @influenceOfTemperature INT;
	DECLARE @influenceOfLocation INT;
	DECLARE @recommendedIntervalBetweenWatering INT;

	-- Temperature influence
	SET @influenceOfTemperature =
		CASE 
			WHEN @averageTemperature <= 10 THEN 2 
			WHEN @averageTemperature <= 20 THEN 1 
			WHEN @averageTemperature <= 25 THEN 0 
			WHEN @averageTemperature <= 30 THEN -1 
			ELSE -2 
		END;

	-- Outdoor plants dry faster	
	SET @influenceOfLocation =
		CASE
			WHEN @isOutdoor = 0 THEN 1
			ELSE 0
		END;
		
	-- Calculate recommended interval
	SET @recommendedIntervalBetweenWatering = 
	@baseIntervalBetweenWatering 
	+ @influenceOfTemperature 
	+ @influenceOfLocation 
	- CEILING(
	CAST(@numberOfSunnyDaysRecently AS FLOAT)
	/ @baseIntervalBetweenWatering);

	IF @recommendedIntervalBetweenWatering < 1 
		SET @recommendedIntervalBetweenWatering = 1;
	RETURN @recommendedIntervalBetweenWatering;
END;
GO
----------------------------------------------------------------
--Function to calculate when next to water plants 
----------------------------------------------------------------
CREATE FUNCTION dbo.jaboit02_calculateWaterNextForPlantId
(
    	@plantId INT	
)
RETURNS DATE
AS
BEGIN
	DECLARE @lastWatered DATE;
	DECLARE @waterNeeded INT;
	DECLARE @isOutdoor BIT;
	DECLARE @baseIntervalBetweenWatering INT;
	DECLARE @averageTemperature DECIMAL (5,2);
	DECLARE @numberOfSunnyDaysRecently INT;
	DECLARE @recommendedIntervalBetweenWatering INT;
	DECLARE @waterNext DATE;

	-- Retrieve information about plant
	SELECT
	@lastWatered = lastWatered,
	@waterNeeded = waterNeeded,
	@isOutdoor = plantOutdoor
	FROM dbo.jaboit02_Plant
	WHERE plantId = @plantId;

	-- Calculate base interval between watering
	SET @baseIntervalBetweenWatering =
	dbo.jaboit02_calculateBaseIntervalBetweenWatering(@waterNeeded);

	-- Retrieve information about weather
	-- Temperature on average within the base interval
	-- AI IMPROVEMENT: Use SELECT on  a range instead of using single SELECTS within a loop
	SELECT @averageTemperature = ISNULL(AVG(temperature), 20) -- AI Improvement: Provide alternative in case weather data is missing
	FROM dbo.jaboit02_Weather
	WHERE weatherDate >= DATEADD(day, -@baseIntervalBetweenWatering, CAST(CURRENT_TIMESTAMP AS DATE))
	AND weatherDate < CAST(CURRENT_TIMESTAMP AS DATE);

	-- Number of sunny days within the base interval
	-- AI IMPROVEMENT: Use SELECT on  a range instead of using single SELECTS within a loop
	SELECT @numberOfSunnyDaysRecently = COUNT(*)
	FROM dbo.jaboit02_Weather
	WHERE weatherDate >= DATEADD(day, -@baseIntervalBetweenWatering, CAST(CURRENT_TIMESTAMP AS DATE))
	AND weatherDate < CAST(CURRENT_TIMESTAMP AS DATE)
	AND isSunny = 1;

	-- Calculate recommended interval between watering
	SET @recommendedIntervalBetweenWatering =
	dbo.jaboit02_calculateRecommendedIntervalBetweenWatering (
	@isOutdoor, 
	@baseIntervalBetweenWatering, 
	@averageTemperature,
	@numberOfSunnyDaysRecently 
	);

	SET @waterNext = DATEADD(day, @recommendedIntervalBetweenWatering, @lastWatered);
	RETURN @waterNext;
END;
GO
----------------------------------------------------------------
