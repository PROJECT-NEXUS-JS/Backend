# 1단계: Gradle을 사용하여 애플리케이션 빌드
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 2단계: 빌드된 애플리케이션을 JRE가 설치된 이미지로 복사
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
