package program;

import javafx.application.Application;
import javafx.stage.Stage;
import program.view.View;

import java.io.IOException;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        View.instantiateView(primaryStage);
        primaryStage.setTitle("Retroveje");
    }
}
