package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

/**
 * Simple implementation of the Board interface.
 * Manages the game state including the board matrix, current brick,
 * and game mechanics like movement, rotation, and collision detection.
 */
public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    /**
     * Constructs a new SimpleBoard with the specified dimensions.
     * Uses a RandomBrickGenerator by default.
     * 
     * @param width  the width of the board
     * @param height the height of the board
     */
    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }
    
    /**
     * Constructs a new SimpleBoard with the specified dimensions and brick generator.
     * 
     * @param width          the width of the board
     * @param height         the height of the board
     * @param brickGenerator the brick generator to use
     */
    public SimpleBoard(int width, int height, BrickGenerator brickGenerator) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        this.brickGenerator = brickGenerator;
        brickRotator = new BrickRotator();
        score = new Score();
    }

    /**
     * Moves the current brick down one position.
     * 
     * @return true if the brick was able to move, false if it couldn't move (collision or boundary)
     */
    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Moves the current brick left one position.
     * 
     * @return true if the brick was able to move, false if it couldn't move (collision or boundary)
     */
    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Moves the current brick right one position.
     * 
     * @return true if the brick was able to move, false if it couldn't move (collision or boundary)
     */
    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Rotates the current brick counterclockwise.
     * 
     * @return true if the brick was able to rotate, false if it couldn't rotate (collision or boundary)
     */
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    /**
     * Creates a new brick at the top of the board.
     * 
     * @return true if there is a conflict (game over), false if the brick was created successfully
     */
    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 1); // Changed from (4, 10) to (4, 0) to start from top
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Drops the current brick to the bottom of the board in one move.
     * 
     * @return true when the operation is complete
     */
    @Override
    public boolean dropBrickToBottom() {
        boolean canMove = true;
        while (canMove) {
            canMove = moveBrickDown();
        }
        return true;
    }

    /**
     * Gets the current state of the game board matrix.
     * 
     * @return a 2D array representing the current state of the board
     */
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Gets the view data for the current brick.
     * 
     * @return ViewData object containing information about the current brick's position and shape
     */
    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
    }

    /**
     * Merges the current brick into the background matrix.
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Checks for and removes completed rows from the board.
     * 
     * @return ClearRow object containing information about the cleared rows and score bonus
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    /**
     * Gets the current score object.
     * 
     * @return the Score object for this board
     */
    @Override
    public Score getScore() {
        return score;
    }

    /**
     * Resets the board for a new game using the default brick generator.
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
    
    /**
     * Resets the board for a new game using a specific brick generator.
     * 
     * @param brickGenerator the brick generator to use for this game
     */
    public void newGame(BrickGenerator brickGenerator) {
        this.brickGenerator = brickGenerator;
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}