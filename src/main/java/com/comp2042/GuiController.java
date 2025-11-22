package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * GuiController manages the graphical user interface for the Tetris game.
 * It handles user input, updates the display, and manages game states.
 */
public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int BASE_SPEED = 400; // milliseconds
    private static final int SCORE_PER_LEVEL = 200; // points needed to advance to next level
    private static final double SPEED_INCREASE_PER_LEVEL = 0.10; // 10% speed increase per level

    /**
     * Game modes supported by the application.
     */
    public enum GameMode {
        /** Classic mode with score-based speed increases */
        CLASSIC, 
        /** Challenge mode with level-based speed increases */
        CHALLENGE,
        /** Relax mode with limited brick types and no leaderboard recording */
        RELAX
    }

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;
    
    @FXML
    private Label scoreLabel;
    
    @FXML
    private Label levelLabel;
    
    @FXML
    private Label speedLabel;
    
    @FXML
    private Label pauseLabel;
    
    @FXML
    private MenuItem classicModeMenuItem;
    
    @FXML
    private MenuItem challengeModeMenuItem;
    
    @FXML
    private MenuItem relaxModeMenuItem;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    
    private IntegerProperty scoreProperty;
    
    private GameMode currentGameMode = GameMode.CLASSIC;
    private int currentLevel = 1;

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up event handlers and initial game state.
     * 
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    // Handle space key to drop brick to bottom
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        dropToBottom(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }
                // Handle Enter key to pause/resume game
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    togglePause();
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        gameOverPanel.setVisible(false);
        
        // Set up restart button click handler
        gameOverPanel.setOnRestartClicked(e -> newGame(null));
        
        // Set up menu items
        classicModeMenuItem.setOnAction(e -> switchToClassicMode());
        challengeModeMenuItem.setOnAction(e -> switchToChallengeMode());
        relaxModeMenuItem.setOnAction(e -> switchToRelaxMode());

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    /**
     * Initializes the game view with the board matrix and initial brick data.
     * 
     * @param boardMatrix the game board matrix
     * @param brick the initial brick view data
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);


        timeLine = new Timeline(new KeyFrame(
                Duration.millis(BASE_SPEED),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /**
     * Gets the color for a brick cell based on its value.
     * 
     * @param i the brick cell value
     * @return the Paint color for the cell
     */
    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }

    /**
     * Refreshes the brick display with new position and shape data.
     * 
     * @param brick the ViewData containing the brick information
     */
    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
    }

    /**
     * Refreshes the game background display with updated board data.
     * 
     * @param board the updated board matrix
     */
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * Sets the visual properties of a rectangle based on its color value.
     * 
     * @param color the color value
     * @param rectangle the rectangle to update
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    /**
     * Moves the brick down one position.
     * 
     * @param event the move event
     */
    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }
    
    /**
     * Drops the brick to the bottom of the board.
     * 
     * @param event the move event
     */
    private void dropToBottom(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDropToBottomEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }
    
    /**
     * Toggles the pause state of the game.
     */
    private void togglePause() {
        if (isGameOver.getValue() == Boolean.FALSE) {
            if (isPause.getValue() == Boolean.FALSE) {
                timeLine.pause();
                isPause.setValue(Boolean.TRUE);
                // Show pause message
                pauseLabel.setVisible(true);
            } else {
                timeLine.play();
                isPause.setValue(Boolean.FALSE);
                // Hide pause message
                pauseLabel.setVisible(false);
            }
        }
    }

    /**
     * Sets the input event listener for handling user input.
     * 
     * @param eventListener the input event listener
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Binds the score property to the GUI display.
     * 
     * @param integerProperty the score property to bind
     */
    public void bindScore(IntegerProperty integerProperty) {
        this.scoreProperty = integerProperty;
        // Add listener to update speed when score changes
        this.scoreProperty.addListener((observable, oldValue, newValue) -> {
            updateGameSpeed(newValue.intValue());
            // Update score display
            scoreLabel.setText("Score: " + newValue.toString());
            
            // Handle level progression in challenge mode
            if (currentGameMode == GameMode.CHALLENGE) {
                int newLevel = (newValue.intValue() / SCORE_PER_LEVEL) + 1;
                if (newLevel != currentLevel) {
                    currentLevel = newLevel;
                    levelLabel.setText("Level: " + currentLevel);
                    updateSpeedDisplay();
                }
            }
        });
    }
    
    /**
     * Updates the game speed based on the current score or level.
     * 
     * @param score the current score
     */
    private void updateGameSpeed(int score) {
        double speedMultiplier;
        
        if (currentGameMode == GameMode.CLASSIC) {
            // Calculate speed multiplier based on score for classic mode
            if (score >= 0 && score <= 499) {
                speedMultiplier = 1.0; // Normal speed
            } else if (score >= 500 && score <= 999) {
                speedMultiplier = 1.5; // Increase 50% speed
            } else if (score >= 1000 && score <= 1999) {
                speedMultiplier = 2.0; // Increase 100% speed
            } else { // score >= 2000
                speedMultiplier = 2.5; // Increase 150% speed
            }
        } else { // CHALLENGE or RELAX mode
            // Calculate speed multiplier based on level (10% increase per level)
            speedMultiplier = 1.0 + (currentLevel - 1) * SPEED_INCREASE_PER_LEVEL;
        }
        
        // Update timeline with new speed
        if (timeLine != null) {
            timeLine.stop();
            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(BASE_SPEED / speedMultiplier),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Timeline.INDEFINITE);
            
            // Resume if not paused
            if (!isPause.getValue() && !isGameOver.getValue()) {
                timeLine.play();
            }
        }
    }
    
    /**
     * Updates the speed display label.
     */
    private void updateSpeedDisplay() {
        if (currentGameMode == GameMode.CHALLENGE) {
            int speedPercentage = (int) (100 + (currentLevel - 1) * 10);
            speedLabel.setText("Speed: " + speedPercentage + "%");
        } else {
            // For classic mode, we'll show the current speed level
            if (scoreProperty != null) {
                int score = scoreProperty.get();
                if (score >= 0 && score <= 499) {
                    speedLabel.setText("Speed: 100%");
                } else if (score >= 500 && score <= 999) {
                    speedLabel.setText("Speed: 150%");
                } else if (score >= 1000 && score <= 1999) {
                    speedLabel.setText("Speed: 200%");
                } else {
                    speedLabel.setText("Speed: 250%");
                }
            }
        }
    }
    
    /**
     * Switches the game to classic mode.
     */
    private void switchToClassicMode() {
        currentGameMode = GameMode.CLASSIC;
        newGame(null);
    }
    
    /**
     * Switches the game to challenge mode.
     */
    private void switchToChallengeMode() {
        currentGameMode = GameMode.CHALLENGE;
        newGame(null);
    }
    
    /**
     * Switches the game to relax mode.
     */
    private void switchToRelaxMode() {
        currentGameMode = GameMode.RELAX;
        newGame(null);
    }
    
    /**
     * Gets the current game mode.
     * 
     * @return the current game mode
     */
    public GameMode getCurrentGameMode() {
        return currentGameMode;
    }
    
    /**
     * Shows the game over screen with the final score.
     * 
     * @param score the final score
     */
    public void showGameOverWithScore(int score) {
        // Show game over panel
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    /**
     * Starts a new game.
     * 
     * @param actionEvent the action event that triggered the new game
     */
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        // Hide pause message when starting new game
        pauseLabel.setVisible(false);
        // Reset score display
        if (scoreLabel != null && scoreProperty != null) {
            scoreLabel.setText("Score: 0");
        }
        
        // Reset level and speed display based on game mode
        if (currentGameMode == GameMode.CHALLENGE) {
            currentLevel = 1;
            levelLabel.setText("Level: 1");
            speedLabel.setText("Speed: 100%");
        } else {
            levelLabel.setText("Mode: Classic");
            speedLabel.setText("Speed: 100%");
        }
    }

    /**
     * Pauses the game.
     * 
     * @param actionEvent the action event that triggered the pause
     */
    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}