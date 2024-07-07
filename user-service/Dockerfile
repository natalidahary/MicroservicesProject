FROM openjdk:22-jdk-slim

# Set up the volume
VOLUME /tmp

# The application's JAR file
ARG JAR_FILE=target/*.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} app.jar

# Install Dapr CLI
RUN curl -fsSL https://raw.githubusercontent.com/dapr/cli/master/install/install.sh | /bin/bash

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app.jar"]
