#!/bin/bash

# Run Maven clean and package
mvn clean package

# Make sure that the necessary directories exist and are empty
rm -rf docs
mkdir -p docs/coverage
mkdir docs/javadocs
rm -rf distribution
mkdir distribution

#Copy test coverage and javadocs to docs directory
cp -r target/site/jacoco/* docs/coverage
cp -r target/reports/apidocs/* docs/javadocs

#Make the dependency graph (class-level)
jdeps -dotoutput docs/dependency_graph -verbose:class -filter:none target/classes
python3.12 graph_filter.py
dot -Tpng docs/dependency_graph/filtered_classes.dot -o docs/dependency_graph/deps.png
dot -Tpng docs/dependency_graph/filtered_cleaner_classes.dot -o docs/dependency_graph/deps_cleaner.png

#Copy jar file, dependencies, data, docs, and run scripts to distribution directory
cp target/Project-1.0-SNAPSHOT-with-dependencies.jar distribution
cp -r target/dependency distribution
cp -r data distribution
cp -r docs distribution
cp -r build/* distribution

#Zip distribution using PowerShell
powershell.exe "Compress-Archive -Path \"distribution\" -DestinationPath \"distribution.zip\" -Force"