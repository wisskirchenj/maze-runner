package de.cofinpro.maze;

import de.cofinpro.maze.controller.MainMenuController;
import de.cofinpro.maze.io.ConsolePrinter;

import java.util.Scanner;

public class MazeRunnerApp {
    public static void main(String[] args) {
        new MainMenuController(new ConsolePrinter(), new Scanner(System.in)).run();
    }
}