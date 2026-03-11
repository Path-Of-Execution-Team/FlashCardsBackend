# ---- Build ----
FROM --platform=$BUILDPLATFORM maven:3-eclipse-temurin-21 AS build
WORKDIR /workspace

ARG MAVEN_OPTS="-Xmx1g"
COPY pom.xml .

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -DskipTests -e package

# ---- Run ----
FROM --platform=$TARGETPLATFORM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -u 1001 appuser
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
COPY --from=build /workspace/target/app.jar /app/app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
