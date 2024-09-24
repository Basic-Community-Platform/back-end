pipeline {
    agent any

    environment {
        // Jenkins 환경에서 설정된 자격 증명 및 환경 변수 사용
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
                    // GitHub에서 코드 체크아웃
                    git branch: "${GIT_BRANCH}", credentialsId: "${GITHUB_CREDENTIALS_ID}", url: "${GITHUB_REPO_URL}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // gradlew 파일에 실행 권한 부여
                    sh 'chmod +x ./gradlew'

                    // 민감한 정보를 안전하게 처리
                    withCredentials([string(credentialsId: 'JWT_SECRET_KEY', variable: 'JWT_SECRET_KEY')]) {
                        // Gradle 빌드 실행
                        sh """
                            ./gradlew clean build \
                            -Djwt.secret-key=$JWT_SECRET_KEY
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Docker 이미지를 빌드
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:latest ."
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // 기존 Docker 컨테이너 중지 및 제거 후 새 컨테이너 실행
                    sh """
                    docker stop ${DOCKER_CONTAINER_NAME} || true
                    docker rm ${DOCKER_CONTAINER_NAME} || true
                    docker run -d -p 80:${DOCKER_PORT} --name ${DOCKER_CONTAINER_NAME} ${DOCKER_IMAGE_NAME}:latest
                    """
                }
            }
        }
    }

    post {
        always {
            // 워크스페이스 정리
            cleanWs()
        }
    }
}
