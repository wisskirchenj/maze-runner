package de.cofinpro.maze.io;

import de.cofinpro.maze.model.Maze;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StateSerializer {

    private static final String SERIALIZE_PATH = "src/main/resources/data/";

    private final ConsolePrinter printer;

    public StateSerializer(ConsolePrinter printer) {
        this.printer = printer;
    }

    /**
     * serialization of the budget state via ObjectOutputStream.writeObject
     * @param maze Maze to serialize
     */
    public void serialize(String filepath, Maze maze) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(SERIALIZE_PATH + filepath)))) {
            oos.writeObject(maze);
        } catch (IOException exception) {
            printer.printError("cannot serialize to file\n", exception);
        }
    }

    public Maze deserialize(String filepath) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(SERIALIZE_PATH + filepath)))) {
            return (Maze) ois.readObject();
        } catch (FileNotFoundException exception) {
            printer.printInfo("The file %s does not exist".formatted(filepath));
        } catch (IOException | ClassNotFoundException exception) {
            printer.printError("Cannot load the maze. It has an invalid format\n", exception);
        }
        return null;
    }
}
