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

# Check backend server configuration.
if [ -z "$MACA_FIN_BACKEND_PUBLIC_PORT" ]; then
  echo "MACA_FIN_BACKEND_PUBLIC_PORT is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_BACKEND_DATA_PATH" ]; then
  echo "MACA_FIN_BACKEND_DATA_PATH is empty, please check .env file."
  exit 255
fi
# Check backend database configuration.
if [ -z "$MACA_FIN_DATABASE_JDBC_URL" ]; then
  echo "MACA_FIN_DATABASE_JDBC_URL is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_DATABASE_USERNAME" ]; then
  echo "MACA_FIN_DATABASE_USERNAME is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_DATABASE_PASSWORD" ]; then
  echo "MACA_FIN_DATABASE_PASSWORD is empty, please check .env file."
  exit 255
fi
# Check backend OIDC configuration.
if [ -z "$MACA_FIN_OIDC_SERVER_URI" ]; then
  echo "MACA_FIN_OIDC_SERVER_URI is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_OIDC_REALM_NAME" ]; then
  echo "MACA_FIN_OIDC_REALM_NAME is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_OIDC_REALM_CLIENT" ]; then
  echo "MACA_FIN_OIDC_REALM_CLIENT is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_OIDC_REALM_SECRET" ]; then
  echo "MACA_FIN_OIDC_REALM_SECRET is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_OIDC_ADMIN_USERNAME" ]; then
  echo "MACA_FIN_OIDC_ADMIN_USERNAME is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_OIDC_ADMIN_PASSWORD" ]; then
  echo "MACA_FIN_OIDC_ADMIN_PASSWORD is empty, please check .env file."
  exit 255
fi

# Scan migrate folder and execute all sql files
if [ -d "migrate" ]; then
  echo "migrate folder detected, applying..."
  for file in migrate/*.sql; do
    if [ -f "$file" ]; then
      echo "Executing $file..."
      # execute postgresql script
      sudo docker exec -i $MACA_FIN_DATABASE_CONTAINER psql \
      -h 127.0.0.1 -p 5432 -d $MACA_FIN_DATABASE_NAME \
      -U $MACA_FIN_DATABASE_USERNAME < "$file"
      # execute mysql script
      # sudo docker exec -i $MACA_FIN_DATABASE_CONTAINER mysql \
      # -u$MACA_FIN_DATABASE_USERNAME -p$MACA_FIN_DATABASE_PASSWORD \
      # --default-character-set=utf8mb4 $MACA_FIN_DATABASE_NAME < "$file"
    fi
  done
else
  echo "migrate folder not detected, skipping..."
fi

# stop all container
sudo docker compose down -v --remove-orphans
sudo docker network remove maca_cloud_network

# start all container
sudo docker network create maca_cloud_network
sudo docker compose up -d
