package ui;

import config.AppConfig;
import dao.ProductDao;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.Product;
import utils.Alerts;
import utils.ImageUtils;

import java.io.File;
import java.io.IOException;

public class ProductForm {

    private final Product product;
    private final ProductDao productDao = new ProductDao();

    // Предотвращаем открытие нескольких форм редактирования одновременно
    private static boolean isOpen = false;

    private TextField articleField;
    private TextField measureItemField;
    private TextField priceField;
    private TextField quantityField;
    private TextField saleField;
    private TextArea descriptionArea;

    private ComboBox<String> categoryBox;
    private ComboBox<String> supplierBox;
    private ComboBox<String> manufacturerBox;
    private ComboBox<String> nameBox;

    private ImageView photoPreview;

    // currentPhotoPath — путь, сохранённый в БД (может быть null)
    private final String currentPhotoPath;
    // pendingPhotoPath — путь к файлу, выбранному пользователем в текущей сессии (ещё не сохранён)
    private String pendingPhotoPath;

    public ProductForm(Product product) {
        this.product = product;
        this.currentPhotoPath = product != null ? product.getPhotoPath() : null;
    }

    public Product showAndWait() {
        if (isOpen) {
            Alerts.error("Форма редактирования уже открыта. Закройте её перед открытием новой.");
            return null;
        }
        isOpen = true;
        try {
            return openDialog();
        } finally {
            isOpen = false;
        }
    }

    private Product openDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(product == null ? "Добавление товара" : "Редактирование товара");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        ScrollPane scroll = new ScrollPane(createForm());
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(650);
        dialog.getDialogPane().setContent(scroll);

        fillFieldsIfEdit();

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            if (!isValid()) e.consume();
        });

        dialog.setResultConverter(button -> {
            if (button != saveButtonType) {
                // Отмена: удаляем временный файл если пользователь выбирал фото
                ImageUtils.deleteFile(pendingPhotoPath);
                return null;
            }
            return buildProduct();
        });

        return dialog.showAndWait().orElse(null);
    }

    private VBox createForm() {
        VBox form = new VBox(8);
        form.setPrefWidth(440);

        articleField = new TextField();
        measureItemField = new TextField();
        priceField = new TextField();
        quantityField = new TextField();
        saleField = new TextField();
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

        photoPreview = new ImageView();
        photoPreview.setFitWidth(AppConfig.PHOTO_WIDTH);
        photoPreview.setFitHeight(AppConfig.PHOTO_HEIGHT);
        photoPreview.setPreserveRatio(true);
        photoPreview.setImage(ImageUtils.loadForForm(currentPhotoPath));

        Button choosePhotoBtn = new Button("Выбрать фото");
        choosePhotoBtn.setOnAction(e -> {
            Window owner = choosePhotoBtn.getScene() != null ? choosePhotoBtn.getScene().getWindow() : null;
            choosePhoto(owner);
        });

        if (product != null) {
            form.getChildren().addAll(new Label("ID:"), new Label(String.valueOf(product.getId())));
        }

        form.getChildren().addAll(
                new Label("Артикул:"), articleField,
                new Label("Название:"), nameBox,
                new Label("Категория:"), categoryBox,
                new Label("Поставщик:"), supplierBox,
                new Label("Производитель:"), manufacturerBox,
                new Label("Единица измерения:"), measureItemField,
                new Label("Цена:"), priceField,
                new Label("Количество:"), quantityField,
                new Label("Скидка (%):"), saleField,
                new Label("Описание:"), descriptionArea,
                new Label("Фото товара:"), photoPreview, choosePhotoBtn
        );

        return form;
    }

    private void choosePhoto(Window owner) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Выберите изображение");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Изображения (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fc.showOpenDialog(owner);
        if (file == null) return;

        try {
            String newPath = ImageUtils.copyResized(file);

            // Если в этой сессии уже выбирали фото — удаляем предыдущий временный файл
            if (pendingPhotoPath != null) {
                ImageUtils.deleteFile(pendingPhotoPath);
            }

            pendingPhotoPath = newPath;
            photoPreview.setImage(ImageUtils.loadForForm(pendingPhotoPath));
        } catch (IOException ex) {
            Alerts.error("Ошибка при загрузке изображения: " + ex.getMessage());
        }
    }

    private void fillFieldsIfEdit() {
        if (product == null) return;
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
    }

    private boolean isValid() {
        if (articleField.getText().isBlank()) {
            Alerts.error("Введите артикул товара.");
            return false;
        }
        if (nameBox.getValue() == null) {
            Alerts.error("Выберите название товара.");
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
            if (price < 0) { Alerts.error("Цена не может быть отрицательной."); return false; }
        } catch (NumberFormatException e) {
            Alerts.error("Цена должна быть числом.");
            return false;
        }
        try {
            int qty = Integer.parseInt(quantityField.getText());
            if (qty < 0) { Alerts.error("Количество не может быть отрицательным."); return false; }
        } catch (NumberFormatException e) {
            Alerts.error("Количество должно быть целым числом.");
            return false;
        }
        try {
            int sale = Integer.parseInt(saleField.getText());
            if (sale < 0 || sale > 100) { Alerts.error("Скидка должна быть от 0 до 100."); return false; }
        } catch (NumberFormatException e) {
            Alerts.error("Скидка должна быть целым числом.");
            return false;
        }
        return true;
    }

    private Product buildProduct() {
        int id = product == null ? 0 : product.getId();

        String finalPhotoPath;
        if (pendingPhotoPath != null) {
            // Новое фото выбрано — удаляем старый файл из БД
            ImageUtils.deleteFile(currentPhotoPath);
            finalPhotoPath = pendingPhotoPath;
        } else {
            finalPhotoPath = currentPhotoPath;
        }

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
                finalPhotoPath
        );
    }
}
