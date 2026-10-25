-- ==============================================================================
-- Script Name:  deleteData.sql
-- Description:  Safely truncates all operational tracking tables, resets all 
--               auto-incrementing primary key sequences, and resolves foreign 
--               key constraints dynamically using cascade enforcement.
-- 
-- Architecture Timeline & AI Usage:
--   - October 2026:   The data-reset requirement was authored manually. Gemini 
--                     was utilized as a syntax utility during the PostgreSQL 
--                     migration to consolidate multiple separate T-SQL statements 
--                     into a single, optimized native PostgreSQL command 
--                     (leveraging RESTART IDENTITY and CASCADE).
-- ==============================================================================

----------------------------------------------------------------
-- Clean up all tables and reset auto-increment IDs
----------------------------------------------------------------
TRUNCATE jaboit02_PlantOutput, jaboit02_OutdoorPlant, jaboit02_Weather, jaboit02_GardenBed, jaboit02_Plant RESTART IDENTITY CASCADE;

