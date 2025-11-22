package com.comp2042;

import com.comp2042.logic.bricks.RelaxBrickGenerator;

/**
 * GameController acts as the intermediary between the game logic (Board)
 * and the user interface (GuiController).
 * It handles user input events and updates the game state accordingly.
 */
public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);
    private boolean gameOverReported = false;

    private final GuiController viewGuiController;

    /**
     * Constructs a new GameController with the specified GUI controller.
     * 
     * @param c the GuiController to interact with
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    /**
     * Handles the down movement event.
     * 
     * @param event the move event
     * @return DownData containing information about the move result
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            // Check if creating a new brick results in a conflict (game over)
            boolean isGameOver = board.createNewBrick();
            if (isGameOver && !gameOverReported) {
                gameOverReported = true;
                viewGuiController.showGameOverWithScore(board.getScore().scoreProperty().get());
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        }
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Handles the left movement event.
     * 
     * @param event the move event
     * @return ViewData containing information about the brick's new position
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Handles the right movement event.
     * 
     * @param event the move event
     * @return ViewData containing information about the brick's new position
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /**
     * Handles the rotation event.
     * 
     * @param event the move event
     * @return ViewData containing information about the brick's new position
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Handles creating a new game.
     * Resets the board and game state for a new game.
     */
    @Override
    public void createNewGame() {
        if (viewGuiController.getCurrentGameMode() == GuiController.GameMode.RELAX) {
            board.newGame(new RelaxBrickGenerator());
        } else {
            board.newGame();
        }
        gameOverReported = false; // Reset the flag for a new game
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
    
    /**
     * Handles the drop to bottom event.
     * Instantly drops the current brick to the bottom of the board.
     * 
     * @param event the move event
     * @return DownData containing information about the move result
     */
    @Override
    public DownData onDropToBottomEvent(MoveEvent event) {
        board.dropBrickToBottom();
        // After dropping to bottom, we need to merge and create new brick
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }
        // Check if creating a new brick results in a conflict (game over)
        boolean isGameOver = board.createNewBrick();
        if (isGameOver && !gameOverReported) {
            gameOverReported = true;
            viewGuiController.showGameOverWithScore(board.getScore().scoreProperty().get());
        }
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return new DownData(clearRow, board.getViewData());
    }
}