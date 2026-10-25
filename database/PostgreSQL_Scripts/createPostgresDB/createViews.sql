-- ==============================================================================
-- Script Name:  createViews.sql
-- Description:  Database views to aggregate plant metadata, yield logs, and 
--               optional garden bed infrastructure for backend reporting.
-- 
-- Architecture Timeline & AI Usage:
--   - June 2026:      The analytical view concept, column selections, and data 
--                     aggregation layouts were authored manually. An LLM 
--                     (Gemini) was used as a review assistant to optimize 
--                     the join logic (ensuring LEFT JOINs were utilized so indoor 
--                     plants are not excluded from harvest outputs).
--   - October 2026:   The manually designed view definition was translated and 
--                     migrated from Microsoft SQL Server syntax to native PostgreSQL 
--                     using Gemini.
-- ==============================================================================

----------------------------------------------------------------
-- Views
----------------------------------------------------------------
-- View to get PlantOutput + corresponding Info about Plant and (if available) GardenBed
----------------------------------------------------------------
CREATE OR REPLACE VIEW jaboit02_View_Harvest AS
SELECT
    plant.plantId,
    plant.plantName,
    plant.plantType,
    plant.plantLocation,

    gardenbed.gardenBedId,
    gardenbed.gardenBedName,
    gardenbed.gardenBedLocation,

    plantoutput.plantOutputId,
    plantoutput.outputDate AS harvestDate,
    plantoutput.outputAmount AS harvestAmount

FROM jaboit02_Plant plant
-- Get PlantOutput Entries + Plant Information
INNER JOIN jaboit02_PlantOutput plantoutput
    ON plant.plantId = plantoutput.plantId
-- Get corresponding GardenBed Information if linked by OutdoorPlant
LEFT JOIN jaboit02_OutdoorPlant outdoorplant
    ON plant.plantId = outdoorplant.plantId
LEFT JOIN jaboit02_GardenBed gardenbed
    ON outdoorplant.gardenBedId = gardenbed.gardenBedId;

