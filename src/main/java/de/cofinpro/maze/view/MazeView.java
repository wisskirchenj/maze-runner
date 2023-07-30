package de.cofinpro.maze.view;

import de.cofinpro.maze.model.Maze;
import de.cofinpro.maze.model.Position;

import java.util.Map;

/**
 * View representation of a maze in text graphics.
 */
public class MazeView {

    private static final Map<Maze.Element, String> DISPLAY_MAP = Map.of(
            Maze.Element.PATH, "  ",
            Maze.Element.WALL, "██",
            Maze.Element.TRAVERSE, "//",
            Maze.Element.VISITED, "  "
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
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                stringBuilder.append(DISPLAY_MAP.get(maze.atPosition(new Position(row, col))));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
