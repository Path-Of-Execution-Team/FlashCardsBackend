# syntax=docker/dockerfile:1.7
# -----------------------------------------------------------------------------
# Build stage
# -----------------------------------------------------------------------------
FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /build

# Resolve dependencies in their own layer so source-only changes do not redownload
# the whole repository on every build.
COPY pom.xml ./
RUN mvn -B -ntp -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -ntp -DskipTests package

# Split the fat jar into Spring Boot layers: dependencies change rarely, the
# application class files change on every commit.
# layertools only materialises directories for layers that have content, and
# this project has no SNAPSHOT dependencies - pre-creating all four keeps the
# COPY instructions below from failing on a missing path.
RUN set -eu; \
    jar="$(find target -maxdepth 1 -name '*.jar' ! -name '*-sources.jar' ! -name 'original-*' | head -n 1)"; \
    java -Djarmode=layertools -jar "$jar" extract --destination /build/layers; \
    mkdir -p /build/layers/dependencies \
             /build/layers/spring-boot-loader \
             /build/layers/snapshot-dependencies \
             /build/layers/application

# -----------------------------------------------------------------------------
# Runtime stage
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine-3.22

# uid/gid must match runAsUser/runAsGroup in k8s/backend.yaml.
RUN addgroup -g 10001 -S app \
 && adduser -u 10001 -S app -G app

WORKDIR /app

# Ordered least- to most-frequently changed.
COPY --from=build --chown=10001:10001 /build/layers/dependencies/ ./
COPY --from=build --chown=10001:10001 /build/layers/spring-boot-loader/ ./
COPY --from=build --chown=10001:10001 /build/layers/snapshot-dependencies/ ./
COPY --from=build --chown=10001:10001 /build/layers/application/ ./

USER 10001:10001

EXPOSE 8080

# The container filesystem is read-only in Kubernetes; /tmp is an emptyDir.
ENV TMPDIR=/tmp \
    SERVER_PORT=8080 \
    SPRING_DOCKER_COMPOSE_ENABLED=false

# Spring Boot 3.2+ launcher coordinates. JVM sizing comes from JAVA_TOOL_OPTIONS
# set in the Deployment, so it stays tunable without a rebuild.
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
