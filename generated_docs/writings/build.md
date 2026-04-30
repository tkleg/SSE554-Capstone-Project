# My JavaFX Application

## How to Build and Run from Source Code
1. Clone the repository
2. Navigate to the project directory
3. Ensure the following are installed:
   - Java 17 or higher
   - Maven
   - Graphviz (for generating dependency graphs)
   - Python 3.12 (for running the graph filtering script)
   - Bash (for running the launch script on Linux/Mac)
4. Run the script `./build-and-copy-files.sh` to build the project and move files to the right location
5. Navigate to the `distribution` directory and run the appropriate launch script for your operating system (see below for instructions on how to run from JAR)
6. Enjoy the application!

### Running from VSCode
1. Do steps 1-3 above
2. Open the project in VSCode
3. Go to the **Run and Debug** tab on the left sidebar
4. Select **Main** and click the green play button

## How to Run from JAR
   1. Clone the repository
   2. Navigate to the `distribution` directory
### Windows:
Run `run.bat`

### Linux/Mac:
```bash
chmod +x run.sh
./run.sh
```

### Manual (if needed):
```bash
java --module-path "dependency" --add-modules javafx.controls,javafx.fxml -jar Project-1.0-SNAPSHOT-with-dependencies.jar
```
## Requirements
- Java 17 or higher
- No additional installations needed - JavaFX and all other dependencies are included

## Contents
- `Project-1.0-SNAPSHOT-with-dependencies.jar` - Main application JAR file
- `dependency/` - Dependency JAR files
- `data/` - Application data files
- `run.bat` / `run.sh` - Launch scripts
- `docs` - Documentation