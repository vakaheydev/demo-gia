package ui;

import config.AppConfig;
import dao.OrderDao;
import dao.ProductDao;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.Product;
import model.User;
import utils.Alerts;
import utils.ImageUtils;

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
        root.setStyle("-fx-font-family: '" + AppConfig.FONT_FAMILY + "'; -fx-background-color: " + AppConfig.BACKGROUND_COLOR + ";");

        table = createTable();
        root.setTop(createTopPanel(stage));
        root.setCenter(table);
        root.setBottom(createBottomPanel(stage));

        loadProducts();
        return new Scene(root, 1300, 680);
    }

    private TableView<Product> createTable() {
        TableView<Product> t = new TableView<>();
        t.setFixedCellSize(56);

        TableColumn<Product, String> photoCol = new TableColumn<>("Фото");
        photoCol.setCellValueFactory(new PropertyValueFactory<>("photoPath"));
        photoCol.setPrefWidth(90);
        photoCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView iv = new ImageView();
            {
                iv.setFitWidth(AppConfig.TABLE_PHOTO_WIDTH);
                iv.setFitHeight(AppConfig.TABLE_PHOTO_HEIGHT);
                iv.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty) { setGraphic(null); return; }
                iv.setImage(ImageUtils.loadForTable(path));
                setGraphic(iv);
            }
        });

        TableColumn<Product, String> articulCol = new TableColumn<>("Артикул");
        articulCol.setCellValueFactory(new PropertyValueFactory<>("articul"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Категория");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, String> descCol = new TableColumn<>("Описание");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(150);

        TableColumn<Product, String> manufacturerCol = new TableColumn<>("Производитель");
        manufacturerCol.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));

        TableColumn<Product, String> supplierCol = new TableColumn<>("Поставщик");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        // Колонка цены: зачёркнутая красная оригинальная + чёрная со скидкой
        TableColumn<Product, Product> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue()));
        priceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) { setGraphic(null); setText(null); return; }
                if (p.getSale() > 0) {
                    Text orig = new Text(String.format("%.2f", p.getPrice()));
                    orig.setStrikethrough(true);
                    orig.setFill(Color.RED);
                    double salePrice = p.getPrice() * (1 - p.getSale() / 100.0);
                    Text discounted = new Text(" " + String.format("%.2f", salePrice));
                    discounted.setFill(Color.BLACK);
                    setGraphic(new TextFlow(orig, discounted));
                    setText(null);
                } else {
                    setText(String.format("%.2f", p.getPrice()));
                    setGraphic(null);
                }
            }
        });

        TableColumn<Product, String> measureCol = new TableColumn<>("Ед. изм.");
        measureCol.setCellValueFactory(new PropertyValueFactory<>("measureItem"));

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Кол-во");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Integer> saleCol = new TableColumn<>("Скидка %");
        saleCol.setCellValueFactory(new PropertyValueFactory<>("sale"));

        t.getColumns().addAll(
                photoCol, articulCol, nameCol, categoryCol,
                descCol, manufacturerCol, supplierCol,
                priceCol, measureCol, quantityCol, saleCol
        );

        t.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) { setStyle(""); return; }
                if (p.getQuantity() == 0) {
                    setStyle("-fx-background-color: " + AppConfig.OUT_OF_STOCK_COLOR + ";");
                } else if (p.getSale() > AppConfig.HIGH_SALE_THRESHOLD) {
                    setStyle("-fx-background-color: " + AppConfig.HIGH_SALE_COLOR + ";");
                } else {
                    setStyle("");
                }
            }
        });

        return t;
    }

    private void loadProducts() {
        products = FXCollections.observableArrayList(productDao.findAll());
        filteredProducts = new FilteredList<>(products, p -> true);
        SortedList<Product> sorted = new SortedList<>(filteredProducts);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
        loadSuppliers();
        applyFilters();
    }

    private HBox createTopPanel(Stage stage) {
        searchField = new TextField();
        searchField.setPromptText("Поиск");

        supplierBox = new ComboBox<>();
        supplierBox.setPrefWidth(200);

        Button ordersBtn = new Button("Заказы");
        Button logoutBtn = new Button("Выйти");

        boolean canSearch = isAdmin() || isManager();
        searchField.setVisible(canSearch);
        supplierBox.setVisible(canSearch);
        ordersBtn.setVisible(canSearch);

        searchField.textProperty().addListener((obs, old, val) -> applyFilters());
        supplierBox.valueProperty().addListener((obs, old, val) -> applyFilters());

        logoutBtn.setOnAction(e -> stage.setScene(new LoginView().createScene(stage)));
        ordersBtn.setOnAction(e -> stage.setScene(new OrdersView(user).createScene(stage)));

        // ФИО пользователя — в правом верхнем углу
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label userLabel = new Label(user.getFio());

        HBox top = new HBox(10, searchField, supplierBox, ordersBtn, spacer, userLabel, logoutBtn);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(0, 0, 10, 0));
        return top;
    }

    private HBox createBottomPanel(Stage stage) {
        Button addBtn = new Button("Добавить");
        Button editBtn = new Button("Редактировать");
        Button deleteBtn = new Button("Удалить");

        addBtn.setVisible(isAdmin());
        editBtn.setVisible(isAdmin());
        deleteBtn.setVisible(isAdmin());

        addBtn.setOnAction(e -> {
            Product product = new ProductForm(null).showAndWait();
            if (product != null) {
                productDao.create(product);
                Alerts.info("Товар успешно создан");
            }
            loadProducts();
        });

        editBtn.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alerts.error("Выберите товар для редактирования");
                return;
            }
            Product product = new ProductForm(selected).showAndWait();
            if (product != null) {
                productDao.update(product);
                Alerts.info("Товар успешно обновлён");
            }
            loadProducts();
        });

        deleteBtn.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alerts.error("Выберите товар для удаления");
                return;
            }
            if (orderDao.hasProductInOrder(selected.getArticul())) {
                Alerts.error("Товар присутствует в заказе — удаление невозможно.");
                return;
            }
            if (!Alerts.confirm("Удалить товар «" + selected.getProductName() + "»?")) {
                return;
            }
            productDao.deleteById(selected.getId());
            Alerts.info("Товар успешно удалён");
            loadProducts();
        });

        HBox bottom = new HBox(10, addBtn, editBtn, deleteBtn);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        return bottom;
    }

    private void loadSuppliers() {
        supplierBox.getItems().clear();
        supplierBox.getItems().add("Все поставщики");
        supplierBox.getItems().addAll(productDao.findAllSupplierNames());
        supplierBox.getSelectionModel().selectFirst();
    }

    private void applyFilters() {
        if (filteredProducts == null) return;
        filteredProducts.setPredicate(p -> matchesSearch(p) && matchesSupplier(p));
    }

    private boolean matchesSearch(Product p) {
        String q = searchField.getText();
        if (q == null || q.isBlank()) return true;
        q = q.toLowerCase();
        return p.getArticul().toLowerCase().contains(q)
                || p.getProductName().toLowerCase().contains(q)
                || p.getCategory().toLowerCase().contains(q)
                || p.getSupplier().toLowerCase().contains(q)
                || p.getManufacturer().toLowerCase().contains(q);
    }

    private boolean matchesSupplier(Product p) {
        String sel = supplierBox.getValue();
        if (sel == null || sel.equalsIgnoreCase("Все поставщики")) return true;
        return p.getSupplier().equalsIgnoreCase(sel);
    }

    private boolean isAdmin() {
        return user.getRole().equalsIgnoreCase(AppConfig.ROLE_ADMIN);
    }

    private boolean isManager() {
        return user.getRole().equalsIgnoreCase(AppConfig.ROLE_MANAGER);
    }
}
