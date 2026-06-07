package ui;

import dao.UserDao;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
        root.setStyle("-fx-font-family: 'Times New Roman'; -fx-background-color: #FFFFFF;");

        root.setPadding(new Insets(20));
        Label title = new Label("Авторизация");

        TextField login = new TextField();
        login.setPromptText("Введите логин");

        PasswordField password = new PasswordField();
        password.setPromptText("Введите пароль");

        Button loginBtn = new Button("Войти");
        Button guestBtn = new Button("Войти как гость");

        loginBtn.setStyle("-fx-background-color: #00FA9A;");

        loginBtn.setOnAction(event -> {
            var user = userDao.getUserByLoginAndPassword(login.getText(), password.getText());
            if (user == null) {
                Alerts.error("Неверный логин и / или пароль");
                return;
            }

            System.out.println("Авторизация прошла успешно");
            stage.setScene(new ProductView(user).createScene(stage));
        });

        guestBtn.setOnAction(event -> {
            System.out.println("Авторизация прошла успешно");
            User guest = new User(0, "Гость", "Гость", "Гость", "Гость");
            stage.setScene(new ProductView(guest).createScene(stage));
        });

        ImageView logo = new ImageView(new Image("/Icon.JPG"));
        logo.setFitWidth(80);
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        root.getChildren().addAll(logo, title, login, password, loginBtn, guestBtn);

        return new Scene(root, 400, 300);
    }

}
