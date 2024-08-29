FROM ubuntu:latest
LABEL authors="shubhamdiwakar"

ENTRYPOINT ["top", "-b"]

# Stage 1: Build the application
FROM gradle:7.5.0-jdk17 AS build
WORKDIR /app

# Copy all files into the container
COPY . .

# Build the application (skip tests for faster build)
RUN gradle clean build -x test

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/ktor-notes_APP_API-all.jar /app/ktor-notes_APP_API.jar

# Expose the port that the Ktor application will run on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "ktor-notes_APP_API.jar"]
