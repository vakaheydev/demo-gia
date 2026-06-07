package ui;

import dao.UserDao;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;
import utils.Alerts;

public class LoginViewOriginal {
    private final UserDao userDao = new UserDao();

    public Scene createScene(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Авторизация");

        TextField loginField = new TextField();
        loginField.setPromptText("Логин");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");

        Button loginBtn = new Button("Войти");
        Button guestBtn = new Button("Войти как гость");

        loginBtn.setOnAction(event -> {
            User user = userDao.getUserByLoginAndPassword(
                    loginField.getText(),
                    passwordField.getText()
            );

            if (user == null) {
                Alerts.error("Неверный логин и/или пароль");
                return;
            }

            System.out.println("Авторизация прошла успешно");
            stage.setScene(new ProductViewOriginal(user).createScene(stage));
        });

        guestBtn.setOnAction(event -> {
            User guest = new User(0, "guest", "Гость", "Гость", "Гость");
            System.out.println("Гость авторизовался");
            stage.setScene(new ProductViewOriginal(guest).createScene(stage));
        });

        root.getChildren().addAll(
                title,
                loginField,
                passwordField,
                loginBtn,
                guestBtn
        );

        return new Scene(root, 400, 300);
    }
}
