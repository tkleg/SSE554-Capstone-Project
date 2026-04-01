#!/bin/bash

jdeps -dotoutput deps target/classes

python3.12 graph_filter.py

# 3. Render the filtered DOT file
dot -Tpng deps/filtered_classes.dot -o java-class-deps.png

echo "Class-level dependency graph generated: java-class-deps.png"