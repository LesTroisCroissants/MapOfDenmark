package program.controller;

import static program.model.Model.MOT.*;

public class CommandParser {
    private static CommandExecutor c;

    public static void setCommandExecutor(CommandExecutor controller){
        c = controller;
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
                c.addressSearch(address.toLowerCase());
                break;
            case "!as":
                if (address.equals("") || id.equals("")) throw new IllegalCommandException("Command !as takes both an address and an ID");
                else c.setPOI(id, address);
                break;

                // Route planning
            case "!t":
            case "!to":
                if (address.equals("")) throw new IllegalCommandException("Command !to takes either an address or a POI ID");
                else c.planRoute(address, true);
                break;
            case "!instructions":
            case "!i":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !i does not take any arguments");
                else c.displayInstructions();
            case "!f":
            case "!from":
                if (address.equals("")) throw new IllegalCommandException("Command !from takes either an address or a POI ID");
                else c.planRoute(address, false);
                break;

                //Program navigation:
            case "!q":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !q does not take any arguments");
                else c.quitSelection();
                break;
            case "!help":
            case "!h":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !help does not take any arguments");
                else c.getHelp();
                break;
            case "!about":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !about does not take any arguments");
                else c.displayProgramInformation();
                break;
            case "!poi":
            case "!p":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !poi does not take any arguments");
                else c.displayPOIs();
                break;
                //Settings:
            case "!walk":
            case "!w":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !walk does not take any arguments");
                else c.setModeOfTransportation(WALK);
                break;
            case "!bike":
            case "!b":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !bike does not take any arguments");
                else c.setModeOfTransportation(BIKE);
                break;
            case "!car":
            case "!c":
                if (!address.equals("") || !id.equals("")) throw new IllegalCommandException("Command !car does not take any arguments");
                else c.setModeOfTransportation(CAR);
                break;
            case "!display":
            case "!d":
                if (!address.equals("")) throw new IllegalCommandException("Command !display does not take an address");
                else c.setDisplay(id);
                break;

            case "!debug":
                c.setDebug(id);
                break;

                case "!load":
                    c.load();
                    break;

            default:
                throw new IllegalCommandException("Command " + toExecute + " is not a legal command");
        }
    }


    public static class IllegalCommandException extends Exception {
        public IllegalCommandException(String message){
            super(message);
        }
    }
}
