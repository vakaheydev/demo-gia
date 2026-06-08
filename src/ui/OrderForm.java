package ui;

import dao.OrderDao;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Order;
import model.User;
import utils.Alerts;

import java.util.Random;

public class OrderForm {

    private final Order order;
    private final User user;
    private final OrderDao orderDao = new OrderDao();

    private TextField articulField;
    private ComboBox<String> statusBox;
    private ComboBox<String> addressBox;
    private DatePicker orderDatePicker;
    private DatePicker deliveryDatePicker;

    public OrderForm(Order order, User user) {
        this.order = order;
        this.user = user;
    }

    public Order showAndWait() {
        Dialog<Order> dialog = new Dialog<>();
        dialog.setTitle(order == null ? "Добавление заказа" : "Редактирование заказа");

        ButtonType saveBtnType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(createForm());
        fillIfEdit();

        Button saveBtn = (Button) dialog.getDialogPane().lookupButton(saveBtnType);
        saveBtn.addEventFilter(ActionEvent.ACTION, e -> {
            if (!isValid()) e.consume();
        });

        dialog.setResultConverter(btn -> btn == saveBtnType ? buildOrder() : null);

        return dialog.showAndWait().orElse(null);
    }

    private VBox createForm() {
        VBox form = new VBox(10);
        form.setPrefWidth(400);

        articulField = new TextField();
        statusBox = new ComboBox<>();
        addressBox = new ComboBox<>();
        orderDatePicker = new DatePicker();
        deliveryDatePicker = new DatePicker();

        statusBox.getItems().addAll(orderDao.getStatuses());
        addressBox.getItems().addAll(orderDao.getAddresses());

        form.getChildren().addAll(
                new Label("Артикул:"), articulField,
                new Label("Статус заказа:"), statusBox,
                new Label("Адрес пункта выдачи:"), addressBox,
                new Label("Дата заказа:"), orderDatePicker,
                new Label("Дата выдачи:"), deliveryDatePicker
        );

        return form;
    }

    private void fillIfEdit() {
        if (order == null) return;
        articulField.setText(order.getArticul());
        orderDatePicker.setValue(order.getOrderDate());
        deliveryDatePicker.setValue(order.getDeliveryDate());
        addressBox.setValue(order.getPickupPoint());
        statusBox.setValue(order.getStatus());
    }

    private boolean isValid() {
        if (articulField.getText().isBlank()) {
            Alerts.error("Введите артикул товара.");
            return false;
        }
        if (statusBox.getValue() == null) {
            Alerts.error("Выберите статус заказа.");
            return false;
        }
        if (addressBox.getValue() == null) {
            Alerts.error("Выберите адрес пункта выдачи.");
            return false;
        }
        if (orderDatePicker.getValue() == null) {
            Alerts.error("Укажите дату заказа.");
            return false;
        }
        if (deliveryDatePicker.getValue() == null) {
            Alerts.error("Укажите дату выдачи.");
            return false;
        }
        return true;
    }

    private Order buildOrder() {
        int id = order == null ? 0 : order.getId();
        return new Order(
                id,
                articulField.getText(),
                orderDatePicker.getValue(),
                deliveryDatePicker.getValue(),
                addressBox.getValue(),
                user.getFio(),
                new Random().nextInt(1000),
                statusBox.getValue()
        );
    }
}
