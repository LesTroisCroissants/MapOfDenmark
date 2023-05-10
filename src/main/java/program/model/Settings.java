package program.model;

import program.view.*;
import program.view.themes.*;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Saves the settings for the program
 */
public class Settings implements Serializable {
    private final String[] legalModesOfTransportation = new String[]{"w","b","c"};
    private Theme theme;
    private String modeOfTransportation;

    public Settings(){
        theme = new BrookTheme();
        modeOfTransportation = "b";
    }

    /**
     * Sets the display style of the map
     * @param newTheme
     */
    public void setTheme(String newTheme){
        if (newTheme.equalsIgnoreCase("sakura"))
            theme = new SakuraTheme();
        else if (newTheme.equalsIgnoreCase("mint"))
            theme = new MintTheme();
        else if (newTheme.equalsIgnoreCase("verdant"))
            theme = new VerdantTheme();
        else if (newTheme.equalsIgnoreCase("brook"))
            theme = new BrookTheme();
        else if (newTheme.equalsIgnoreCase("contrast"))
            theme = new ContrastTheme();
        else
            throw new IllegalArgumentException("Display style does not exist");
    }

    /**
     * Sets the mode of transportation for route-planning
     * @param newModeOfTransportation
     */
    public void setModeOfTransportation(String newModeOfTransportation){
        if (Arrays.asList(legalModesOfTransportation).contains(newModeOfTransportation))
            modeOfTransportation = newModeOfTransportation;
        else
            throw new IllegalArgumentException("Mode of transportation is not defined");
    }

    /**
     * Returns the current display style
     * @return
     */
    public Theme getTheme(){
        return theme;
    }

    /**
     * Returns the current mode of transportation
     * @return
     */
    public String getModeOfTransportation(){
        return modeOfTransportation;
    }
}
