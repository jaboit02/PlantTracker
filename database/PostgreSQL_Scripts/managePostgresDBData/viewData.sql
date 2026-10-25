-- ==============================================================================
-- Script Name:  viewData.sql
-- Description:  Utility query script for data verification and debugging. 
--               Retrieves raw table states and executes structured JOINs to 
--               inspect relational integrity and tracking logs.
-- 
-- Architecture Timeline & AI Usage:
--   - June 2026:      The verification queries and structured JOIN logic were 
--                     authored manually for database debugging.
--   - October 2026:   The queries were translated and migrated from MS SQL Server 
--                     syntax to native PostgreSQL (removing dbo prefixes) using 
--                     Gemini.
-- ==============================================================================

----------------------------------------------------------------
-- View Data
----------------------------------------------------------------
-- jaboit02_Plant
----------------------------------------------------------------
SELECT * FROM jaboit02_Plant;

----------------------------------------------------------------
-- jaboit02_GardenBed
----------------------------------------------------------------
SELECT * FROM jaboit02_GardenBed;

----------------------------------------------------------------
-- jaboit02_OutdoorPlant
----------------------------------------------------------------
SELECT * FROM jaboit02_OutdoorPlant outplant
JOIN jaboit02_Plant plant 
    ON plant.plantId = outplant.plantId
JOIN jaboit02_GardenBed gardenbed 
    ON gardenbed.gardenBedId = outplant.gardenBedId;

----------------------------------------------------------------
-- jaboit02_PlantOutput
----------------------------------------------------------------
SELECT * FROM jaboit02_PlantOutput plantoutput
JOIN jaboit02_Plant plant 
    ON plant.plantId = plantoutput.plantId;

----------------------------------------------------------------
-- jaboit02_Weather
----------------------------------------------------------------
SELECT * FROM jaboit02_Weather;

