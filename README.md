# Tetris Game Course Project

## GitHub Repository
https://github.com/LaiYuyang118/CW2025

## Compilation Instructions
1. Ensure Java JDK 8 or higher is installed
2. Build the project using Maven:
   ```
   mvn clean package
   ```
3. Run the game:
   ```
   java -jar target/CW2025-master.jar
   ```
   
Or open the project directly in an IDE (such as IntelliJ IDEA) and run Main.java

## Implemented and Working Features

### 1. Core Game Features
- Standard Tetris gameplay
- Block falling, movement, and rotation
- Line clearing and scoring system
- Game over detection

### 2. Game Modes
- **Classic Mode**: Traditional Tetris gameplay with speed increases based on score
- **Challenge Mode**: Progress to next level every 200 points, each level increases speed by 10%
- **Relax Mode**: Only generates I-shaped (long) and O-shaped (square) blocks, suitable for relaxed gameplay, scores not recorded in leaderboard

### 3. User Interface Improvements
- Top menu bar for switching game modes
- Right-side information panel displaying current score, level, and speed
- Pause/resume functionality (Enter key)
- Instant drop functionality (Space bar)

### 4. Visual Effects
- Blocks start falling from the top
- Window size adjustment for wider game area
- "PAUSED" display when game is paused

## Implemented but Not Working Features
None

## Unimplemented Features
None

## New Java Classes

### 1. RelaxBrickGenerator.java
**Location**: src/main/java/com/comp2042/logic/bricks/RelaxBrickGenerator.java
**Purpose**: Class specifically for Relax mode to generate blocks, only generates I-shaped and O-shaped blocks

## Modified Java Classes

### 1. SimpleBoard.java
**Modifications**:
- Modified initial block position to start falling from top (Y coordinate changed from 10 to 0)
- Added constructor supporting custom block generators
- Added newGame method supporting custom block generators

**Reason**:
- Improve game experience, having blocks start falling from top of game area is more intuitive for players
- Support different block generators for different game modes

### 2. Main.java
**Modifications**:
- Increased window width from 300 pixels to 800 pixels

**Reason**:
- Provide larger game area, improve visual effects and game experience

### 3. GuiController.java
**Modifications**:
- Implemented block position calculation correction to accommodate menu bar addition
- Added score display, level display, and speed display
- Implemented pause/resume functionality
- Implemented instant drop functionality
- Added game mode switching logic (Classic, Challenge, Relax)
- Fixed block position calculation issues
- Added Relax mode where scores are not saved to leaderboard

**Reason**:
- Enhance game interactivity and user interface
- Provide better game control options
- Implement multiple game modes to increase playability
- Add dedicated Relax mode to meet different player needs

### 4. GameController.java
**Modifications**:
- Fixed issue of scores being recorded multiple times at game end
- Modified game over detection logic
- Added support for Relax mode

**Reason**:
- Ensure each game session records score only once
- Improve leaderboard data accuracy
- Support new Relax game mode

### 5. Board.java
**Modifications**:
- Added newGame method supporting custom block generators

**Reason**:
- Support different block generators for different game modes

### 6. gameLayout.fxml
**Modifications**:
- Added Relax mode menu item
- Added top menu bar
- Added right-side information display area
- Adjusted game area layout to accommodate new elements

**Reason**:
- Provide access to Relax mode
- Provide better user interface organization
- Display more game information

### 7. window_style.css
**Modifications**:
- Added menu bar style definitions

**Reason**:
- Ensure new UI elements have consistent visual style

## Unexpected Issues

### 1. Block Position Calculation Issue
**Issue Description**: After adding menu bar, visual position of blocks didn't match logical position, causing blocks to fall to wrong positions
**Solution**: Adjusted block position calculation in GuiController, correctly handled menu bar height's effect on position

### 2. Score Recording Duplication Issue
**Issue Description**: Scores were recorded multiple times to leaderboard files at game end
**Solution**: Added gameOverReported flag in GameController to ensure each game session records score only once
