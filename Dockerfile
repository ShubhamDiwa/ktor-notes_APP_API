FROM ubuntu:latest
LABEL authors="shubhamdiwakar"

ENTRYPOINT ["top", "-b"]

# Use an official Gradle image to build the application
FROM gradle:7.5-jdk11 AS build

# Set the working directory
WORKDIR /app

# Copy the build.gradle and settings.gradle files
COPY build.gradle.kts settings.gradle.kts ./

# Copy the source code
COPY src ./src

# Build the application
RUN gradle clean build -x test

# Use an official OpenJDK runtime as a base image for the final stage
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/your-app.jar ./your-app.jar

# Expose the port your application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "your-app.jar"]
