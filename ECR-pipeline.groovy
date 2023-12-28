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
                    sh "aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | 
                    docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
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
                    dockerImage = docker.build "${IMAGE_REPO_NAME}:${IMAGE_TAG}" 
                }
            }
        }

        stage('pushing to ECR') {
            steps{
                script {
                    sh "docker tag ${IMAGE_REPO_NAME}:${IMAGE_TAG} ${REPOSITORY_URI}:${IMAGE_TAG}"
                    sh "docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}:${IMAGE_TAG}"
                }
            }
        }

        stage('Ansible') {
            steps{
                ansiblePlaybook credentialsId: 'target-server', disableHostKeyChecking: true, extras: "-e DOCKER_TAG=${DOCKER_TAG}", installation: 'ansible', inventory: 'dev.inv', playbook: 'deployment.yml', vaultTmpPath: ''
            }
        }

    }
}
def getVersion(){
    def commitHash = sh label:'', returnStdout: true, script: 'git rev-parse --short HEAD'
    return commitHash
}