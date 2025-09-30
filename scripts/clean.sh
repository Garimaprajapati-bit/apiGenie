#!/bin/bash

echo "🧹 Cleaning up API Mock Generator..."

# Stop and remove containers
docker-compose down -v

# Remove images
docker rmi $(docker images -q mock-api-*)

# Remove unused volumes
docker volume prune -f

echo "✅ Cleanup complete!"
