package de.cofinpro.maze.view;

import de.cofinpro.maze.model.Maze;

import java.util.Map;

/**
 * View representation of a maze in text graphics.
 */
public class MazeView {

    private static final Map<Maze.Element, String> DISPlAY_MAP = Map.of(
            Maze.Element.PATH, "  ",
            Maze.Element.WALL, "██"
    );
    private final Maze maze;

    public MazeView(Maze maze) {
        this.maze = maze;
    }

    /**
     * @return String displaying the full maze.
     */
    public String display() {
        var stringBuilder = new StringBuilder();
        for (int row = 0; row < maze.getDimension(); row++) {
            for (int col = 0; col < maze.getDimension(); col++) {
                stringBuilder.append(DISPlAY_MAP.get(maze.atPosition(row, col)));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
