FROM vegardit/graalvm-maven:21.0.0 AS builder
WORKDIR /build/

# Download dependencies first - Docker Layer Caching
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2/repository  \
    mvn dependency:go-offline && \
    mvn dependency:resolve-plugins && \
    mvn verify --fail-never

# Build the JAR
COPY src ./src/
RUN --mount=type=cache,target=/root/.m2/repository  \
    mvn clean package -Pnative -DskipTests

# Run the JAR file stage
FROM ghcr.io/graalvm/native-image-community:21.0.1 AS production

# Add a spring user to run our application so that it doesn't need to run as root
RUN groupadd spring && adduser -g spring spring
USER spring:spring

WORKDIR /usr/src/passportease

# Copy the jar to the production image from the builder stage.
COPY --from=builder /build/target/*.jar ./passportease.jar

# Run the web service on container startup.
ENTRYPOINT ["java","-jar","./passportease.jar"]