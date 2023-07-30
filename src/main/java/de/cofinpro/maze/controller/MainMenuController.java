package de.cofinpro.maze.controller;

import de.cofinpro.maze.io.ConsolePrinter;
import de.cofinpro.maze.io.StateSerializer;
import de.cofinpro.maze.model.Maze;
import de.cofinpro.maze.view.MazeView;

import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * Controller Class for the traffic lights manager app providing the main menu functionality.
 */
public class MainMenuController implements Runnable {

    private static final String MENU_START_TEXT = """
            === Menu ===
            1. Generate a new maze
            2. Load a maze
            0. Exit""";
    private static final String MENU_FULL_TEXT = """
            === Menu ===
            1. Generate a new maze
            2. Load a maze
            3. Save the maze
            4. Display the maze
            5. Find the escape
            0. Exit""";
    private static final Map<ApplicationState, String> MENU = Map.of(
            ApplicationState.INITIAL, MENU_START_TEXT,
            ApplicationState.WITH_MAZE, MENU_FULL_TEXT);

    private final ConsolePrinter printer;
    private final Scanner scanner;
    private final StateSerializer serializer;

    private ApplicationState applicationState = ApplicationState.INITIAL;
    private Maze maze;

    public MainMenuController(ConsolePrinter printer, Scanner scanner) {
        this.printer = printer;
        this.scanner = scanner;
        this.serializer = new StateSerializer(printer);
    }

    /**
     * entry point method doing the menu loop and triggering the actions chosen.
     */
    @Override
    public void run() {
        mainMenuLoop();
        printer.printInfo("Bye!");
    }

    private void mainMenuLoop() {
        var choice = getMenuChoice();
        while (choice != Choice.QUIT) {
            getMenuAction(choice).run();
            choice = getMenuChoice();
        }
    }

    private Runnable getMenuAction(Choice choice) {
        return Map.<Choice, Runnable>of(
                Choice.GENERATE, this::generateMaze,
                Choice.LOAD, this::loadMaze,
                Choice.SAVE, this::saveMaze,
                Choice.DISPLAY, this::displayMaze,
                Choice.SOLVE, this::solveMaze
        ).get(choice);
    }

    private void generateMaze() {
        printer.printInfo("Please, enter the size of a maze");
        var dimension = Integer.parseInt(scanner.nextLine());
        maze = new Maze(dimension).generate();
        displayMaze();
    }

    private void loadMaze() {
        printer.printInfo("path to maze file:");
        var filepath = scanner.nextLine();
        maze = serializer.deserialize(filepath);
        if (Objects.nonNull(maze)) {
            applicationState = ApplicationState.WITH_MAZE;
        }
    }

    private void saveMaze() {
        printer.printInfo("path to maze file:");
        var filepath = scanner.nextLine();
        serializer.serialize(filepath, maze);
    }

    private void displayMaze() {
        var view = new MazeView(maze);
        printer.printInfo(view.display());
        applicationState = ApplicationState.WITH_MAZE;
    }

    private void solveMaze() {
        if (!maze.isSolved()) {
            maze.solve();
        }
        displayMaze();
    }

    private Choice getMenuChoice() {
        printer.printInfo(MENU.get(applicationState));
        var menuOptionsRegex = applicationState == ApplicationState.INITIAL ? "[0-2]" : "[0-5]";
        var menuIndex = scanIntegerValidated(menuOptionsRegex, () ->
                printer.printInfo("Incorrect option. Please try again"));
        return Choice.values()[menuIndex];
    }

    private int scanIntegerValidated(String regex, Runnable invalidAction) {
        var input = scanner.nextLine();
        while (!input.matches(regex)) {
            invalidAction.run();
            input = scanner.nextLine();
        }
        return Integer.parseInt(input);
    }

    private enum Choice {
        QUIT, GENERATE, LOAD, SAVE, DISPLAY, SOLVE
    }

    private enum ApplicationState {
        INITIAL, WITH_MAZE
    }
}
