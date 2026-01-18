# Hunt the Wumpus - Docker Image
# Uses Alpine-based Java runtime with Graphviz for visualization support

FROM alpine:3.19 AS builder

# Install OpenJDK and Maven for building
RUN apk add --no-cache openjdk17 maven

WORKDIR /build

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -q || true

# Copy source and build
COPY src ./src
RUN mvn package -q

# Runtime image - minimal Alpine with JRE
FROM alpine:3.19

# Install OpenJDK JRE and Graphviz for visualization support
RUN apk add --no-cache openjdk17-jre graphviz ttf-dejavu

WORKDIR /app

# Copy the shaded JAR from builder
COPY --from=builder /build/target/wumpus-1.0-SNAPSHOT.jar /app/wumpus.jar

# Set entry point to run the game
ENTRYPOINT ["java", "-jar", "/app/wumpus.jar"]

# Default to no arguments (can be overridden)
CMD []
