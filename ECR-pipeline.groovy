pipeline {
    agent any
    environment {
        AWS_ACCOUNT_ID="931916374817"
        AWS_DEFAULT_REGION="us-east-1"
        IMAGE_REPO_NAME="jenkins-pipeline"
        IMAGE_TAG=getVersion()
        REPOSITORY_URI="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}"
        
    }

    stages {
        stage('Logging into AWS'){
            steps{
                script {
                    echo "${COMMIT_HASH}"
                    sh "aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
                }
            }
        }

        stage("SCM Checkout"){
            steps{
            git branch: 'main', credentialsId: 'Github', 
            url: 'https://github.com/Pardhu-Guttula/jenkins-ansible'
            }
        }

        stage('Build docker image') {
            steps {  
                script {
                    sh 'docker build -t ${REPOSITORY_URI}:${IMAGE_TAG} .'
                }
            }
        }

        stage('pushing to ECR') {
            steps{
                script {
                
                    sh "docker push ${REPOSITORY_URI}:${IMAGE_TAG}"
                }
            }
        }

        stage('Ansible') {
            steps{
                ansiblePlaybook credentialsId: 'target-server', disableHostKeyChecking: true, extras: '-e IMAGE_TAG=${IMAGE_TAG}', installation: 'ansible', inventory: 'dev.inv', playbook: 'deployment.yml', vaultTmpPath: ''
            }
        }

    }
}
def getVersion() {
    // Fetch commit hash from GitHub API
    def githubApiUrl = "https://api.github.com/repos/Pardhu-Guttula/jenkins-ansible/commits/main"
    def commitHashFromApi = sh(script: "curl -s $githubApiUrl | jq -r '.sha' | cut -c1-7", returnStdout: true).trim()

    // Set COMMIT_HASH and DOCKER_TAG
    COMMIT_HASH = commitHashFromApi
    DOCKER_TAG = commitHashFromApi

    // Print COMMIT_HASH (optional)
    echo "COMMIT_HASH: $COMMIT_HASH"

    // Return COMMIT_HASH
    return COMMIT_HASH
}