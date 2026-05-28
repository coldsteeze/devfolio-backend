#!/bin/bash

echo "Registering auth connector..."

curl -X POST http://localhost:8087/connectors \
  -H "Content-Type: application/json" \
  -d @./connectors/auth-outbox-connector.json

echo "Registering profile connector..."

curl -X POST http://localhost:8087/connectors \
  -H "Content-Type: application/json" \
  -d @./connectors/user-profile-outbox-connector.json

echo "Registering project connector..."

curl -X POST http://localhost:8087/connectors \
  -H "Content-Type: application/json" \
  -d @./connectors/project-outbox-connector.json