package com.comp2042;

import com.comp2042.logic.bricks.BrickGenerator;

/**
 * Interface for game board implementations.
 * Defines the contract for board operations such as moving bricks,
 * rotating bricks, and managing game state.
 */
public interface Board {

    /**
     * Moves the current brick down one position.
     * 
     * @return true if the brick was able to move, false if it couldn't move (collision or boundary)
     */
    boolean moveBrickDown();

    /**
     * Moves the current brick left one position.
     * 
     * @return true if the brick was able to move, false if it couldn't move (collision or boundary)
     */
    boolean moveBrickLeft();

    /**
     * Moves the current brick right one position.
     * 
     * @return true if the brick was able to move, false if it couldn't move (collision or boundary)
     */
    boolean moveBrickRight();

    /**
     * Rotates the current brick counterclockwise.
     * 
     * @return true if the brick was able to rotate, false if it couldn't rotate (collision or boundary)
     */
    boolean rotateLeftBrick();

    /**
     * Creates a new brick at the top of the board.
     * 
     * @return true if there is a conflict (game over), false if the brick was created successfully
     */
    boolean createNewBrick();

    /**
     * Gets the current state of the game board matrix.
     * 
     * @return a 2D array representing the current state of the board
     */
    int[][] getBoardMatrix();

    /**
     * Gets the view data for the current brick.
     * 
     * @return ViewData object containing information about the current brick's position and shape
     */
    ViewData getViewData();

    /**
     * Merges the current brick into the background matrix.
     */
    void mergeBrickToBackground();

    /**
     * Checks for and removes completed rows from the board.
     * 
     * @return ClearRow object containing information about the cleared rows and score bonus
     */
    ClearRow clearRows();

    /**
     * Gets the current score object.
     * 
     * @return the Score object for this board
     */
    Score getScore();

    /**
     * Resets the board for a new game using the default brick generator.
     */
    void newGame();
    
    /**
     * Resets the board for a new game using a specific brick generator.
     * 
     * @param brickGenerator the brick generator to use for this game
     */
    void newGame(BrickGenerator brickGenerator);
    
    /**
     * Drops the current brick to the bottom of the board in one move.
     * 
     * @return true when the operation is complete
     */
    boolean dropBrickToBottom();
}