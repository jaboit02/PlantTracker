-- ==============================================================================
-- Script Name:  viewData.sql
-- Description:  Outputs all data from the planttracker tables.
-- ==============================================================================
----------------------------------------------------------------
-- View Data
----------------------------------------------------------------
-- jaboit02_Plant
----------------------------------------------------------------
SELECT * FROM dbo.jaboit02_Plant 
----------------------------------------------------------------
-- jaboit02_GardenBed
----------------------------------------------------------------
SELECT * FROM dbo.jaboit02_GardenBed 
----------------------------------------------------------------
-- jaboit02_OutdoorPlant
----------------------------------------------------------------
SELECT * FROM dbo.jaboit02_OutdoorPlant outplant
JOIN dbo.jaboit02_Plant plant 
ON plant.plantId = outplant.plantID
JOIN dbo.jaboit02_GardenBed gardenbed 
ON gardenbed.gardenBedId = outplant.gardenBedId
----------------------------------------------------------------
-- jaboit02_PlantOutput
----------------------------------------------------------------
SELECT * FROM dbo.jaboit02_PlantOutput plantoutput
JOIN dbo.jaboit02_Plant plant 
ON plant.plantId = plantoutput.plantID
----------------------------------------------------------------
-- jaboit02_Weather
----------------------------------------------------------------
SELECT * FROM dbo.jaboit02_Weather
