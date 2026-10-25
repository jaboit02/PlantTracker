-- ==============================================================================
-- Script Name:  cleanupData.sql
-- Description:  Deletes all data from 
--               the planttracker tables while preserving the structural schema.
-- ==============================================================================

DELETE FROM dbo.jaboit02_PlantOutput;
DELETE FROM dbo.jaboit02_OutdoorPlant;
DELETE FROM dbo.jaboit02_Weather;
DELETE FROM dbo.jaboit02_GardenBed;
DELETE FROM dbo.jaboit02_Plant;