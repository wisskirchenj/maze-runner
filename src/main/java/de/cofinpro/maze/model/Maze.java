package de.cofinpro.maze.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static de.cofinpro.maze.model.Maze.Element.PATH;
import static de.cofinpro.maze.model.Maze.Element.TRAVERSE;
import static de.cofinpro.maze.model.Maze.Element.VISITED;
import static de.cofinpro.maze.model.Maze.Element.WALL;
import static java.util.function.Predicate.not;

/**
 * model class representing the 2-dim maze.
 */
@Getter
@Slf4j
public class Maze implements Serializable {

    @Serial
    private static final long serialVersionUID = 100L;

    private static final Random RANDOM = new Random();
    private final int rows;
    private final int cols;
    private boolean solved = false;

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
        return atPosition(pos.row(), pos.col());
    }

    private Element atPosition(int row, int col) {
        return elements[row][col];
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
            var candidates = validNeighboursOf(pos, 2)
                    .stream().filter(p -> atPosition(p) == PATH).toList();
            var with = candidates.get(RANDOM.nextInt(candidates.size()));
            setToPath(new Position((pos.row() + with.row()) / 2, (pos.col() + with.col()) / 2), false);
        }
    }

    private void applyPrimAlgorithm(Position seed) {
        List<Position> openGridPositions = new ArrayList<>(validNeighboursOf(seed, 2));
        Set<Position> gridAdded = new HashSet<>(openGridPositions);
        while (!openGridPositions.isEmpty()) {
            var nextPath = openGridPositions.remove(RANDOM.nextInt(openGridPositions.size()));
            setToPath(nextPath, true);
            var gridPositions = validNeighboursOf(nextPath, 2).stream()
                    .filter(pos -> atPosition(pos) != PATH)
                    .filter(not(gridAdded::contains))
                    .toList();
            openGridPositions.addAll(gridPositions);
            gridAdded.addAll(gridPositions);
        }
    }

    private List<Position> validNeighboursOf(Position grid, int distance) {
        List<Position> neighbours = new ArrayList<>();
        if (grid.row() >= distance) {
            neighbours.add(grid.up(distance));
        }
        if (grid.row() < rows - distance) {
            neighbours.add(grid.down(distance));
        }
        if (grid.col() >= distance) {
            neighbours.add(grid.left(distance));
        }
        if (grid.col() < cols - distance) {
            neighbours.add(grid.right(distance));
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

    public void solve() {
        var current = findEntranceInCol(0); //left entrance to maze
        var rightEntrance = findEntranceInCol(cols - 1);
        elements[current.row()][current.col()] = TRAVERSE;
        while (!current.equals(rightEntrance)) {
            final var position =  current;
            current = proceedOnPath(position).or(() -> backup(position)).orElseThrow();
        }
        elements[rightEntrance.row()][rightEntrance.col()] = TRAVERSE;
        solved = true;
    }

    private Optional<Position> backup(Position pos) {
        elements[pos.row()][pos.col()] = VISITED;
        return validNeighboursOf(pos, 1).stream()
                .filter(p -> atPosition(p) == TRAVERSE).findFirst();
    }

    private Optional<Position> proceedOnPath(Position pos) {
        final var next = validNeighboursOf(pos, 1).stream().filter(p -> atPosition(p) == PATH).findFirst();
        next.ifPresent(p -> elements[p.row()][p.col()] = TRAVERSE);
        return next;
    }

    private Position findEntranceInCol(int col) {
        var row = IntStream.range(0, rows).filter(r -> atPosition(r, col) == PATH).findFirst().orElseThrow();
        return new Position(row, col);
    }

    public enum Element {
        WALL,
        PATH,
        TRAVERSE,
        VISITED
    }
}