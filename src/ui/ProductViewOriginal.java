package ui;

import dao.ProductDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.*;
import model.Product;
import model.User;

public class ProductViewOriginal {
    private final User user;
    private final ProductDao productDao = new ProductDao();

    public ProductViewOriginal(User user) {
        this.user = user;
    }

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TableView<Product> table = createProductTable();

        ObservableList<Product> products =
                FXCollections.observableArrayList(productDao.findAll());

        table.setItems(products);

        TextField searchField = new TextField();
        searchField.setPromptText("Поиск");

        ComboBox<String> supplierBox = new ComboBox<>();
        supplierBox.getItems().add("Все поставщики");
        supplierBox.getItems().addAll(productDao.findAllSupplierNames());
        supplierBox.getSelectionModel().selectFirst();

        Button ordersButton = new Button("Заказы");
        Button logoutButton = new Button("Выйти");

        HBox top = new HBox(10, searchField, supplierBox, ordersButton,
                new Label(user.getFio()), logoutButton);

        Button addButton = new Button("Добавить");
        Button editButton = new Button("Редактировать");
        Button deleteButton = new Button("Удалить");

        HBox bottom = new HBox(10, addButton, editButton, deleteButton);

        root.setTop(top);
        root.setCenter(table);
        root.setBottom(bottom);

        boolean isAdmin = user.getRole().equals("Администратор");
        boolean isManager = user.getRole().equals("Менеджер");

        addButton.setVisible(isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);

        searchField.setVisible(isAdmin || isManager);
        supplierBox.setVisible(isAdmin || isManager);
        ordersButton.setVisible(isAdmin || isManager);

        logoutButton.setOnAction(event -> {
            stage.setScene(new LoginViewOriginal().createScene(stage));
        });

        FilteredList<Product> filtered = new FilteredList<>(products, p -> true);

        SortedList<Product> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sorted);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filtered.setPredicate(product -> {
                String query = newValue == null ? "" : newValue.toLowerCase();

                if (query.isBlank()) {
                    return true;
                }

                return product.getProductName().toLowerCase().contains(query)
                        || product.getArticul().toLowerCase().contains(query)
                        || product.getSupplier().toLowerCase().contains(query)
                        || product.getCategory().toLowerCase().contains(query)
                        || product.getManufacturer().toLowerCase().contains(query);
            });
        });

//        ordersButton.setOnAction(event -> {
//            stage.setScene(createOrderScene(stage, user));
//        });

        return new Scene(root, 1100, 650);
    }

    private TableView<Product> createProductTable() {
        TableView<Product> table = new TableView<>();

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> articleCol = new TableColumn<>("Артикул");
        articleCol.setCellValueFactory(new PropertyValueFactory<>("articul"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<Product, String> supplierCol = new TableColumn<>("Поставщик");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Количество");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Integer> saleCol = new TableColumn<>("Скидка");
        saleCol.setCellValueFactory(new PropertyValueFactory<>("sale"));

        table.getColumns().addAll(
                idCol,
                articleCol,
                nameCol,
                supplierCol,
                priceCol,
                quantityCol,
                saleCol
        );

        return table;
    }
}
