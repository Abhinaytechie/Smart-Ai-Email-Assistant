# Use OpenJDK 21 as the base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file to the container
COPY target/email-ai-writer-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Set environment variables (replace with actual values in Render)
ENV GEMINI_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="
ENV GEMINI_KEY="YOUR_GEMINI_KEY_HERE"

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
