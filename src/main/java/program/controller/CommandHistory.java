package program.controller;

import java.util.*;


public class CommandHistory {
    private Deque<String> commandHistoryUp;
    private Deque<String> commandHistoryDown;

    private static CommandHistory instance;

    public static CommandHistory getInstance() {
        if (instance == null) instance = new CommandHistory();
        return instance;
    }

    private CommandHistory() {
        commandHistoryUp = new ArrayDeque<>();
        commandHistoryDown = new ArrayDeque<>();
    }

    /**
     * Reestablishes order in history and adds new command
     * @param command command to be added
     */
    public void add(String command) {
        if (command.equals("")) return;
        while (!commandHistoryDown.isEmpty()) {
            commandHistoryUp.addFirst(commandHistoryDown.pollFirst());
        }
        commandHistoryUp.addFirst(command);
    }

    /**
     *
     * @return
     */
    public String getNext() {
        try {
            String lastUp = commandHistoryUp.removeFirst();
            commandHistoryDown.addFirst(lastUp);
            return lastUp;
        } catch (NoSuchElementException e) {
            throw new RuntimeException("No previous command");
        }
    }

    public String getPrevious() {
        try {
            String lastDown = commandHistoryDown.removeFirst();
            commandHistoryUp.addFirst(lastDown);
            return lastDown;
        } catch (NoSuchElementException e) {
            throw new RuntimeException("No previous command");
        }
    }
}
