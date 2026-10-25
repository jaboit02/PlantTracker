-- ==============================================================================
-- Script Name:  createTestData.sql
-- Description:  Fully dynamic, test dataset optimized for PostgreSQL.
--               All dates scale automatically relative to the current execution day
--               to provide permanent boundary coverage for triggers and functions.
-- 
-- Note:         Migrated to PostgreSQL and made fully dynamic with the assistance 
--               of Gemini in October 2026.
-- ==============================================================================

----------------------------------------------------------------
-- 1. Table: jaboit02_Plant
----------------------------------------------------------------
-- Uses relative intervals to simulate past maintenance events
INSERT INTO jaboit02_Plant (
    plantName, plantType, plantOutdoor, plantLocation, lightNeeded, waterNeeded,
    plantCreatedAt, lastWatered, lastRepotted, lastFertilized
) VALUES 
('TEST_GreenPlantOnBalcony', 'Green', 1, 'Balcony', 2, 2,
  CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE - INTERVAL '4 days', CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE - INTERVAL '14 days'),

('TEST_HerbPlantInGarden', 'Herb', 1, 'Garden', 2, 3,
  CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '20 days'),

('TEST_FruitPlantInGarden', 'Fruit', 1, 'Garden', 3, 4,
  CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '1 day',  CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '15 days'),

('TEST_VegetablePlantInGarden', 'Vegetable', 1, 'Garden', 2, 5,
  CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '10 days'),

('TEST_IndoorHousePlant', 'Green', 0, 'Indoor', 1, 2,
  CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE - INTERVAL '30 days'),

-- Edge Cases for boundary verification
('TEST_ThirstyFlowerInGarden', 'Flower', 1, 'Garden', 3, 7,
  CURRENT_DATE - INTERVAL '5 days',  CURRENT_DATE - INTERVAL '6 days', CURRENT_DATE - INTERVAL '5 days',  CURRENT_DATE - INTERVAL '5 days'),

('TEST_DesertCactusIndoor', 'Green', 0, 'Indoor', 3, 1,
  CURRENT_DATE - INTERVAL '90 days', CURRENT_DATE - INTERVAL '12 days', CURRENT_DATE - INTERVAL '90 days', CURRENT_DATE - INTERVAL '90 days');

----------------------------------------------------------------
-- 2. Table: jaboit02_GardenBed
----------------------------------------------------------------
INSERT INTO dbo.jaboit02_GardenBed (
    gardenBedName, gardenBedLocation, roofIsPresent, sunlightAvailable, lastWatered
) VALUES 
('TEST_MainGardenBed', 'Garden', 0, 3, CURRENT_DATE - INTERVAL '2 days'),
('TEST_CoveredBalconyBed', 'Balcony', 1, 1, CURRENT_DATE - INTERVAL '4 days');

----------------------------------------------------------------
-- 3. Table: jaboit02_OutdoorPlant
----------------------------------------------------------------
-- Link Balcony Plant to Balcony Bed
INSERT INTO jaboit02_OutdoorPlant (plantId, gardenBedId)
SELECT p.plantId, g.gardenBedId 
FROM jaboit02_Plant p, jaboit02_GardenBed g
WHERE p.plantName = 'TEST_GreenPlantOnBalcony' 
  AND g.gardenBedName = 'TEST_CoveredBalconyBed';

-- Link Garden Plants to the Main Garden Bed
INSERT INTO jaboit02_OutdoorPlant (plantId, gardenBedId)
SELECT p.plantId, g.gardenBedId 
FROM jaboit02_Plant p, g.gardenBedId
WHERE g.gardenBedName = 'TEST_MainGardenBed'
  AND p.plantName IN ('TEST_HerbPlantInGarden', 'TEST_FruitPlantInGarden', 'TEST_VegetablePlantInGarden');

----------------------------------------------------------------
-- 4. Table: jaboit02_PlantOutput (Harvest Logs)
----------------------------------------------------------------
-- Dynamic timestamps for harvest tracking
INSERT INTO jaboit02_PlantOutput (plantId, outputAmount, outputDate)
SELECT plantId, 1, CURRENT_TIMESTAMP - INTERVAL '2 days' FROM jaboit02_Plant WHERE plantName = 'TEST_HerbPlantInGarden';

INSERT INTO jaboit02_PlantOutput (plantId, outputAmount, outputDate)
SELECT plantId, 2, CURRENT_TIMESTAMP - INTERVAL '1 day' FROM jaboit02_Plant WHERE plantName = 'TEST_FruitPlantInGarden';

----------------------------------------------------------------
-- 5. Table: jaboit02_Weather
----------------------------------------------------------------
-- Generates 7 days of sunny weather leading up to yesterday
INSERT INTO jaboit02_Weather (weatherDate, temperature, precipitation, cloudCover)
SELECT 
    day::timestamp,
    25.00,
    0.0,
    15
FROM generate_series(
    CURRENT_DATE - INTERVAL '7 days', 
    CURRENT_DATE - INTERVAL '1 day', 
    '1 day'::interval
) AS day;

