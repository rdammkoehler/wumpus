#!/bin/bash
#
# Updates the static class diagram PNG from the PlantUML source.
# Usage: ./bin/update-class-diagram.sh
#

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DOCS_DIR="$PROJECT_DIR/docs"
PUML_FILE="$DOCS_DIR/static_class_diagram.puml"
PNG_FILE="$DOCS_DIR/static_class_diagram.png"
PLANTUML_JAR="/tmp/plantuml.jar"
PLANTUML_VERSION="1.2024.7"
PLANTUML_URL="https://github.com/plantuml/plantuml/releases/download/v${PLANTUML_VERSION}/plantuml-${PLANTUML_VERSION}.jar"

# Find Java
find_java() {
    # Check Homebrew OpenJDK first (common on macOS)
    local homebrew_java=$(find /opt/homebrew/Cellar/openjdk -name "java" -type f 2>/dev/null | head -1)
    if [[ -n "$homebrew_java" && -x "$homebrew_java" ]]; then
        echo "$homebrew_java"
        return 0
    fi

    # Check JAVA_HOME
    if [[ -n "$JAVA_HOME" && -x "$JAVA_HOME/bin/java" ]]; then
        echo "$JAVA_HOME/bin/java"
        return 0
    fi

    # Check macOS java_home
    if [[ -x /usr/libexec/java_home ]]; then
        local java_home=$(/usr/libexec/java_home 2>/dev/null)
        if [[ -n "$java_home" && -x "$java_home/bin/java" ]]; then
            echo "$java_home/bin/java"
            return 0
        fi
    fi

    # Try java in PATH (test it actually works)
    if command -v java &> /dev/null && java -version &> /dev/null; then
        echo "java"
        return 0
    fi

    return 1
}

# Check for Graphviz
check_graphviz() {
    if ! command -v dot &> /dev/null; then
        echo "Error: Graphviz (dot) is not installed."
        echo "Install with: brew install graphviz"
        exit 1
    fi
}

# Download PlantUML if needed
download_plantuml() {
    if [[ ! -f "$PLANTUML_JAR" ]]; then
        echo "Downloading PlantUML ${PLANTUML_VERSION}..."
        curl -sL -o "$PLANTUML_JAR" "$PLANTUML_URL"
        echo "Downloaded to $PLANTUML_JAR"
    fi
}

# Main
main() {
    echo "Updating static class diagram..."

    # Check prerequisites
    check_graphviz

    JAVA=$(find_java)
    if [[ -z "$JAVA" ]]; then
        echo "Error: Java not found. Please install Java or set JAVA_HOME."
        exit 1
    fi
    echo "Using Java: $JAVA"

    # Check source file exists
    if [[ ! -f "$PUML_FILE" ]]; then
        echo "Error: PlantUML source not found: $PUML_FILE"
        exit 1
    fi

    # Download PlantUML
    download_plantuml

    # Generate PNG
    echo "Generating PNG from $PUML_FILE..."
    "$JAVA" -jar "$PLANTUML_JAR" -tpng "$PUML_FILE" -o "$(dirname "$PNG_FILE")"

    # Verify output
    if [[ -f "$PNG_FILE" ]]; then
        echo "Success: $PNG_FILE updated"
        ls -lh "$PNG_FILE"
    else
        echo "Error: Failed to generate PNG"
        exit 1
    fi
}

main "$@"
