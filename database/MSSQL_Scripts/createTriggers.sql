-- ==============================================================================
-- Script Name:  createTriggers.sql 
-- Description:  Database triggers for automating lifecycle updates, keeping track 
--               of watering events, and dynamically adjusting next watering dates 
--               based on environmental events (like rainfall).
-- 
-- AI Usage:
--   - June 2026:      The entire automation architecture, operational logic, and 
--                     event-driven flows were authored manually. An LLM 
--                     (Gemini) was used to troubleshoot complex T-SQL multi-table 
--                     joins and provide formatting advice.
--   - October 2026:   Gemini assisted in porting this architecture to PostgreSQL. 
--                     Note: Due to a brief migration confusion, the non-cascading 
--                     workaround blocks required for MSSQL were accidentally removed 
--                     and subsequently restored with AI assistance to preserve 
--                     native Microsoft SQL Server compatibility.
-- ==============================================================================

----------------------------------------------------------------
-- Trigger
----------------------------------------------------------------
CREATE TRIGGER UpdateWaterNextBasedOnAPlantsLastWatered
ON dbo.jaboit02_Plant
AFTER UPDATE, INSERT
AS
BEGIN 
	IF UPDATE(lastWatered)
	BEGIN
		UPDATE plant
		SET waterNext = dbo.jaboit02_calculateWaterNextForPlantId(i.plantId) 
		FROM dbo.jaboit02_Plant plant
		JOIN inserted i
			ON plant.plantId = i.plantId 
	END
END;
GO
----------------------------------------------------------------
CREATE TRIGGER UpdateWaterNextBasedOnAGardenBedsLastWatered
ON dbo.jaboit02_GardenBed
AFTER UPDATE
AS
BEGIN 
	IF UPDATE(lastWatered)
	BEGIN

		-- Update the last watered date for the plants inside the garden bed
		UPDATE plant 
        SET plant.lastWatered = i.lastWatered
        FROM dbo.jaboit02_Plant plant
        JOIN dbo.jaboit02_OutdoorPlant outplant
            ON outplant.plantId = plant.plantId
        JOIN inserted i
            ON i.gardenBedId = outplant.gardenBedId;
			
		-- MANUAL WORKAROUND: Force recalculation because MSSQL triggers do not cascade
		UPDATE plant 
		SET waterNext = dbo.jaboit02_calculateWaterNextForPlantId(plant.plantId) 
		FROM dbo.jaboit02_Plant plant
		JOIN dbo.jaboit02_OutdoorPlant outplant
			ON outplant.plantId = plant.plantId
		JOIN inserted i
			ON i.gardenBedId = outplant.gardenBedId
	END
END;
GO
----------------------------------------------------------------
CREATE TRIGGER UpdateLastWateredBasedOnRain
ON dbo.jaboit02_Weather
AFTER INSERT, UPDATE
AS
BEGIN 

	SET NOCOUNT ON;

	-- Only execute if it is actually raining
    IF EXISTS (SELECT 1 FROM inserted i WHERE i.isRainy = 1)
    BEGIN

		-- Update GardenBed without Roof
		UPDATE gardenbed
		SET lastWatered = CONVERT(DATE, i.weatherDate) --AI Improvement: Use explicit convert instead of relying on implicit convert
		FROM dbo.jaboit02_GardenBed gardenbed
		CROSS JOIN inserted i -- AI Improvement: Fixed Join
		WHERE gardenbed.roofIsPresent = 0
		AND i.isRainy = 1;

		-- Update plants WITHIN these GardenBeds (lastWatered)
        UPDATE plant
        SET plant.lastWatered = CONVERT(DATE, i.weatherDate)
        FROM dbo.jaboit02_Plant plant
        JOIN dbo.jaboit02_OutdoorPlant outplant ON outplant.plantId = plant.plantId
        JOIN dbo.jaboit02_GardenBed gardenbed ON gardenbed.gardenBedId = outplant.gardenBedId
        CROSS JOIN inserted i
        WHERE gardenbed.roofIsPresent = 0;

		-- MANUAL WORKAROUND: Force recalculation because MSSQL triggers do not cascade
		UPDATE plant
        SET plant.waterNext = dbo.jaboit02_calculateWaterNextForPlantId(plant.plantId)
        FROM dbo.jaboit02_Plant plant
        JOIN dbo.jaboit02_OutdoorPlant outplant ON outplant.plantId = plant.plantId
        JOIN dbo.jaboit02_GardenBed gardenbed ON gardenbed.gardenBedId = outplant.gardenBedId
        WHERE gardenbed.roofIsPresent = 0;

		-- Update Plants in Garden that are not in a GardenBed (lastWatered)
		UPDATE plant
		SET lastWatered = CONVERT(DATE, i.weatherDate)
		FROM dbo.jaboit02_Plant plant
		CROSS JOIN inserted i -- AI Improvement: Fixed Join
		WHERE i.isRainy = 1
		AND (
				plant.plantLocation = 'Garden'
				AND NOT EXISTS (
					SELECT 1
					FROM dbo.jaboit02_OutdoorPlant outplant
					WHERE outplant.plantId = plant.plantId
				)
		);

		-- MANUAL WORKAROUND: Force recalculation because MSSQL triggers do not cascade
		UPDATE plant
			SET plant.waterNext = dbo.jaboit02_calculateWaterNextForPlantId(plant.plantId)
			FROM dbo.jaboit02_Plant plant
			WHERE plant.plantLocation = 'Garden'
			AND NOT EXISTS (
				SELECT 1 FROM dbo.jaboit02_OutdoorPlant outplant WHERE outplant.plantId = plant.plantId
			);
	END
END;
GO
----------------------------------------------------------------
