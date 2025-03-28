# Use OpenJDK 21 as the base image
FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

# Copy the project files
COPY . .

# Build the application inside the container
RUN ./mvnw clean package -DskipTests

# Expose the port
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "target/email-ai-writer-0.0.1-SNAPSHOT.jar"]
