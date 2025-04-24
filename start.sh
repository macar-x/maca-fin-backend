# Script to run containers
# @authors Emmett
# @since 2025/04/24

# Load environment variables from the .env file.
if [ -f ".env" ]; then
  echo ".env file detected, applying..."
  set -a
  source ".env"
  set +a
fi

# Check environment variables.
if [ -z "$MACA_FIN_BACKEND_PUBLIC_PORT" ]; then
  echo "MACA_FIN_BACKEND_PUBLIC_PORT is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_BACKEND_DATA_PATH" ]; then
  echo "MACA_FIN_BACKEND_DATA_PATH is empty, please check .env file."
  exit 255
fi

# stop all container
sudo docker compose down -v --remove-orphans
sudo docker network remove maca_cloud_network

# start all container
sudo docker network create maca_cloud_network
sudo docker compose up -d
