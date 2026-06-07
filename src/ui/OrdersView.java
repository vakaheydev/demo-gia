package ui;

import dao.OrderDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Order;
import model.Product;
import model.User;
import utils.Alerts;

import java.time.LocalDate;

public class OrdersView {
    private final User user;
    private ObservableList<Order> orders;
    private final OrderDao orderDao = new OrderDao();
    private TableView<Order> table;

    public OrdersView(User user) {
        this.user = user;
    }

    public Scene createScene(Stage stage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20));

        borderPane.setTop(createTop(stage));
        borderPane.setCenter(createTable());
        borderPane.setBottom(createBottom(stage));

        loadOrders();
        return new Scene(borderPane, 1200, 600);
    }

    private void loadOrders() {
        orders = FXCollections.observableArrayList(orderDao.findAll());
        table.setItems(orders);
    }

    public HBox createTop(Stage stage) {
        HBox root = new HBox(10);
        Label userLabel = new Label("Пользователь " + user.getFio());

        Button logoutBtn = new Button("Выйти");
        Button productsBtn = new Button("Товары");

        logoutBtn.setOnAction(event -> {
            stage.setScene(new LoginView().createScene(stage));
        });

        productsBtn.setOnAction(event -> {
            stage.setScene(new ProductView(user).createScene(stage));
        });

        root.getChildren().addAll(
                userLabel, logoutBtn, productsBtn
        );

        return root;
    }

    public HBox createBottom(Stage stage) {
        HBox root = new HBox(10);
        Button addBtn = new Button("Добавить заказ");
        Button editBtn = new Button("Изменить заказ");
        Button deleteBtn = new Button("Удалить заказ");

        addBtn.setOnAction(event -> {
            Order order = new OrderForm(null, user).showAndWait();
            if (order != null) {
                orderDao.create(order);
                Alerts.info("Заказ успешно создан");
                loadOrders();
            }
        });

        editBtn.setOnAction(event -> {
            Order selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }

            Order order = new OrderForm(selected, user).showAndWait();
            if (order != null) {
                orderDao.update(order);
                Alerts.info("Заказ успешно обновлен");
                loadOrders();
            }
        });

        deleteBtn.setOnAction(event -> {
            Order selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                return;
            }

            if (!Alerts.confirm("Подтвердите удаление заказа id=" + selected.getId())) {
                return;
            }

            orderDao.deleteById(selected.getId());
            Alerts.info("Заказ успешно удален");
            loadOrders();
        });

        root.getChildren().addAll(
                addBtn, editBtn, deleteBtn
        );

        addBtn.setVisible(isAdmin());
        editBtn.setVisible(isAdmin());
        deleteBtn.setVisible(isAdmin());

        return root;
    }

    public boolean isAdmin() {
        return user.getRole().equalsIgnoreCase("администратор");
    }

    public boolean isManager() {
        return user.getRole().equalsIgnoreCase("Менеджер");
    }

    public TableView<Order> createTable() {
        table = new TableView<>();

        TableColumn<Order, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, String> articul = new TableColumn<>("Артикул");
        articul.setCellValueFactory(new PropertyValueFactory<>("articul"));

        TableColumn<Order, LocalDate> orderDate = new TableColumn<>("Дата заказа");
        orderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        TableColumn<Order, LocalDate> deliveryDate = new TableColumn<>("Дата доставки");
        deliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));

        TableColumn<Order, String> pickupPoint = new TableColumn<>("Адрес пункта выдачи");
        pickupPoint.setCellValueFactory(new PropertyValueFactory<>("pickupPoint"));

        TableColumn<Order, String> clientFio = new TableColumn<>("ФИО клиента");
        clientFio.setCellValueFactory(new PropertyValueFactory<>("clientFio"));

        TableColumn<Order, Integer> code = new TableColumn<>("Код для получения");
        code.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Order, Integer> status = new TableColumn<>("Статус заказа");
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(
                id,
                articul,
                orderDate,
                deliveryDate,
                pickupPoint,
                clientFio,
                code,
                status
        );

        return table;
    }
}
