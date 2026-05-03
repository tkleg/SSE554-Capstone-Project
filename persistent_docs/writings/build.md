# My JavaFX Application

## How to Build and Run from Source Code
1. Clone the repository
2. Navigate to the project directory
3. Ensure requirements are met (see below for details)
4. Run the script `./build-and-copy-files.sh` to build the project and move files to the right location
5. Go to `How to Run from JAR` below to run the application from the generated JAR file.

### Running from VSCode
1. Do steps 1-3 above
2. Open the project in VSCode
3. Go to the **Run and Debug** tab on the left sidebar
4. Select **Main** and click the green play button

## How to Run from JAR
   1. Clone the repository
   2. Navigate to the `distribution` directory
   3. Run `run.sh`

### Manual (if needed):
First, ensure you are in the `distribution` directory, then run the following command in the terminal.
```bash
java --module-path "dependency" --add-modules javafx.controls,javafx.fxml -jar Project-1.0-SNAPSHOT-with-dependencies.jar
```

## System Requirements
- Java 17 or higher
- Maven
- Graphviz (for generating dependency graphs)
- Python 3.12 (for running the graph filtering script)
- Bash (for running the launch script on Linux/Mac)
- Recommended 8GB of RAM or higher