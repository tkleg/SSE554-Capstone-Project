# My JavaFX Application

## How to Run

### Windows:
Double-click `run.bat`

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
- No additional installations needed - JavaFX and all dependencies are included

## Contents
- `Project-1.0-SNAPSHOT-with-dependencies.jar` - Main application
- `dependency/` - JavaFX runtime libraries
- `data/` - Application data files
- `run.bat` / `run.sh` - Launch scripts