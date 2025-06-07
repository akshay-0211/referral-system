# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 