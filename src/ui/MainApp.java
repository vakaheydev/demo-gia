package ui;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Обувь");
        stage.getIcons().add(new Image("/Icon.JPG"));
        stage.setScene(new LoginView().createScene(stage));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
