package program.controller;

import static program.model.Model.MOT.*;

/**
 * Interprets a command given as a string and calls relevant methods in the CommandExecutor
 */
public class CommandParser {
    private static CommandExecutor commandExecutor;

    public static void setCommandExecutor(CommandExecutor controller){
        commandExecutor = controller;
    }

    public static void parseCommand(String command) throws IllegalCommandException{
        char[] input = command.toCharArray();

        StringBuilder toExecute = new StringBuilder();
        StringBuilder address = new StringBuilder();
        StringBuilder id = new StringBuilder();
        boolean isCommand = false;
        boolean isId = false;
        for (char c : input){
            if (c == '!'){
                isCommand = true;
                isId = false;
            }
            else if (isCommand && c == ' ') {
                isCommand = false;
                if (toExecute.charAt(1) == 'd' || toExecute.charAt(1) == 'a')
                    isId = true;
            }

            if (isCommand) toExecute.append(c);
            else if (isId) id.append(c);
            else address.append(c);
        }

        giveCommand(toExecute.toString().strip(), address.toString().strip(), id.toString().strip());
    }

    private static void giveCommand(String toExecute, String address, String id) throws IllegalCommandException{
        switch (toExecute) {
                //Points
            case "":
                commandExecutor.addressSearch(address.toLowerCase());
                break;
            case "!as":
                if (address.equals("") || id.equals("")) throw new IllegalCommandException("Command !as takes both an address and an ID");
                else commandExecutor.setPOI(id, address);
                break;

                // Route planning
            case "!t":
            case "!to":
                if (address.equals("")) throw new IllegalCommandException("Command !to takes either an address or a POI ID");
                else commandExecutor.planRoute(address, true);
                break;
            case "!instructions":
            case "!i":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !i does not take any arguments");
                else commandExecutor.displayInstructions();
                break;
            case "!f":
            case "!from":
                if (address.equals("")) throw new IllegalCommandException("Command !from takes either an address or a POI ID");
                else commandExecutor.planRoute(address, false);
                break;

                //Program navigation:
            case "!q":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !q does not take any arguments");
                else commandExecutor.quitSelection();
                break;
            case "!help":
            case "!h":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !help does not take any arguments");
                else commandExecutor.getHelp();
                break;
            case "!about":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !about does not take any arguments");
                else commandExecutor.displayProgramInformation();
                break;
            case "!poi":
            case "!p":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !poi does not take any arguments");
                else commandExecutor.displayPOIs();
                break;

                //Settings:
            case "!walk":
            case "!w":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !walk does not take any arguments");
                else commandExecutor.setModeOfTransportation(WALK);
                break;
            case "!bike":
            case "!b":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !bike does not take any arguments");
                else commandExecutor.setModeOfTransportation(BIKE);
                break;
            case "!car":
            case "!c":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !car does not take any arguments");
                else commandExecutor.setModeOfTransportation(CAR);
                break;
            case "!display":
            case "!d":
                if (!address.equals("")) throw new IllegalCommandException("Command !display does not take an address");
                else commandExecutor.setDisplay(id);
                break;
            case "!load":
                commandExecutor.load();
                break;

            case "!debug":
                commandExecutor.setDebug(id);
                break;

            default:
                throw new IllegalCommandException("Command " + toExecute + " is not a legal command");
        }
    }

    /**
     * Thrown when an entered command is not a part of the system or is entered with the wrong arguments
     */
    public static class IllegalCommandException extends Exception {
        public IllegalCommandException(String message){
            super(message);
        }
    }
}
