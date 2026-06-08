package ui;

import config.AppConfig;
import dao.OrderDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.Order;
import model.User;
import utils.Alerts;

import java.time.LocalDate;

public class OrdersView {

    private final User user;
    private final OrderDao orderDao = new OrderDao();

    private TableView<Order> table;
    private ObservableList<Order> orders;

    public OrdersView(User user) {
        this.user = user;
    }

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-font-family: '" + AppConfig.FONT_FAMILY + "'; -fx-background-color: " + AppConfig.BACKGROUND_COLOR + ";");

        table = createTable();
        root.setTop(createTopPanel(stage));
        root.setCenter(table);
        root.setBottom(createBottomPanel());

        loadOrders();
        return new Scene(root, 1200, 600);
    }

    private void loadOrders() {
        orders = FXCollections.observableArrayList(orderDao.findAll());
        table.setItems(orders);
    }

    private HBox createTopPanel(Stage stage) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label(user.getFio());

        Button logoutBtn = new Button("Выйти");
        Button productsBtn = new Button("Товары");

        logoutBtn.setOnAction(e -> stage.setScene(new LoginView().createScene(stage)));
        productsBtn.setOnAction(e -> stage.setScene(new ProductView(user).createScene(stage)));

        HBox top = new HBox(10, productsBtn, spacer, userLabel, logoutBtn);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle("-fx-background-color: " + AppConfig.TOP_BAR_COLOR + ";");
        top.setPadding(new Insets(6));
        return top;
    }

    private HBox createBottomPanel() {
        Button addBtn = new Button("Добавить заказ");
        Button editBtn = new Button("Изменить заказ");
        Button deleteBtn = new Button("Удалить заказ");

        addBtn.setVisible(isAdmin());
        editBtn.setVisible(isAdmin());
        deleteBtn.setVisible(isAdmin());

        addBtn.setOnAction(e -> {
            Order order = new OrderForm(null, user).showAndWait();
            if (order != null) {
                orderDao.create(order);
                Alerts.info("Заказ успешно создан");
                loadOrders();
            }
        });

        editBtn.setOnAction(e -> {
            Order selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alerts.error("Выберите заказ для редактирования");
                return;
            }
            Order order = new OrderForm(selected, user).showAndWait();
            if (order != null) {
                orderDao.update(order);
                Alerts.info("Заказ успешно обновлён");
                loadOrders();
            }
        });

        deleteBtn.setOnAction(e -> {
            Order selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alerts.error("Выберите заказ для удаления");
                return;
            }
            if (!Alerts.confirm("Удалить заказ #" + selected.getId() + "?")) {
                return;
            }
            orderDao.deleteById(selected.getId());
            Alerts.info("Заказ успешно удалён");
            loadOrders();
        });

        HBox bottom = new HBox(10, addBtn, editBtn, deleteBtn);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        return bottom;
    }

    private TableView<Order> createTable() {
        TableView<Order> t = new TableView<>();

        TableColumn<Order, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, String> articulCol = new TableColumn<>("Артикул");
        articulCol.setCellValueFactory(new PropertyValueFactory<>("articul"));

        TableColumn<Order, LocalDate> orderDateCol = new TableColumn<>("Дата заказа");
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        TableColumn<Order, LocalDate> deliveryDateCol = new TableColumn<>("Дата выдачи");
        deliveryDateCol.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));

        TableColumn<Order, String> pickupCol = new TableColumn<>("Адрес пункта выдачи");
        pickupCol.setCellValueFactory(new PropertyValueFactory<>("pickupPoint"));
        pickupCol.setPrefWidth(200);

        TableColumn<Order, String> clientCol = new TableColumn<>("ФИО клиента");
        clientCol.setCellValueFactory(new PropertyValueFactory<>("clientFio"));

        TableColumn<Order, Integer> codeCol = new TableColumn<>("Код получения");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Order, String> statusCol = new TableColumn<>("Статус заказа");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        t.getColumns().addAll(idCol, articulCol, orderDateCol, deliveryDateCol,
                pickupCol, clientCol, codeCol, statusCol);
        return t;
    }

    private boolean isAdmin() {
        return user.getRole().equalsIgnoreCase(AppConfig.ROLE_ADMIN);
    }
}
