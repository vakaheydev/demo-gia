package model;

public class Product {
    private int id;
    private String articul;
    private String productName;
    private String measureItem;
    private double price;
    private String supplier;
    private String manufacturer;
    private String category;
    private int sale;
    private int quantity;
    private String description;
    private String photoPath;

    public Product(int id, String articul, String productName, String measureItem, double price, String supplier, String manufacturer, String category, int sale, int quantity, String description, String photoPath) {
        this.id = id;
        this.articul = articul;
        this.productName = productName;
        this.measureItem = measureItem;
        this.price = price;
        this.supplier = supplier;
        this.manufacturer = manufacturer;
        this.category = category;
        this.sale = sale;
        this.quantity = quantity;
        this.description = description;
        this.photoPath = photoPath;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMeasureItem() {
        return measureItem;
    }

    public void setMeasureItem(String measureItem) {
        this.measureItem = measureItem;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", articul='" + articul + '\'' +
                ", productName='" + productName + '\'' +
                ", measureItem='" + measureItem + '\'' +
                ", price=" + price +
                ", supplier='" + supplier + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", category='" + category + '\'' +
                ", sale=" + sale +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", photoPath='" + photoPath + '\'' +
                '}';
    }
}
