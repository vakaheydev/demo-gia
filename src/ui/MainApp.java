package ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Обувь");
        stage.setScene(new LoginView().createScene(stage));
        stage.show();
    }
}
