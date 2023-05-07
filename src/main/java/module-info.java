module program {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;


    opens program to javafx.fxml;
    exports program;
    exports program.view;
    opens program.view to javafx.fxml;
    exports program.controller;
    opens program.controller to javafx.fxml;
    exports program.view.themes;
    opens program.view.themes to javafx.fxml;
}