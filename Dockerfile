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
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["/bin/sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
