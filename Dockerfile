FROM vegardit/graalvm-maven:21.0.0 AS builder
WORKDIR /build/

# Log the version of GraalVM, Maven and Java
RUN echo "Java Version:" && echo $(java -version)
RUN echo "GraalVM Version:" && echo $(native-image --version)
RUN echo "Maven Version:" && echo $(mvn -version)


# Download dependencies first - Docker Layer Caching
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:go-offline
RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:resolve-plugins
RUN --mount=type=cache,target=/root/.m2/repository mvn verify --fail-never

# Build the JAR
COPY src ./src/
RUN --mount=type=cache,target=/root/.m2/repository mvn clean package -Pnative -DskipTests

# Run the JAR file stage
FROM ghcr.io/graalvm/native-image-community:21.0.0 AS production

RUN echo "Java Version:" && echo $(java -version)
RUN echo "GraalVM Version:" && echo $(native-image --version)

# Install curl with rpm
RUN microdnf install curl

# Add a spring user to run our application so that it doesn't need to run as root
RUN groupadd spring && adduser -g spring spring
USER spring:spring

WORKDIR /usr/src/passportease

# Copy the jar to the production image from the builder stage.
COPY --from=builder /build/target/*.jar ./passportease.jar

# Run the web service on container startup.
ENTRYPOINT ["native-image","-jar","./passportease.jar"]