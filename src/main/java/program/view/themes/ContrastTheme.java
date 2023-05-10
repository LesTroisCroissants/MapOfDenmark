package program.view.themes;

import javafx.scene.paint.Color;
import program.view.Theme;

public class ContrastTheme extends Theme {
    public ContrastTheme(){
        super(Color.web("#00FD3E"),   //primary
                Color.web("#d8FF00"), //secondary
                Color.web("#91A659"), //tertiary
                Color.web("#000000"), //land
                Color.web("#8B00FF"), //buildings
                Color.web("#FFFFFF"), //other
                Color.web("#2200ff")); //highlighted
    }
}
