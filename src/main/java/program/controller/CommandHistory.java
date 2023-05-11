package program.controller;

import java.util.*;


public class CommandHistory {
    private Deque<String> commandHistoryUp; //Used as a stack
    private Deque<String> commandHistoryDown; //Used as a stack

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
     * Returns the next command in the history (that is the command that was entered before the one currently displayed)
     * @return command as String
     */
    public String getNext(String current) {
        try {
            String lastUp = commandHistoryUp.removeFirst();

            while (lastUp.equals(current)) {
                commandHistoryDown.addFirst(lastUp);
                lastUp = commandHistoryUp.removeFirst();
            }

            commandHistoryDown.addFirst(lastUp);
            return lastUp;
        } catch (NoSuchElementException e) {
            throw new RuntimeException("No previous command");
        }
    }

    /**
     * Returns the previous command in the history (that is the command that was entered prior to the one currently displayed)
     * @return command as String
     */
    public String getPrevious(String current) {
        try {
            String lastDown = commandHistoryDown.removeFirst();

            while (lastDown.equals(current)) {
                commandHistoryUp.addFirst(lastDown);
                lastDown = commandHistoryDown.removeFirst();
            }

            commandHistoryDown.addFirst(lastDown);
            return lastDown;
        } catch (NoSuchElementException e) {
            throw new RuntimeException("No previous command");
        }
    }
}
