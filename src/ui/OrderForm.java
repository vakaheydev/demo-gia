package ui;

import dao.OrderDao;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Order;
import model.User;
import utils.Alerts;

import java.util.Random;

public class OrderForm {
    private final Order order;
    private final User user;
    private final OrderDao orderDao = new OrderDao();

    private TextField articul;
    private ComboBox<String> status;
    private ComboBox<String> address;
    private DatePicker orderDate;
    private DatePicker deliveryDate;

    public OrderForm(Order order, User user) {
        this.order = order;
        this.user = user;
    }

    public Order showAndWait() {
        Dialog<Order> dialog = new Dialog<>();

        if (order != null) {
            dialog.setTitle("Редактирование заказа");
        } else {
            dialog.setTitle("Добавление заказа");
        }

        ButtonType saveBtnType = new ButtonType(
                "Сохранить",
                ButtonBar.ButtonData.OK_DONE
        );
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        Button saveBtn = (Button) dialog.getDialogPane().lookupButton(saveBtnType);
        saveBtn.addEventFilter(ActionEvent.ACTION, event -> {
            if (!isValid()) {
                event.consume();
            }
        });

        dialog.setResultConverter(btn -> {
            if (btn != saveBtnType) {
                return null;
            }

            return buildFormData();
        });

        VBox form = createForm();
        dialog.getDialogPane().setContent(form);

        fillIfEdit();

        return dialog.showAndWait().orElse(null);
    }

    private void fillIfEdit() {
        if (order == null) {
            return;
        }

        articul.setText(order.getArticul());
        orderDate.setValue(order.getOrderDate());
        deliveryDate.setValue(order.getDeliveryDate());
        address.setValue(order.getPickupPoint());
        status.setValue(order.getStatus());
    }

    private Order buildFormData() {
        int id = order == null ? 0 : order.getId();

        return new Order(
                id,
                articul.getText(),
                orderDate.getValue(),
                deliveryDate.getValue(),
                address.getValue(),
                user.getFio(),
                new Random().nextInt(1000),
                status.getValue()
        );
    }

    private boolean isValid() {
        if (articul.getText().isBlank()) {
            Alerts.error("Введите артикул");
            return false;
        }

        if (status.getValue() == null) {
            Alerts.error("Выберите статус");
            return false;
        }

        if (address.getValue() == null) {
            Alerts.error("Выберите адрес");
            return false;
        }

        if (orderDate.getValue() == null) {
            Alerts.error("Выберите дату заказа");
            return false;
        }

        if (deliveryDate.getValue() == null) {
            Alerts.error("Выберите дату выдачи");
            return false;
        }

        return true;
    }

    private VBox createForm() {
        VBox form = new VBox(10);
        form.setPrefWidth(400);

        articul = new TextField();
        status = new ComboBox<>();
        address = new ComboBox<>();
        orderDate = new DatePicker();
        deliveryDate = new DatePicker();

        status.getItems().addAll(orderDao.getStatuses());
        address.getItems().addAll(orderDao.getAddresses());

        form.getChildren().addAll(
                new Label("Артикул"),
                articul,

                new Label("Статус"),
                status,

                new Label("Адрес пункта выдачи"),
                address,

                new Label("Дата заказа"),
                orderDate,

                new Label("Дата выдачи"),
                deliveryDate
        );

        return form;
    }
}
