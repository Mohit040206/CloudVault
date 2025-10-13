# -----------------------------------------------------------------
# STAGE 1: BUILD THE SPRING BOOT APPLICATION (maven:3.9.2-jdk-21)
# -----------------------------------------------------------------
# Use the correct Maven image with OpenJDK 21 for building the project.
FROM maven:3.9.2-jdk-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file first to download dependencies.
# This step creates a cache layer that only invalidates if pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the final JAR file, skipping tests
RUN mvn clean package -DskipTests

# -----------------------------------------------------------------
# STAGE 2: CREATE THE FINAL RUNTIME IMAGE (eclipse-temurin:21-jre)
# -----------------------------------------------------------------
# Use a lightweight JRE image to run the application.
# This significantly reduces the final image size and attack surface.
FROM eclipse-temurin:21-jre

# Set the working directory for the application
WORKDIR /app

# Copy the application JAR from the 'build' stage
# The name '*.jar' accounts for the file potentially including the version (e.g., cloudvault-0.0.1-SNAPSHOT.jar)
COPY --from=build /app/target/*.jar app.jar

# Expose the default port for Spring Boot
EXPOSE 8080

# Define the command to run the application
# Use the executable form (JSON array) for better signal handling
ENTRYPOINT ["java", "-jar", "app.jar"]