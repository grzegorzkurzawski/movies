#!/usr/bin/env bash

echo Running postgresql docker container
docker run -d --name moviespgs -p 6789:5432 -e POSTGRES_PASSWORD=admin -e POSTGRES_USER=admin -e POSTGRES_DB=movies postgres

# Tests if the database is ready, the check is aborted after 1000 tries.
it=0
until docker exec moviespgs psql -c "\conninfo" -U admin movies > /dev/null 2>&1 || [ "$it" -gt 1000 ]; do
    it=$(($it+1))
done
