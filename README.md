# Jenkins-Ansible Automation

This repository contains files and scripts for automating deployment using Jenkins and Ansible.

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Repository Structure](#repository-structure)
- [Jenkins Pipeline](#jenkins-pipeline)
- [Ansible Playbook](#ansible-playbook)
- [Docker Integration](#docker-integration)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Overview

This repository provides automation scripts for deploying applications using Jenkins and Ansible. It includes a Jenkins pipeline for building and pushing a Docker image to Dockerhub and an Ansible playbook for deploying the application to a target server.

## Prerequisites

- Jenkins installed and configured
- Ansible installed on the Jenkins server
- Docker installed on the target server
- Dockerhub account for image repository

## Repository Structure

- [Jenkinsfile](Jenkinsfile): Jenkins pipeline script for building and pushing Docker image.
- [ansible-playbook.yml](ansible-playbook.yml): Ansible playbook for deploying the application.
- [deploy.sh](deploy.sh): Bash script for stopping the running Docker container before deployment.
- [Dockerfile](Dockerfile): Dockerfile for building the application image.
- [dev.inv](dev.inv): Ansible inventory file for specifying target servers.

## Jenkins Pipeline

The Jenkins pipeline defined in `Jenkinsfile` performs the following steps:
1. SCM Checkout: Fetches the source code from the GitHub repository.
2. Build Docker Image: Builds a Docker image with a version derived from the Git commit hash.
3. Dockerhub Login: Logs in to Dockerhub using credentials stored in Jenkins.
4. Push Image to Dockerhub: Pushes the built Docker image to Dockerhub.
5. Ansible Deployment: Uses Ansible to deploy the Docker image to a target server.

## Ansible Playbook

The Ansible playbook (`ansible-playbook.yml`) automates the following tasks:
1. Updates the apt package cache on the target server.
2. Upgrades installed packages.
3. Installs required system packages.
4. Adds Docker GPG apt key and repository.
5. Installs Docker and Docker Module for Python.

## Docker Integration

The Docker integration includes a Dockerfile (`Dockerfile`) for packaging the application and a Bash script (`deploy.sh`) for managing Docker containers.

## Usage

1. Configure Jenkins credentials for Dockerhub and target server.
2. Adjust Jenkins pipeline and Ansible playbook variables as needed.
3. Run the Jenkins pipeline to trigger the deployment process.