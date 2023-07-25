package de.cofinpro.maze.model;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static de.cofinpro.maze.model.Maze.Element.PATH;
import static de.cofinpro.maze.model.Maze.Element.WALL;

/**
 * model class representing the 2-dim maze.
 */
@Getter
public class Maze implements Serializable {

    @Serial
    private static final long serialVersionUID = 100L;

    private static final Random RANDOM = new Random();
    private final int rows;
    private final int cols;

    private final Element[][] elements;

    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        elements = new Element[rows][cols];
        IntStream.range(0, rows).forEach(r -> Arrays.fill(elements[r], WALL));
    }

    public Maze(int dimension) {
        this(dimension, dimension);
    }

    public Element atPosition(Position pos) {
        return elements[pos.row()][pos.col()];
    }

    public Maze generate() {
        var seed = pickRandomInnerCell();
        setToPath(seed, false);
        applyPrimAlgorithm(seed);
        encloseAgain(); // needed for even rows or cols
        openRandomExits();
        return this;
    }

    private void encloseAgain() {
        Arrays.fill(elements[rows - 1], WALL);
        IntStream.range(0,rows).forEach(r -> elements[r][cols -1] = WALL);
    }

    private void setToPath(Position pos, boolean connect) {
        elements[pos.row()][pos.col()] = PATH;
        if (connect) {
            var candidates = validNeighboursOf(pos)
                    .stream().filter(p -> atPosition(p) == PATH).toList();
            var with = candidates.get(RANDOM.nextInt(candidates.size()));
            setToPath(new Position((pos.row() + with.row()) / 2, (pos.col() + with.col()) / 2), false);
        }
    }

    private void applyPrimAlgorithm(Position seed) {
        List<Position> openGridPositions = new ArrayList<>(validNeighboursOf(seed));
        while (!openGridPositions.isEmpty()) {
            var nextPath = openGridPositions.remove(RANDOM.nextInt(openGridPositions.size()));
            setToPath(nextPath, true);
            openGridPositions.addAll(
                    validNeighboursOf(nextPath).stream().filter(pos -> atPosition(pos) != PATH).toList()
            );
        }
    }

    private List<Position> validNeighboursOf(Position grid) {
        List<Position> neighbours = new ArrayList<>();
        if (grid.row() >= 2) {
            neighbours.add(new Position(grid.row() - 2, grid.col()));
        }
        if (grid.row() < rows - 2) {
            neighbours.add(new Position(grid.row() + 2, grid.col()));
        }
        if (grid.col() >= 2) {
            neighbours.add(new Position(grid.row(), grid.col() - 2));
        }
        if (grid.col() < cols - 2) {
            neighbours.add(new Position(grid.row(), grid.col() + 2));
        }
        return neighbours;
    }

    private void openRandomExits() {
        int row;
        do {
            row = RANDOM.nextInt(1, rows - 1);
        } while (elements[row][1] != PATH);
        setToPath(new Position(row, 0), false);

        do {
            row = RANDOM.nextInt(1, rows - 1);
        } while (elements[row][cols - 2] != PATH);
        setToPath(new Position(row, cols - 1), false);
    }

    private Position pickRandomInnerCell() {
        var row = RANDOM.nextInt(rows / 2) * 2 + 1;
        var col = RANDOM.nextInt(cols / 2) * 2 + 1;
        return new Position(row, col);
    }

    public enum Element {
        WALL,
        PATH
    }
}