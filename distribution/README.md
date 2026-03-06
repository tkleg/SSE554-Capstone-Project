# My JavaFX Application

## How to Run

### Main Application

#### Windows:
Double-click `run.bat`

#### Linux/Mac:
```bash
chmod +x run.sh
./run.sh
```

#### Manual (if needed):
```bash
java --module-path "dependency" --add-modules javafx.controls,javafx.fxml -jar Project-1.0-SNAPSHOT-with-dependencies.jar
```

### Search Engine (Standalone)

#### Windows:
Double-click `run-search-engine.bat`

#### Linux/Mac:
```bash
chmod +x run-search-engine.sh
./run-search-engine.sh
```

#### Manual:
```bash
java -jar search-engine-standalone.jar
```

## Requirements
- Java 17 or higher
- No additional installations needed - JavaFX and all dependencies are included

## Contents
- `Project-1.0-SNAPSHOT-with-dependencies.jar` - Main application
- `search-engine-standalone.jar` - Standalone search engine
- `dependency/` - JavaFX runtime libraries
- `data/` - Application data files
- `run.bat` / `run.sh` - Main application launch scripts
- `run-search-engine.bat` / `run-search-engine.sh` - Search engine launch scripts