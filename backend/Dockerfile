# Stage 1: Build the application
FROM maven:3.9.2-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy only the pom.xml first (for caching dependencies)
COPY backend/pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy the source code
COPY backend/src ./src

# Package the application, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/greencart-logistics-0.0.1-SNAPSHOT.jar ./greencart-logistics.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "greencart-logistics.jar"]
