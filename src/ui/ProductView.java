package ui;

import dao.OrderDao;
import dao.ProductDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Product;
import model.User;
import utils.Alerts;

public class ProductView {
    private final User user;
    private final ProductDao productDao = new ProductDao();
    private final OrderDao orderDao = new OrderDao();

    private TableView<Product> table;
    private ObservableList<Product> products;
    private FilteredList<Product> filteredProducts;

    private TextField searchField;
    private ComboBox<String> supplierBox;

    public ProductView(User user) {
        this.user = user;
    }

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        table = createTable();
        HBox top = createTopPanel(stage);
        HBox bottom = createBottomPanel(stage);

        root.setTop(top);
        root.setCenter(table);
        root.setBottom(bottom);

        loadProducts();
        return new Scene(root, 1100, 650);
    }

    private TableView<Product> createTable() {
        TableView<Product> table = new TableView<>();

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> articulCol = new TableColumn<>("Артикул");
        articulCol.setCellValueFactory(new PropertyValueFactory<>("articul"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Категория");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, String> supplierCol = new TableColumn<>("Поставщик");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        TableColumn<Product, String> manufacturerCol = new TableColumn<>("Производитель");
        manufacturerCol.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Количество");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Integer> saleCol = new TableColumn<>("Скидка");
        saleCol.setCellValueFactory(new PropertyValueFactory<>("sale"));

        table.getColumns().addAll(
                idCol,
                articulCol,
                nameCol,
                categoryCol,
                supplierCol,
                manufacturerCol,
                priceCol,
                quantityCol,
                saleCol
        );

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);

                if (empty || product == null) {
                    setStyle("");
                    return;
                }

                if (product.getQuantity() == 0) {
                    setStyle("-fx-background-color: lightblue;");
                } else if (product.getSale() > 15) {
                    setStyle("-fx-background-color: #2E8B57;");
                } else {
                    setStyle("");
                }
            }
        });

        return table;
    }

    private void loadProducts() {
        products = FXCollections.observableArrayList(productDao.findAll());

        filteredProducts = new FilteredList<>(products, product -> true);

        SortedList<Product> sortedProducts = new SortedList<>(filteredProducts);
        sortedProducts.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedProducts);
        loadSuppliers();
        applyFilters();
    }

    private HBox createBottomPanel(Stage stage) {
        Button addBtn = new Button("Добавить");
        Button editBtn = new Button("Редактировать");
        Button deleteBtn = new Button("Удалить");

        addBtn.setVisible(isAdmin());
        editBtn.setVisible(isAdmin());
        deleteBtn.setVisible(isAdmin());

        addBtn.setOnAction(evetn -> {
            Product product = new ProductForm(null).showAndWait();
            if (product != null) {
                productDao.create(product);
                Alerts.info("Товар успешно создан");
            }

            loadProducts();
        });

        editBtn.setOnAction(event -> {
            Product selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                Alerts.error("Выберите товар для редактирования");
            }

            Product product = new ProductForm(selected).showAndWait();

            if (product != null) {
                productDao.update(product);
                Alerts.info("Товар успешно обновлен");
            }

            loadProducts();
        });

        deleteBtn.setOnAction(event -> {
            Product selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                Alerts.error("Выберите товар для удаления");
                return;
            }

            if (orderDao.hasProductInOrder(selected.getArticul())) {
                Alerts.error("Товар присутствует в заказе, его нельзя удалить!");
                return;
            }

            if (!Alerts.confirm("Подтвердите удаление товара " + selected.getId())) {
                return;
            }

            System.out.println("Удаляем товар " + selected.getId());
            productDao.deleteById(selected.getId());

            Alerts.info("Товар успешно удален");
            loadProducts();
        });

        return new HBox(
                10,
                addBtn,
                editBtn,
                deleteBtn
        );
    }

    private HBox createTopPanel(Stage stage) {
        searchField = new TextField();
        searchField.setPromptText("Поиск");

        supplierBox = new ComboBox<>();
        supplierBox.setPrefWidth(200);

        Button ordersBtn = new Button("Заказы");
        Button logoutBtn = new Button("Выйти");

        Label userLabel = new Label(user.getFio());

        boolean canSearch = isAdmin() || isManager();

        searchField.setVisible(canSearch);
        supplierBox.setVisible(canSearch);
        ordersBtn.setVisible(canSearch);

         searchField.textProperty().addListener((x1, x2, x3) -> applyFilters());
         supplierBox.valueProperty().addListener((x1, x2, x3) -> applyFilters());

        logoutBtn.setOnAction(event -> {
            stage.setScene(new LoginView().createScene(stage));
        });

        ordersBtn.setOnAction(event -> {
            stage.setScene(new OrdersView(user).createScene(stage));
        });

        return new HBox(
                10,
                searchField,
                supplierBox,
                ordersBtn,
                userLabel,
                logoutBtn
        );
    }

    private void loadSuppliers() {
        supplierBox.getItems().clear();

        supplierBox.getItems().add("Все поставщики");
        supplierBox.getItems().addAll(productDao.findAllSupplierNames());

        supplierBox.getSelectionModel().selectFirst();
    }

    private void applyFilters() {
        if (filteredProducts == null) {
            return;
        }

        filteredProducts.setPredicate(product -> matchesSearch(product) && matchesSupplier(product));
    }

    private boolean matchesSupplier(Product product) {
        String selectedSupplier = supplierBox.getValue();

        if (selectedSupplier == null || selectedSupplier.equalsIgnoreCase("Все поставщики")) {
            return true;
        }

        return product.getSupplier().equalsIgnoreCase(selectedSupplier);
    }

    private boolean matchesSearch(Product product) {
        String query = searchField.getText();

        if (query == null || query.isEmpty()) {
            return true;
        }

        String q = query.toLowerCase();

        return product.getArticul().toLowerCase().contains(q)
                || product.getProductName().toLowerCase().contains(q)
                || product.getCategory().toLowerCase().contains(q)
                || product.getSupplier().toLowerCase().contains(q)
                || product.getManufacturer().toLowerCase().contains(q);
    }

    private boolean isAdmin() {
        return user.getRole().equalsIgnoreCase("Администратор");
    }

    private boolean isManager() {
        return user.getRole().equalsIgnoreCase("Менеджер");
    }
}
