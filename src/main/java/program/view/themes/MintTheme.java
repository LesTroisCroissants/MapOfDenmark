package program.view.themes;

import javafx.scene.paint.Color;
import program.view.Theme;

public class MintTheme extends Theme {

    public MintTheme(){
        super(Color.web("#83E8BA"), //primary
                Color.web("#83E8BA"), //secondary
                Color.web("#689689"), //tertiary
                Color.web("504136"), //land
                Color.web("#A49E8D"), //buildings
                Color.web("#B2E6D4"), //other
                Color.RED);                     //highlighted
    }
}
