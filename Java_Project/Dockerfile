# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the compiled JAR file into the container
COPY /target/ecom-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on (optional)
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
