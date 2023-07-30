package de.cofinpro.maze.model;

public record Position(int row, int col) {

    private Position shift(int delRow, int delCol) {
        return new Position(this.row() + delRow, this.col() + delCol);
    }

    public Position up(int distance) {
        return shift(-distance, 0);
    }

    public Position down(int distance) {
        return shift(distance, 0);
    }

    public Position left(int distance) {
        return shift(0, -distance);
    }

    public Position right(int distance) {
        return shift(0, distance);
    }
}
