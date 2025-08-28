# ---- Build ----
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Run ----
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["/bin/sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
