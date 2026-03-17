#!/bin/bash

# Run Maven clean and package
mvn clean package

# Copy necessary files to distribution and docs directory
cp target/Project-1.0-SNAPSHOT-with-dependencies.jar distribution
cp target/search-engine-standalone.jar distribution
cp -r target/dependency distribution
cp -r data distribution
cp -r target/site docs
    
#Zip distribution using PowerShell
powershell.exe "Compress-Archive -Path \"distribution\" -DestinationPath \"distribution.zip\" -Force"