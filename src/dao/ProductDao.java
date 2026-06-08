package dao;

import db.DatabaseConnection;
import model.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    private final DictionaryDao dict = new DictionaryDao();

    public void create(Product product) {
        String sql = """
            INSERT INTO products (articul, product_name_id, measure_item, price, supplier_id, manufacturer_id, category_id, sale, quantity, description, photo_path)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, product.getArticul());
            ps.setInt(2, dict.getIdByName(product.getProductName(), "product_names"));
            ps.setString(3, product.getMeasureItem());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, dict.getIdByName(product.getSupplier(), "suppliers"));
            ps.setInt(6, dict.getIdByName(product.getManufacturer(), "manufacturers"));
            ps.setInt(7, dict.getIdByName(product.getCategory(), "categories"));
            ps.setInt(8, product.getSale());
            ps.setInt(9, product.getQuantity());
            ps.setString(10, product.getDescription());
            ps.setString(11, product.getPhotoPath());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Product product) {
        String sql = """
            UPDATE products SET
                articul = ?,
                product_name_id = ?,
                measure_item = ?,
                price = ?,
                supplier_id = ?,
                manufacturer_id = ?,
                category_id = ?,
                sale = ?,
                quantity = ?,
                description = ?,
                photo_path = ?
            WHERE id = ?
        """;
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, product.getArticul());
            ps.setInt(2, dict.getIdByName(product.getProductName(), "product_names"));
            ps.setString(3, product.getMeasureItem());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, dict.getIdByName(product.getSupplier(), "suppliers"));
            ps.setInt(6, dict.getIdByName(product.getManufacturer(), "manufacturers"));
            ps.setInt(7, dict.getIdByName(product.getCategory(), "categories"));
            ps.setInt(8, product.getSale());
            ps.setInt(9, product.getQuantity());
            ps.setString(10, product.getDescription());
            ps.setString(11, product.getPhotoPath());
            ps.setInt(12, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Product> findAll() {
        String sql = """
            SELECT
                p.id,
                p.articul,
                pn.name AS product_name,
                p.measure_item,
                p.price,
                s.name  AS supplier_name,
                m.name  AS manufacturer_name,
                c.name  AS category_name,
                p.sale,
                p.quantity,
                p.description,
                p.photo_path
            FROM products p
            JOIN product_names pn ON pn.id = p.product_name_id
            JOIN suppliers      s  ON s.id  = p.supplier_id
            JOIN manufacturers  m  ON m.id  = p.manufacturer_id
            JOIN categories     c  ON c.id  = p.category_id
            ORDER BY p.id
        """;
        List<Product> list = new ArrayList<>();
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("id"),
                        rs.getString("articul"),
                        rs.getString("product_name"),
                        rs.getString("measure_item"),
                        rs.getDouble("price"),
                        rs.getString("supplier_name"),
                        rs.getString("manufacturer_name"),
                        rs.getString("category_name"),
                        rs.getInt("sale"),
                        rs.getInt("quantity"),
                        rs.getString("description"),
                        rs.getString("photo_path")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<String> findAllProductNames()      { return dict.getAllNames("product_names"); }
    public List<String> findAllCategoryNames()     { return dict.getAllNames("categories"); }
    public List<String> findAllSupplierNames()     { return dict.getAllNames("suppliers"); }
    public List<String> findAllManufacturerNames() { return dict.getAllNames("manufacturers"); }
}
