package ui;

import config.AppConfig;
import dao.UserDao;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;
import utils.Alerts;

public class LoginView {

    private final UserDao userDao = new UserDao();

    public Scene createScene(Stage stage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-font-family: '" + AppConfig.FONT_FAMILY + "'; -fx-background-color: " + AppConfig.BACKGROUND_COLOR + ";");
        root.setPadding(new Insets(20));

        ImageView logo = new ImageView(new Image(AppConfig.LOGO_PATH));
        logo.setFitWidth(80);
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        Label title = new Label("Авторизация");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TextField loginField = new TextField();
        loginField.setPromptText("Введите логин");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Введите пароль");

        Button loginBtn = new Button("Войти");
        loginBtn.setStyle("-fx-background-color: " + AppConfig.COLOR_ACCENT + ";");

        Button guestBtn = new Button("Войти как гость");

        loginBtn.setOnAction(event -> {
            User user = userDao.getUserByLoginAndPassword(loginField.getText(), passwordField.getText());
            if (user == null) {
                Alerts.error("Неверный логин и / или пароль");
                return;
            }
            stage.setScene(new ProductView(user).createScene(stage));
        });

        guestBtn.setOnAction(event -> {
            User guest = new User(0, "Гость", "", "Гость", "Гость");
            stage.setScene(new ProductView(guest).createScene(stage));
        });

        root.getChildren().addAll(logo, title, loginField, passwordField, loginBtn, guestBtn);
        return new Scene(root, 400, 300);
    }
}
