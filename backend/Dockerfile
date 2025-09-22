# Use Maven + JDK 17 image to build
FROM maven:3.9.2-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the Spring Boot jar
RUN mvn clean package -DskipTests

# Use minimal JRE image to run the jar
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/target/greencart-logistics-0.0.1-SNAPSHOT.jar ./greencart-logistics.jar

# Expose port (Spring Boot default 8080)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "greencart-logistics.jar"]
