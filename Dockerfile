# 베이스 이미지로 OpenJDK 17 사용
FROM openjdk:17-jdk-slim

# JAR 파일 복사 (실제 빌드된 파일명으로 변경)
COPY build/libs/community-hub-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
