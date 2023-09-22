FROM maven:3-amazoncorretto-21-debian AS builder
WORKDIR /build/

# Download dependencies first - Docker Layer Caching
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:go-offline
RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:resolve-plugins
RUN --mount=type=cache,target=/root/.m2/repository mvn verify --fail-never

# Build the JAR
COPY src ./src/
RUN --mount=type=cache,target=/root/.m2/repository mvn clean package -Pnative -DskipTests

# Run the JAR file stage
FROM amazoncorretto:21.0.0-alpine3.18 AS production

# Install curl
RUN  apk add curl

# Add a spring user to run our application so that it doesn't need to run as root
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

WORKDIR /usr/src/passportease

# Copy the jar to the production image from the builder stage.
COPY --from=builder /build/target/*.jar ./passportease.jar

# Run the web service on container startup.
ENTRYPOINT ["java","-jar","./passportease.jar"]