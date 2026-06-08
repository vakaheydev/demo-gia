package ui;

import config.AppConfig;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(AppConfig.APP_TITLE);
        stage.getIcons().add(new Image(AppConfig.LOGO_PATH));
        stage.setScene(new LoginView().createScene(stage));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
