FROM maven:3.9.3 AS builder
WORKDIR /build/

# Download dependencies first (dependency:go-offline issue here https://issues.apache.org/jira/browse/MDEP-82) - Docker Layer Caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Build the JAR - Skip Tests because we don't have a DB yet
COPY src ./src/
RUN mvn clean package -DskipTests

# Run the JAR file stage
FROM openjdk:22-slim AS production

# Install curl
RUN apt-get update
RUN  apt-get install -y curl

# Add a spring user to run our application so that it doesn't need to run as root
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

WORKDIR /usr/src/passportease

# Copy the jar to the production image from the builder stage.
COPY --from=builder /build/target/*.jar ./passportease.jar

# Run the web service on container startup.
ENTRYPOINT ["java","-jar","./passportease.jar"]