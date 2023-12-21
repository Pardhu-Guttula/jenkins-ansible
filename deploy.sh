#!/bin/bash

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

docker pull pardhuguttula/ansible:$DOCKER_TAG
docker run -d --name ansible -p 8088:80 pardhuguttula/ansible:$DOCKER_TAG