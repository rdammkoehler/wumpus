#!/usr/bin/env bash
set -e
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  SCRIPT_DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$SCRIPT_DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
SCRIPT_DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

pushd "${SCRIPT_DIR}" > /dev/null

source wump

ABSOLUTE_JAR_FILE_PATH=$(find_jar)
TARGET_DIR=$(basename $(dirname "$ABSOLUTE_JAR_FILE_PATH"))
RELATIVE_JAR_FILE_PATH="$TARGET_DIR/"$(basename "$ABSOLUTE_JAR_FILE_PATH")

pushd "$PROJECT_DIR" > /dev/null
docker build --build-arg JAR_FILE="$RELATIVE_JAR_FILE_PATH" -t wumpus .

popd > /dev/null
popd > /dev/null