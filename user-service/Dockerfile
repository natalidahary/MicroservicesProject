# Stage 1: Build
FROM openjdk:22-jdk-slim AS build

# Set up the volume
VOLUME /tmp

# The application's JAR file
ARG JAR_FILE=target/*.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} app.jar

# Copy the local dapr binary into the build
COPY dapr /usr/local/bin/dapr

# Stage 2: Run
FROM openjdk:22-jdk-slim

# Set up the volume
VOLUME /tmp

# Copy the built app.jar from the build stage
COPY --from=build /app.jar app.jar
COPY --from=build /usr/local/bin/dapr /usr/local/bin/dapr

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java","-jar","/app.jar"]
