package com.comp2042.logic.bricks;

import java.util.Random;

/**
 * A brick generator that only creates I-shaped and O-shaped bricks.
 * This generator is used for the Relax Mode, which simplifies the game
 * by limiting the types of bricks that can appear.
 */
public class RelaxBrickGenerator implements BrickGenerator {
    private final Random random = new Random();

    /**
     * Gets a random brick, either I-shaped or O-shaped.
     * 
     * @return a new IBrick or OBrick instance
     */
    @Override
    public Brick getBrick() {
        // Only generate IBrick (long piece) and OBrick (square piece)
        int brickType = random.nextInt(2);
        switch (brickType) {
            case 0:
                return new IBrick();
            case 1:
            default:
                return new OBrick();
        }
    }

    /**
     * Gets the next random brick, either I-shaped or O-shaped.
     * 
     * @return a new IBrick or OBrick instance
     */
    @Override
    public Brick getNextBrick() {
        // Only generate IBrick (long piece) and OBrick (square piece)
        int brickType = random.nextInt(2);
        switch (brickType) {
            case 0:
                return new IBrick();
            case 1:
            default:
                return new OBrick();
        }
    }
}