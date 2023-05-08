package program.view.themes;

import javafx.scene.paint.Color;
import program.view.Theme;

public class SakuraTheme extends Theme {

    public SakuraTheme(){
        super(Color.web("#FE5D9F"), //primary
                Color.web("#FE5D9F"), //secondary
                Color.web("#F686BD"), //tertiary
                Color.web("#F1E4F3"), //land
                Color.web("#F4BBD3"), //buildings
                Color.web("#F4BBD3"), //other
                Color.RED);                     //highlighted
    }
}
