# -----------------------------------------------------------------
# STAGE 1: BUILD THE SPRING BOOT APPLICATION (Use a known working tag)
# -----------------------------------------------------------------
# **CORRECTED LINE:** Using the stable 'eclipse-temurin-21' tag for Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file first to download dependencies (Cache Layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the final JAR file, skipping tests
RUN mvn clean package -DskipTests

# -----------------------------------------------------------------
# STAGE 2: CREATE THE FINAL RUNTIME IMAGE
# -----------------------------------------------------------------
# This tag is confirmed to be correct and did not cause the failure.
FROM eclipse-temurin:21-jre

# Set the working directory for the application
WORKDIR /app

# Copy the application JAR from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Expose the default port for Spring Boot
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]