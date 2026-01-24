# Hunt the Wumpus - Docker Image
# Uses Alpine-based Java runtime with Graphviz for visualization support
#
# Requires pre-built JAR: run 'mvn package' before 'docker build'
#
# Usage:
#   docker build --build-arg JAR_FILE=wumpus-1.0.12.jar -t wumpus .
#   docker run --rm -it wumpus

FROM alpine:3.19

# JAR file name (shaded JAR, not original-*)
ARG JAR_FILE

# Install OpenJDK JRE and Graphviz for visualization support
RUN apk add --no-cache openjdk17-jre graphviz ttf-dejavu

WORKDIR /app

# Copy the specified shaded JAR
COPY ${JAR_FILE} /app/wumpus.jar

ENTRYPOINT ["java", "-jar", "/app/wumpus.jar"]

# Default to no arguments (can be overridden)
CMD []
