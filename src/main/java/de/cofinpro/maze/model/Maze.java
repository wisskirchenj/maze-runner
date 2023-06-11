package de.cofinpro.maze.model;

import java.util.List;

import static de.cofinpro.maze.model.Maze.Element.PATH;
import static de.cofinpro.maze.model.Maze.Element.WALL;

/**
 * model class representing the 2-dim maze.
 */
public class Maze {

    private static final int DIM = 10;
    private static final List<String> STAGE1_PATTERN = List.of(
            "1111111111",
            "0010101001",
            "1010001011",
            "1000111000",
            "1010000011",
            "1010111011",
            "1010100011",
            "1010111011",
            "1010001001",
            "1111111111"
    );

    private final Element[][] elements;

    public Maze() {
        elements = new Element[DIM][DIM];
        initStage1Maze();
    }

    public Element atPosition(int row, int column) {
        return elements[row][column];
    }

    public int getDimension() {
        return DIM;
    }

    private void initStage1Maze() {
        for (int row = 0; row < DIM; row++) {
            var line = STAGE1_PATTERN.get(row);
            for (int col = 0; col < DIM; col++) {
                elements[row][col] = line.charAt(col) == '0' ? PATH : WALL;
            }
        }
    }

    public enum Element {

        WALL,
        PATH
    }
}
