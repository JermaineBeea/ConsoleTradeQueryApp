# Trade Console Application

This project provides multiple GUI interfaces for the trade calculation system.

## Available Applications

### 1. **Swing GUI** (Recommended - Simplest)
- Built into Java, no extra dependencies
- Works on all Java installations
- Clean, functional interface

### 2. **JavaFX GUI** (Modern)
- Modern look and feel
- Requires JavaFX (included for Java 21+)
- More visually appealing

### 3. **Web Application**
- Browser-based interface
- Full-featured with real-time updates

## Building and Running

### Quick Start (Launcher)
```bash
# Compile and run the launcher (gives you choice of GUI)
mvn clean compile exec:java -Dexec.mainClass="co.za.Main.ConsoleApplication.ConsoleLauncher"
```

### Individual Applications

#### Swing GUI
```bash
# Compile and run Swing application
mvn clean compile exec:java -Dexec.mainClass="co.za.Main.ConsoleApplication.ConsoleGUIApplication"
```

#### JavaFX GUI
```bash
# Compile and run JavaFX application (requires JavaFX)
mvn clean compile exec:java -Dexec.mainClass="co.za.Main.ConsoleApplication.ConsoleJavaFXApplication"

# OR use JavaFX plugin
mvn clean javafx:run
```

#### Web Application
```bash
# Run web server
mvn clean compile exec:java -Dexec.mainClass="co.za.Main.WebTradeApplication.WebApp"
```

#### Original Console Text Interface
```bash
# Run text-based console
mvn clean compile exec:java -Dexec.mainClass="co.za.Main.ConsoleApplication.ConsoleImplementation"
```

## Features

All GUI applications include:

- **Input Fields**: Spread, Rate KA, Rate PN
- **Mode Toggle**: Market-based vs Execution-based calculations  
- **Editable Table**: Modify minimum/maximum values directly
- **Real-time Calculations**: See return values update automatically
- **Example Data**: Load test data with one click
- **Reset Function**: Clear all values to zero
- **Export**: Save data to SQL/CSV files

## GUI Comparison

| Feature | Swing | JavaFX | Web |
|---------|--------|--------|-----|
| **Setup** | None required | May need JavaFX | Browser needed |
| **Look** | Standard Java | Modern | Best |
| **Performance** | Fast | Fast | Network dependent |
| **Deployment** | Desktop only | Desktop only | Any device |

## Troubleshooting

### JavaFX Issues
If JavaFX doesn't work:
```bash
# Install JavaFX (if not included)
# OR just use the Swing version - it has all the same features!
```

### Database Issues
- Database files are created automatically in the project directory
- If you see database errors, delete `ConsoleDataBase.db` and restart

### Memory Issues
```bash
# If you get memory errors, increase heap size
export MAVEN_OPTS="-Xmx1024m"
mvn clean compile exec:java -Dexec.mainClass="..."
```

## File Structure
```
src/main/java/co/za/Main/ConsoleApplication/
├── ConsoleLauncher.java          # Choose GUI type
├── ConsoleGUIApplication.java    # Swing GUI
├── ConsoleJavaFXApplication.java # JavaFX GUI  
├── TradeVariable.java           # JavaFX data model
├── ConsoleDatabase.java         # Database layer
└── ConsoleImplementation.java   # Text console
```

## Recommendation

**For simplicity**: Use the **Swing GUI** - it works everywhere and has all features!

```bash
mvn clean compile exec:java -Dexec.mainClass="co.za.Main.ConsoleApplication.ConsoleGUIApplication"
```