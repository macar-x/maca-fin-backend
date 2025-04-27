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
if [ -z "$TARGET_IMAGE_VERSION" ]; then
  echo "TARGET_IMAGE_VERSION is empty, please check .env file."
  exit 255
fi
if [ -z "$TZ" ]; then
  echo "Timezone is empty, please check .env file."
  exit 255
fi

# remove existed images.
sudo docker rmi maca-cloud/maca-fin-backend:"$TARGET_IMAGE_VERSION"

# build new images.
sudo docker build -t maca-cloud/maca-fin-backend:"$TARGET_IMAGE_VERSION" .
