pipeline {
    agent any

    environment {
        GITHUB_CREDENTIALS_ID = ''
        GITHUB_REPO_URL = ''
        GIT_BRANCH = ''
        DOCKER_IMAGE_NAME = ''
        DOCKER_CONTAINER_NAME = ''
        DOCKER_PORT = ''
    }

    triggers {
        // GitHub 웹훅 트리거
        githubPush()
    }

    stages {
        stage('Load Properties') {
            steps {
                script {
                    // 외부 properties 파일 로드
                    def props = readProperties file: 'jenkins.properties'
                    env.GITHUB_CREDENTIALS_ID = props['GITHUB_CREDENTIALS_ID']
                    env.GITHUB_REPO_URL = props['GITHUB_REPO_URL']
                    env.GIT_BRANCH = props['GIT_BRANCH']
                    env.DOCKER_IMAGE_NAME = props['DOCKER_IMAGE_NAME']
                    env.DOCKER_CONTAINER_NAME = props['DOCKER_CONTAINER_NAME']
                    env.DOCKER_PORT = props['DOCKER_PORT']
                }
            }
        }

        stage('Checkout') {
            steps {
                // GitHub 리포지토리에서 코드 체크아웃
                git branch: "${GIT_BRANCH}", credentialsId: "${GITHUB_CREDENTIALS_ID}", url: "${GITHUB_REPO_URL}"
            }
        }

        stage('Build') {
            steps {
                // Gradle로 빌드
                sh './gradlew clean build'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Docker 이미지 빌드
                sh "docker build -t ${DOCKER_IMAGE_NAME}:latest ."
            }
        }

        stage('Deploy') {
            steps {
                // 기존 컨테이너 중지 및 삭제
                sh """
                docker stop ${DOCKER_CONTAINER_NAME} || true
                docker rm ${DOCKER_CONTAINER_NAME} || true
                """

                // 새로운 컨테이너 실행
                sh "docker run -d -p 80:${DOCKER_PORT} --name ${DOCKER_CONTAINER_NAME} ${DOCKER_IMAGE_NAME}:latest"
            }
        }
    }
}
