-- ==============================================================================
-- Script Name:  createTables.sql
-- Description:  Core database schema definition for the PlantTracker application,
--               including tables for plants, garden beds, associations, and weather.
-- 
-- Architecture Timeline & AI Usage:
--   - June 2026:      The relational schema design, table structures, constraints, 
--                     and data logic were authored manually. An LLM (Gemini) 
--                     was used to check for structural best practices, 
--                     troubleshoot formatting, and provide minor data type feedback.
--   - October 2026:   The manually designed schema was translated and migrated 
--                     from Microsoft SQL Server syntax to native PostgreSQL 
--                     using Gemini (adapting types to SERIAL, BOOLEAN, and STORED generated columns).
-- ==============================================================================

CREATE TABLE jaboit02_Plant (
    plantId SERIAL PRIMARY KEY,
    plantName VARCHAR(100) NOT NULL DEFAULT 'Plant',
    plantCreatedAt DATE NOT NULL DEFAULT CURRENT_DATE,
    plantAlive BOOLEAN NOT NULL DEFAULT TRUE,
    plantType VARCHAR(20) NOT NULL DEFAULT 'GREEN'
        CHECK (plantType IN ('FLOWER', 'GREEN', 'HERB', 'VEGETABLE', 'FRUIT')),
    plantOutdoor BOOLEAN NOT NULL DEFAULT FALSE,
    plantLocation VARCHAR(20) NOT NULL DEFAULT 'INDOOR'
        CHECK (plantLocation IN ('INDOOR', 'GARDEN', 'BALCONY')),	
    lightNeeded INT NOT NULL DEFAULT 2
        CHECK (lightneeded BETWEEN 1 AND 3),
    waterNeeded INT NOT NULL DEFAULT 2
        CHECK (waterneeded BETWEEN 1 AND 7),
    lastWatered DATE DEFAULT CURRENT_DATE,
    lastRepotted DATE DEFAULT CURRENT_DATE,
    lastFertilized DATE DEFAULT CURRENT_DATE,
    waterNext DATE
);

CREATE TABLE jaboit02_GardenBed (
    gardenBedId SERIAL PRIMARY KEY,
    gardenBedName VARCHAR(100) NOT NULL DEFAULT 'GardenBed',
    gardenBedLocation VARCHAR(20) NOT NULL DEFAULT 'GARDEN'
        CHECK (gardenBedLocation IN ('GARDEN', 'BALCONY')),
    roofIsPresent BOOLEAN NOT NULL DEFAULT FALSE,
    sunlightAvailable INT NOT NULL DEFAULT 2
        CHECK (sunlightAvailable BETWEEN 1 AND 3),
    lastWatered DATE DEFAULT CURRENT_DATE
);

CREATE TABLE jaboit02_OutdoorPlant (
    plantId INT NOT NULL,
    gardenBedId INT NOT NULL, 
    CONSTRAINT PK_outdoorPlantId PRIMARY KEY (plantId, gardenBedId),
    CONSTRAINT FK_outdoorPlantId_Plant 
        FOREIGN KEY (plantId) 
        REFERENCES jaboit02_Plant(plantId)
        ON DELETE CASCADE,
    CONSTRAINT FK_outdoorPlantId_GardenBed 
        FOREIGN KEY (gardenBedId) 
        REFERENCES jaboit02_GardenBed(gardenBedId)
        ON DELETE CASCADE
);

CREATE TABLE jaboit02_PlantOutput (
    plantOutputId SERIAL PRIMARY KEY,
    plantId INT NOT NULL,
    outputDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    outputAmount INT NOT NULL DEFAULT 1,
    CONSTRAINT FK_plantId_Plant 
        FOREIGN KEY (plantId) 
        REFERENCES jaboit02_Plant(plantId)
        ON DELETE CASCADE
);

CREATE TABLE jaboit02_Weather (
    weatherDate TIMESTAMP PRIMARY KEY DEFAULT CURRENT_TIMESTAMP,
    temperature DECIMAL(5,2) NOT NULL DEFAULT 20.0 
        CHECK (temperature BETWEEN -50 AND 60),
    precipitation DECIMAL(5,2) NOT NULL DEFAULT 0.0
        CHECK (precipitation >= 0),
    cloudCover INT NOT NULL DEFAULT 0
        CHECK (cloudCover BETWEEN 0 AND 100),
    isRainy BOOLEAN GENERATED ALWAYS AS (CASE WHEN precipitation > 0.2 THEN TRUE ELSE FALSE END) STORED,
    isSunny BOOLEAN GENERATED ALWAYS AS (CASE WHEN cloudCover < 40 THEN TRUE ELSE FALSE END) STORED
);

