pipeline {
    agent any

    environment {
        GITHUB_CREDENTIALS_ID = credentials('github-credentials-id')  // Jenkins에 저장된 자격증명 ID
        GIT_BRANCH = 'dev'
        GITHUB_REPO_URL ='https://github.com/Basic-Community-Platform/back-end.git'

    }

    triggers {
        // GitHub 웹훅 트리거
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    git branch: GIT_BRANCH, credentialsId: "${GITHUB_CREDENTIALS_ID}", url: GITHUB_REPO_URL
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // Jenkins에서 환경 변수 불러오기
                    def gradleCommand = env.GRADLE_BUILD_COMMAND ?: './gradlew clean build'
                    sh gradleCommand
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def dockerImageName = env.DOCKER_IMAGE_NAME
                    sh "docker build -t ${dockerImageName}:latest ."
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def dockerContainerName = env.DOCKER_CONTAINER_NAME
                    def dockerPort = env.DOCKER_PORT ?: '8080'
                    sh """
                    docker stop ${dockerContainerName} || true
                    docker rm ${dockerContainerName} || true
                    docker run -d -p 80:${dockerPort} --name ${dockerContainerName} ${env.DOCKER_IMAGE_NAME}:latest
                    """
                }
            }
        }
    }
}
