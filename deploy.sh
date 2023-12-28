AWS_REGION="us-east-1"
AWS_ACCOUNT_ID="931916374817"  # Replace with your AWS account ID
ECR_REPO_NAME="jenkins-pipeline"  # Replace with your ECR repository name

# Authenticate Docker to the ECR registry
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO_NAME

# Get the ID of the most recently created container
CONTAINER_ID=$(docker ps -lq)
echo $CONTAINER_ID
if [ -n "$CONTAINER_ID" ]
then
    # Container is running, stop it
    echo "Stopping the running container..."
    docker stop ${CONTAINER_ID}
    echo "Container stopped."
fi

# Pull the Docker image
DOCKER_IMAGE="pardhuguttula/ansible"  # Replace with your Docker image name
DOCKER_TAG=$(curl -s "https://api.github.com/repos/Pardhu-Guttula/jenkins-ansible/commits/main" | jq -r '.sha' | cut -c1-7)

docker pull $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO_NAME:$DOCKER_TAG

# Remove invalid characters from Docker image name and tag
DOCKER_IMAGE_NAME=$(echo "$DOCKER_IMAGE" | tr -cd '[:alnum:]._-' | tr -s '-' | tr '[:upper:]' '[:lower:]')
DOCKER_TAG_NAME=$(echo "$DOCKER_TAG" | tr -cd '[:alnum:]._-' | tr -s '-' | tr '[:upper:]' '[:lower:]')

# Combine Docker image name and tag to create the container name
CONTAINER_NAME="${DOCKER_IMAGE_NAME}-${DOCKER_TAG_NAME}"

# Run the new Docker container
docker run -d --name "${CONTAINER_NAME}" -p 8088:80 $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO_NAME:$DOCKER_TAG

echo "New container started with Docker image: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO_NAME:$DOCKER_TAG"