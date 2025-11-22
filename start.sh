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
if [ -z "$MACA_FIN_BACKEND_DATABASE_JDBC_URL" ]; then
  echo "MACA_FIN_BACKEND_DATABASE_JDBC_URL is empty, please check .env file."
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
if [ -z "$MACA_FIN_DATABASE_PORT" ]; then
  echo "MACA_FIN_DATABASE_PORT is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_DATABASE_NAME" ]; then
  echo "MACA_FIN_DATABASE_NAME is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_DATABASE_PERSIST_PATH" ]; then
  echo "MACA_FIN_DATABASE_PERSIST_PATH is empty, please check .env file."
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
if [ -z "$MACA_FIN_OIDC_SERVER_HTTP_PORT" ]; then
  echo "MACA_FIN_OIDC_SERVER_HTTP_PORT is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_OIDC_SERVER_HTTPS_PORT" ]; then
  echo "MACA_FIN_OIDC_SERVER_HTTPS_PORT is empty, please check .env file."
  exit 255
fi

# stop all container
sudo docker compose down -v --remove-orphans
# sudo docker network remove maca_cloud_network

# start all container
sudo docker network create maca_cloud_network
sudo docker compose up -d

# Function to create database if not exists
create_database_if_not_exists() {
  local db_name=$1
  echo "Checking if database '$db_name' exists..."
  
  local db_exists=$(sudo docker exec $MACA_FIN_DATABASE_CONTAINER psql \
    -U $MACA_FIN_DATABASE_USERNAME -d postgres -tAc \
    "SELECT 1 FROM pg_database WHERE datname='$db_name'" 2>/dev/null)
  
  if [ "$db_exists" != "1" ]; then
    echo "Database '$db_name' does not exist. Creating..."
    sudo docker exec $MACA_FIN_DATABASE_CONTAINER psql \
      -U $MACA_FIN_DATABASE_USERNAME -d postgres -c \
      "CREATE DATABASE $db_name;"
    if [ $? -eq 0 ]; then
      echo "Database '$db_name' created successfully."
    else
      echo "Failed to create database '$db_name'."
      exit 1
    fi
  else
    echo "Database '$db_name' already exists."
  fi
}

# Wait for database container to be ready
echo "Waiting for database container to be ready..."
for i in {1..30}; do
  if sudo docker exec $MACA_FIN_DATABASE_CONTAINER pg_isready -U $MACA_FIN_DATABASE_USERNAME > /dev/null 2>&1; then
    echo "Database container is ready."
    break
  fi
  echo "Waiting for database... ($i/30)"
  sleep 2
done

# Create required databases
create_database_if_not_exists "$MACA_FIN_DATABASE_NAME"
create_database_if_not_exists "keycloak"

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
      if [ $? -ne 0 ]; then
        echo "Failed to execute $file"
        exit 1
      fi
      # execute mysql script
      # sudo docker exec -i $MACA_FIN_DATABASE_CONTAINER mysql \
      # -u$MACA_FIN_DATABASE_USERNAME -p$MACA_FIN_DATABASE_PASSWORD \
      # --default-character-set=utf8mb4 $MACA_FIN_DATABASE_NAME < "$file"
      # if [ $? -ne 0 ]; then
      #   echo "Failed to execute $file"
      #   exit 1
      # fi
    fi
  done
else
  echo "migrate folder not detected, skipping..."
fi
