#!/bin/bash

# ==============================================================================
# Script Name:  init-database.sh
# Description:  Automates PostgreSQL database initialization by sequentially
#               executing table, function, trigger, and view SQL-scripts.
# 
# Note:         This script was generated with the assistance of Google Gemini 
#               in October 2026.
# ==============================================================================


# Exit immediately if a command exits with a non-zero status
set -e

# Path to the folder containing the original SQL scripts inside the container
SCRIPT_DIR="/original_scripts/createPostgresDB"

echo "=== Starting automated database initialization ==="

# 1. Create Tables
echo "Creating tables..."
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "$SCRIPT_DIR/createTables.sql"

# 2. Create Functions
echo "Creating functions..."
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "$SCRIPT_DIR/createFunctions.sql"

# 3. Create Triggers
echo "Creating triggers..."
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "$SCRIPT_DIR/createTriggers.sql"

# 4. Create Views
echo "Creating views..."
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "$SCRIPT_DIR/createViews.sql"

echo "=== Initialization successfully completed! ==="
