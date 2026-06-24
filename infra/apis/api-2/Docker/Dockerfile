FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x gradlew && ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
RUN apk add --no-cache wget curl \
    && addgroup -S vocealuga && adduser -S vocealuga -G vocealuga

COPY --from=builder /app/build/libs/*.jar app.jar
RUN chown vocealuga:vocealuga app.jar

USER vocealuga
ENV SPRING_PROFILES_ACTIVE=web
ENV SERVER_PORT=8082
EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:8082/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
