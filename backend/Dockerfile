# Step 1: Build JAR using Maven
FROM maven:3.9.6-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Step 2: Run the JAR
FROM amazoncorretto:17-alpine
WORKDIR /app

COPY --from=build /app/target/inventory-backend-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
