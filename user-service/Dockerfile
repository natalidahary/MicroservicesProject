# Stage 1: Get the daprd binary from dapr image
FROM daprio/daprd:1.13.5 AS dapr

# Stage 2: Build the application
FROM openjdk:17-jdk-slim AS build

# The application's JAR file
ARG JAR_FILE=target/*.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} /app.jar

# Stage 3: Create the final runtime image
FROM openjdk:17-jdk-slim

# Install bash, iputils-ping, and netcat for troubleshooting
RUN apt-get update && apt-get install -y bash iputils-ping netcat && rm -rf /var/lib/apt/lists/*

# Set up the volume
VOLUME /tmp

# Copy the built app.jar from the build stage
COPY --from=build /app.jar /app.jar

# Copy the daprd binary from the dapr image
COPY --from=dapr /daprd /usr/local/bin/daprd

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
