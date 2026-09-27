# syntax=docker/dockerfile:1.6

# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

# Cache de dependencias primeiro para aproveitar layers
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests \
    && mv target/*.jar target/app.jar

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S pokedeck && adduser -S pokedeck -G pokedeck \
    && mkdir -p /data /app \
    && chown -R pokedeck:pokedeck /data /app

USER pokedeck
WORKDIR /app

COPY --from=builder --chown=pokedeck:pokedeck /workspace/target/app.jar /app/app.jar

VOLUME ["/data"]
EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
