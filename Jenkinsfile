pipeline {
    agent any

    environment {
        GITHUB_CREDENTIALS_ID = credentials('github-credentials-id')  // Jenkins에 저장된 자격증명 ID
        GIT_BRANCH = 'dev'
        GITHUB_REPO_URL = 'https://github.com/Basic-Community-Platform/back-end.git'
        DOCKER_IMAGE_NAME = 'community-hub'
        DOCKER_CONTAINER_NAME = 'community-hub-container'
        DOCKER_PORT = '8080'

        // JWT 환경 변수 (Jenkins Credentials로 저장된 값을 불러옴)
        JWT_SECRET_KEY = credentials('JWT_SECRET_KEY')  // 'jwt.secret-key'에 해당하는 값
        JWT_ACCESS_TOKEN_EXPIRE_TIME = '21600000'
        JWT_REFRESH_TOKEN_EXPIRE_TIME = '604800000'
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

                    // Jenkins에서 환경 변수 불러오기
                    def gradleCommand = env.GRADLE_BUILD_COMMAND ?: './gradlew clean build'

                    // JWT 환경 변수를 명시적으로 Gradle 빌드에 전달
                    sh """
                        ./gradlew clean build \
                        -Djwt.secret-key=${JWT_SECRET_KEY} \
                        -Djwt.access-token-expire-time=${JWT_ACCESS_TOKEN_EXPIRE_TIME} \
                        -Djwt.refresh-token-expire-time=${JWT_REFRESH_TOKEN_EXPIRE_TIME}
                    """
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
