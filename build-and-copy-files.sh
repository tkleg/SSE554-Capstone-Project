#!/bin/bash

# Run Maven clean and package
mvn clean package

# Copy necessary files to distribution directory
cp target/Project-1.0-SNAPSHOT-with-dependencies.jar distribution
cp -r target/dependency distribution
cp -r data target/data