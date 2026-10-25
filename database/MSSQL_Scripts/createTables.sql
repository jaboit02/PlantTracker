-- ==============================================================================
-- Script Name:  createTables.sql
-- Description:  Core database schema definition for the PlantTracker application,
--               including tables for plants, garden beds, associations, and weather.
-- 
-- AI Usage:
--   - June 2026:      An LLM (Gemini) assisted during initial schema design by 
--                     providing architectural best practices (e.g., using BIT for 
--                     booleans, implementing ON DELETE CASCADE, and moving core weather 
--                     logic into database-computed/generated columns).
--   - October 2026:   Gemini assisted in porting and translating this schema 
--                     definition from Microsoft SQL Server to PostgreSQL syntax.
-- ==============================================================================


----------------------------------------------------------------
-- Tables
----------------------------------------------------------------
CREATE TABLE dbo.jaboit02_Plant (
	plantId INT PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.jaboit02_PlantSeq,
	plantName VARCHAR(100) NOT NULL
		DEFAULT 'Plant',
	plantCreatedAt DATE  NOT NULL
		DEFAULT CONVERT(DATE, GETDATE()), -- AI Improvement: use Date, not Timestamp
	plantAlive BIT NOT NULL 
		DEFAULT 1,
	plantType VARCHAR(20) NOT NULL
		DEFAULT 'Green', 
		CHECK (plantType IN ('Flower', 'Green', 'Herb', 'Vegetable', 'Fruit')),
	plantOutdoor BIT NOT NULL -- AI Improvement: use Bit instead of Char for Boolean values
		DEFAULT 0,
		-- 1 = exposed to weather (e.g. garden)
		-- 0 = protected from weather (indoor)
	plantLocation VARCHAR(20) NOT NULL
		DEFAULT 'Indoor'
		CHECK (plantLocation IN ('Indoor', 'Garden', 'Balcony')),	
	lightNeeded INT NOT NULL 
		DEFAULT 2
		CHECK (lightNeeded BETWEEN 1 AND 3),
	waterNeeded INT NOT NULL -- How often to water within a week?
		DEFAULT 2
		CHECK (waterNeeded BETWEEN 1 AND 7),
	lastWatered DATE DEFAULT CONVERT(DATE, GETDATE()),
	lastRepotted DATE DEFAULT CONVERT(DATE, GETDATE()),
	lastFertilized DATE DEFAULT CONVERT(DATE, GETDATE()),
	waterNext DATE
);
----------------------------------------------------------------
CREATE TABLE dbo.jaboit02_GardenBed (
	gardenBedId INT PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.jaboit02_GardenBedSeq,
	gardenBedName VARCHAR(100) NOT NULL 
		DEFAULT 'GardenBed',
	gardenBedLocation VARCHAR(20) NOT NULL
		DEFAULT 'Garden'
		CHECK (gardenBedLocation IN ('Garden', 'Balcony')),
	roofIsPresent BIT NOT NULL
		DEFAULT 0,
	sunlightAvailable INT NOT NULL
		DEFAULT 2
		CHECK (sunlightAvailable BETWEEN 1 AND 3),
	lastWatered DATE DEFAULT CONVERT(DATE, GETDATE())
);
----------------------------------------------------------------
CREATE TABLE dbo.jaboit02_OutdoorPlant (
	plantId INT NOT NULL,
	gardenBedId INT NOT NULL, 
	CONSTRAINT PK_outdoorPlantId PRIMARY KEY (plantId, gardenBedId),
	CONSTRAINT FK_outdoorPlantId_Plant 
		FOREIGN KEY (plantId) 
		REFERENCES dbo.jaboit02_Plant(plantId)
		ON DELETE CASCADE, -- AI Improvement: automatically delete entries from outdoorPlant if corresponding plant is deleted
	CONSTRAINT FK_outdoorPlantId_GardenBed 
		FOREIGN KEY (gardenBedId) 
		REFERENCES dbo.jaboit02_GardenBed(gardenBedId)
		ON DELETE CASCADE
);
----------------------------------------------------------------
CREATE TABLE dbo.jaboit02_PlantOutput (
	plantOutputId INT PRIMARY KEY DEFAULT NEXT VALUE FOR dbo.jaboit02_PlantOutputSeq,
	plantId INT NOT NULL,
	outputDate DATETIME2(0) DEFAULT CURRENT_TIMESTAMP, -- AI Improvement: Use Datetime2(0) for yyyy-MM-dd'T'HH:mm:ss Format
	outputAmount INT NOT NULL DEFAULT 1,
	CONSTRAINT FK_plantId_Plant 
		FOREIGN KEY (plantId) 
		REFERENCES dbo.jaboit02_Plant(plantId)
		ON DELETE CASCADE
);
----------------------------------------------------------------
CREATE TABLE dbo.jaboit02_Weather (
	weatherDate DATETIME PRIMARY KEY DEFAULT CURRENT_TIMESTAMP,
	temperature DECIMAL (5,2) NOT NULL 
		DEFAULT 20.0 
		CHECK (temperature BETWEEN -50 AND 60),
	precipitation DECIMAL (5,2) NOT NULL
		DEFAULT 0.0
		CHECK (precipitation >= 0),
	cloudCover INT NOT NULL
		DEFAULT 0
		CHECK (cloudCover BETWEEN 0 AND 100),
	isRainy AS (CASE WHEN precipitation > 0.2 THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END), -- AI Improvement: Move weather logic from backend to database + Cast to Bit/Bool
	isSunny AS (CASE WHEN cloudCover < 40 THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END) -- AI Improvement: Move weather logic from backend to database + Cast to Bit/Bool
);
----------------------------------------------------------------
