# Script to rebuild images
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
if [ -z "$MACA_FIN_OIDC_SERVER_URI" ]; then
  echo "MACA_FIN_OIDC_SERVER_URI is empty, please check .env file."
  exit 255
fi
if [ -z "$MACA_FIN_OIDC_ADMIN_SECRET" ]; then
  echo "MACA_FIN_OIDC_ADMIN_SECRET is empty, please check .env file."
  exit 255
fi

# remove existed images.
sudo docker rmi maca-cloud/maca-fin-backend:"$TARGET_IMAGE_VERSION"

# build new images.
sudo docker build -t maca-cloud/maca-fin-backend:"$TARGET_IMAGE_VERSION" .
