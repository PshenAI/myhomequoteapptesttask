#!/bin/bash

APP_NAME="testtask"
JAR_NAME="target/${APP_NAME}-1.0.jar"

echo "Building the project with Maven..."
mvn clean package

# Check if build succeeded
if [ $? -ne 0 ]; then
  echo "Build failed. Exiting."
  exit 1
fi

echo "Build successful. Running the application..."
java -jar ${JAR_NAME}