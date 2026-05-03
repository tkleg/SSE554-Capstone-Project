#!/bin/bash

# Run Maven clean and package
mvn clean package

# Make sure that the necessary directories exist and are empty
rm -rf generated_docs
mkdir -p generated_docs/coverage
mkdir generated_docs/javadocs
mkdir generated_docs/dependency_graph
rm -rf distribution
mkdir distribution

#Copy test coverage and javadocs to generated_docs directory
cp -r target/site/jacoco/* generated_docs/coverage
cp -r target/reports/apidocs/* generated_docs/javadocs

#Make the dependency graphs and remove .dot files after generating PNGs
jdeps -dotoutput generated_docs/dependency_graph -verbose:class -filter:none target/classes
python3.12 graph_filter.py
dot -Tpng generated_docs/dependency_graph/filtered_classes.dot -o generated_docs/dependency_graph/deps.png
dot -Tpng generated_docs/dependency_graph/filtered_cleaner_classes.dot -o generated_docs/dependency_graph/deps_cleaner.png
dot -Tpng generated_docs/dependency_graph/filtered_no_item_repo.dot -o generated_docs/dependency_graph/deps_no_item_repo.png
dot -Tpng generated_docs/dependency_graph/filtered_no_item_repo_no_entities.dot -o generated_docs/dependency_graph/deps_no_item_repo_no_entities.png
rm generated_docs/dependency_graph/*.dot

#Copy JAR file, dependencies, data, and the run script to the distribution directory
cp target/Project-1.0-SNAPSHOT-with-dependencies.jar distribution
cp -r target/dependency distribution
cp -r data distribution
cp run.sh distribution