-- ==============================================================================
-- Script Name:  createViews.sql
-- Description:  Database views to aggregate plant metadata, yield logs, and 
--               optional garden bed infrastructure for backend reporting.
-- 
-- AI Usage:
--   - June 2026:      An LLM (Gemini) assisted during data aggregation design 
--                     by identifying a critical join logic bug, recommending 
--                     LEFT JOINs to ensure indoor plants (without garden beds) 
--                     are not accidentally excluded from harvest logs.
--   - October 2026:   Gemini assisted in porting and verifying this view 
--                     definition from MS SQL Server syntax to PostgreSQL.
-- ==============================================================================

----------------------------------------------------------------
-- Views
----------------------------------------------------------------
-- View to get PlantOutput + corresponding Info about Plant and (if available) GardenBed
----------------------------------------------------------------
CREATE VIEW dbo.jaboit02_View_Harvest AS
SELECT
    plant.plantId,
    plant.plantName,
    plant.plantType,
    plant.plantLocation,

    gardenbed.gardenBedId,
    gardenbed.gardenBedName,
    gardenbed.gardenBedLocation,

    plantOutput.plantOutputId,
    plantoutput.outputDate AS harvestDate,
    plantoutput.outputAmount AS harvestAmount

	FROM dbo.jaboit02_Plant plant
	-- Get PlantOutput Entries + Plant Information
	INNER JOIN dbo.jaboit02_PlantOutput plantoutput
		ON plant.plantId = plantoutput.plantId
	-- Get corresponding GardenBed Information if linked by OutdoorPlant
	LEFT JOIN dbo.jaboit02_OutdoorPlant outdoorplant -- AI Improvement: Use Left Join to keep Entries without optional GardenBeds
		ON plant.plantId = outdoorplant.plantId
	LEFT JOIN dbo.jaboit02_GardenBed gardenbed -- AI Improvement: Use Left Join
		ON outdoorplant.gardenBedId = gardenbed.gardenBedId;
GO
----------------------------------------------------------------