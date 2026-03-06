@echo off
echo Starting Search Engine...
echo.

REM Check if search engine JAR exists
if exist "search-engine-standalone.jar" (
    echo Running Search Engine...
    echo.
    java -jar search-engine-standalone.jar
) else (
    echo Search engine JAR not found!
    echo Please run build-and-copy-files.sh first.
    pause
)