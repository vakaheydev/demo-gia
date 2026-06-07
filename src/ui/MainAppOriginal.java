package ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainAppOriginal extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Обувь");
        stage.setScene(new LoginViewOriginal().createScene(stage));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
