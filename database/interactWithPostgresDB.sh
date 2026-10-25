#!/bin/bash
# Exit immediately if a command exits with a non-zero status
set -e

cd ..
sudo docker exec -it planttracker_postgres bash -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"'

