package model;

import java.time.LocalDate;

public class Order {
    private int id;
    private String articul;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private String pickupPoint;
    private String clientFio;
    private int code;
    private String status;

    public Order(int id, String articul, LocalDate orderDate, LocalDate deliveryDate, String pickupPoint, String clientFio, int code, String status) {
        this.id = id;
        this.articul = articul;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.pickupPoint = pickupPoint;
        this.clientFio = clientFio;
        this.code = code;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArticul() {
        return articul;
    }

    public void setArticul(String articul) {
        this.articul = articul;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(String pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public String getClientFio() {
        return clientFio;
    }

    public void setClientFio(String clientFio) {
        this.clientFio = clientFio;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
