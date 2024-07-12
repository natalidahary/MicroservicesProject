# Use an appropriate base image for Dapr sidecar
FROM daprio/daprd:1.13.5 AS dapr

# Stage 1: Build the application
FROM openjdk:17-jdk-slim AS build

# The application's JAR file
ARG JAR_FILE=target/*.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} app.jar

# Stage 2: Run
FROM openjdk:17-jdk-slim

# Set up the volume
VOLUME /tmp

# Copy the built app.jar from the build stage
COPY --from=build /app.jar app.jar

# Copy the daprd binary from the dapr image
COPY --from=dapr /daprd /usr/local/bin/daprd

# Expose the application port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
