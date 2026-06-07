package dao;

import db.DatabaseConnection;
import model.Product;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ProductDao {
    public void create(Product product) {
        String sql = """
            INSERT INTO products (articul, product_name_id, measure_item, price, supplier_id, manufacturer_id, category_id, sale, quantity, description, photo_path)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);
            ps.setString(1, product.getArticul());
            ps.setInt(2, getProductNameIdByName(product.getProductName()));
            ps.setString(3, product.getMeasureItem());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, getSupplierIdByName(product.getSupplier()));
            ps.setInt(6, getManufacturerIdByName(product.getManufacturer()));
            ps.setInt(7, getCategoryIdByName(product.getCategory()));
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

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);
            ps.setString(1, product.getArticul());
            ps.setInt(2, getProductNameIdByName(product.getProductName()));
            ps.setString(3, product.getMeasureItem());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, getSupplierIdByName(product.getSupplier()));
            ps.setInt(6, getManufacturerIdByName(product.getManufacturer()));
            ps.setInt(7, getCategoryIdByName(product.getCategory()));
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

    public List<Product> findAll() {
        String sql = """
            SELECT
             p.id,
             p.articul,
             pn.name as product_name,
             p.measure_item,
             p.price,
             s.name as supplier_name,
             m.name as manufacturer_name,
             c.name as category_name,
             p.sale,
             p.quantity,
             p.description,
             p.photo_path
            FROM products p
            JOIN product_names pn on pn.id = p.product_name_id
            JOIN suppliers s on s.id = p.supplier_id
            JOIN manufacturers m on m.id = p.manufacturer_id
            JOIN categories c on c.id = p.category_id
            ORDER BY p.id
        """;

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);

            try (var rs = ps.executeQuery()) {
                List<Product> list = new ArrayList<>();

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

                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getManufacturerById(int id) {
        return getDictionaryNameById(id, "manufacturers");
    }

    public String getSupplierById(int id) {
        return getDictionaryNameById(id, "suppliers");
    }

    public String getProductNameById(int id) {
        return getDictionaryNameById(id, "product_names");
    }

    public String getCategoryById(int id) {
        return getDictionaryNameById(id, "categories");
    }

    public Integer getCategoryIdByName(String name) {
        return getDictionaryIdByName(name, "categories");
    }

    public Integer getProductNameIdByName(String name) {
        return getDictionaryIdByName(name, "product_names");
    }

    public Integer getManufacturerIdByName(String name) {
        return getDictionaryIdByName(name, "manufacturers");
    }

    public Integer getSupplierIdByName(String name) {
        return getDictionaryIdByName(name, "suppliers");
    }

    public Integer getDictionaryIdByName(String name, String tableName) {
        String sql = "SELECT * FROM " + tableName + " WHERE name = ?";

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);
            ps.setString(1, name);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public String getDictionaryNameById(int id, String tableName) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<String> findAllSupplierNames() {
        return getNamesFromDictionary("suppliers");
    }

    public List<String> findAllCategoryNames() {
        return getNamesFromDictionary("categories");
    }

    public List<String> findAllManufacturerNames() {
        return getNamesFromDictionary("manufacturers");
    }

    public List<String> findAllProductNames() {
        return getNamesFromDictionary("product_names");
    }

    public List<String> getNamesFromDictionary(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        List<String> list = new ArrayList<>();

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
