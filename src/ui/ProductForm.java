package ui;

import dao.ProductDao;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Product;
import utils.Alerts;

public class ProductForm {
    private final Product product;
    private final ProductDao productDao = new ProductDao();

    private TextField articleField;
    private TextField measureItemField;
    private TextField priceField;
    private TextField quantityField;
    private TextField saleField;
    private TextField photoPathField;

    private TextArea descriptionArea;

    private ComboBox<String> categoryBox;
    private ComboBox<String> supplierBox;
    private ComboBox<String> manufacturerBox;
    private ComboBox<String> nameBox;

    public ProductForm(Product product) {
        this.product = product;
    }

    public Product showAndWait() {
        Dialog<Product> dialog = new Dialog<>();

        if (product == null) {
            dialog.setTitle("Добавление товара");
        } else {
            dialog.setTitle("Редактирование товара");
        }

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButtonType,
                ButtonType.CANCEL
        );

        VBox form = createForm();
        dialog.getDialogPane().setContent(form);

        fillFieldsIfEdit();

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);

        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!isValid()) {
                event.consume();
            }
        });

        dialog.setResultConverter(button -> {
            if (button != saveButtonType) {
                return null;
            }

            return buildProductFromFields();
        });

        return dialog.showAndWait().orElse(null);
    }

    private VBox createForm() {
        VBox form = new VBox(8);
        form.setPrefWidth(400);

        articleField = new TextField();
        measureItemField = new TextField();
        priceField = new TextField();
        quantityField = new TextField();
        saleField = new TextField();
        photoPathField = new TextField();

        descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);

        categoryBox = new ComboBox<>();
        supplierBox = new ComboBox<>();
        manufacturerBox = new ComboBox<>();
        nameBox = new ComboBox<>();

        categoryBox.getItems().addAll(productDao.findAllCategoryNames());
        supplierBox.getItems().addAll(productDao.findAllSupplierNames());
        manufacturerBox.getItems().addAll(productDao.findAllManufacturerNames());
        nameBox.getItems().addAll(productDao.findAllProductNames());

        if (product != null) {
            form.getChildren().addAll(
                new Label("ID:"),
                new Label(String.valueOf(product.getId()))
            );
        }

        form.getChildren().addAll(
                new Label("Артикул:"),
                articleField,

                new Label("Название:"),
                nameBox,

                new Label("Категория:"),
                categoryBox,

                new Label("Поставщик:"),
                supplierBox,

                new Label("Производитель:"),
                manufacturerBox,

                new Label("Единица измерения:"),
                measureItemField,

                new Label("Цена:"),
                priceField,

                new Label("Количество:"),
                quantityField,

                new Label("Скидка:"),
                saleField,

                new Label("Описание:"),
                descriptionArea,

                new Label("Путь к фото:"),
                photoPathField
        );

        return form;
    }

    private void fillFieldsIfEdit() {
        if (product == null) {
            return;
        }

        articleField.setText(product.getArticul());
        nameBox.setValue(product.getProductName());
        categoryBox.setValue(product.getCategory());
        supplierBox.setValue(product.getSupplier());
        manufacturerBox.setValue(product.getManufacturer());
        measureItemField.setText(product.getMeasureItem());
        priceField.setText(String.valueOf(product.getPrice()));
        quantityField.setText(String.valueOf(product.getQuantity()));
        saleField.setText(String.valueOf(product.getSale()));
        descriptionArea.setText(product.getDescription());
        photoPathField.setText(product.getPhotoPath());
    }

    private boolean isValid() {
        if (articleField.getText().isBlank()) {
            Alerts.error("Введите артикул товара.");
            return false;
        }

        if (nameBox.getValue() == null) {
            Alerts.error("Введите название товара.");
            return false;
        }

        if (categoryBox.getValue() == null) {
            Alerts.error("Выберите категорию.");
            return false;
        }

        if (supplierBox.getValue() == null) {
            Alerts.error("Выберите поставщика.");
            return false;
        }

        if (manufacturerBox.getValue() == null) {
            Alerts.error("Выберите производителя.");
            return false;
        }

        if (measureItemField.getText().isBlank()) {
            Alerts.error("Введите единицу измерения.");
            return false;
        }

        try {
            double price = Double.parseDouble(priceField.getText());

            if (price < 0) {
                Alerts.error("Цена не может быть отрицательной.");
                return false;
            }
        } catch (NumberFormatException e) {
            Alerts.error("Цена должна быть числом.");
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity < 0) {
                Alerts.error("Количество не может быть отрицательным.");
                return false;
            }
        } catch (NumberFormatException e) {
            Alerts.error("Количество должно быть целым числом.");
            return false;
        }

        try {
            int sale = Integer.parseInt(saleField.getText());

            if (sale < 0 || sale > 100) {
                Alerts.error("Скидка должна быть от 0 до 100.");
                return false;
            }
        } catch (NumberFormatException e) {
            Alerts.error("Скидка должна быть целым числом.");
            return false;
        }

        return true;
    }

    private Product buildProductFromFields() {
        int id = product == null ? 0 : product.getId();

        return new Product(
                id,
                articleField.getText(),
                nameBox.getValue(),
                measureItemField.getText(),
                Double.parseDouble(priceField.getText()),
                supplierBox.getValue(),
                manufacturerBox.getValue(),
                categoryBox.getValue(),
                Integer.parseInt(saleField.getText()),
                Integer.parseInt(quantityField.getText()),
                descriptionArea.getText(),
                photoPathField.getText()
        );
    }
}