pipeline {
    agent any

    environment {
        GITHUB_CREDENTIALS_ID = credentials('github-credentials-id')  // Jenkins에 저장된 자격증명 ID
        GIT_BRANCH = 'dev'
        GITHUB_REPO_URL = 'https://github.com/Basic-Community-Platform/back-end.git'
        DOCKER_IMAGE_NAME = 'community-hub'
        DOCKER_CONTAINER_NAME = 'community-hub-container'
        DOCKER_PORT = '8080'
    }

    triggers {
        // GitHub 웹훅 트리거
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    git branch: "${GIT_BRANCH}", credentialsId: "${GITHUB_CREDENTIALS_ID}", url: "${GITHUB_REPO_URL}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // gradlew 파일에 실행 권한 부여
                    sh 'chmod +x ./gradlew'

                    // JWT 비밀 키를 안전하게 처리하기 위해 withCredentials 블록 사용
                    withCredentials([string(credentialsId: 'JWT_SECRET_KEY', variable: 'JWT_SECRET_KEY')]) {
                        // Gradle 빌드 실행
                        sh """
                            ./gradlew clean build \
                            -Djwt.secret-key=$JWT_SECRET_KEY \
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def dockerImageName = env.DOCKER_IMAGE_NAME ?: 'community-hub'
                    sh "docker build -t ${dockerImageName}:latest ."
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def dockerContainerName = env.DOCKER_CONTAINER_NAME ?: 'community-hub-container'
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
