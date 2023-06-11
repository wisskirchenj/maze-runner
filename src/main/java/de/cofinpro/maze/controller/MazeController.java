package de.cofinpro.maze.controller;

import de.cofinpro.maze.model.Maze;
import de.cofinpro.maze.view.MazeView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MazeController implements Runnable {

    private final Maze maze = new Maze();

    @Override
    public void run() {
        var view = new MazeView(maze);
        log.info(view.display());
    }
}
