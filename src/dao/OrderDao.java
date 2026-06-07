package dao;

import db.DatabaseConnection;
import model.Order;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    private final ProductDao productDao = new ProductDao();

    public boolean hasProductInOrder(String articul) {
        var orders = findAll();
        for (Order order : orders) {
            if (order.getArticul().contains(articul)) {
                return true;
            }
        }
        return false;
    }

    public void create(Order order) {
        String sql = """
               INSERT INTO orders(articul, order_date, delivery_date, pickup_point, client_fio, pickup_code, order_status)
               VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);
            ps.setString(1, order.getArticul());
            ps.setDate(2, Date.valueOf(order.getOrderDate()));
            ps.setDate(3, Date.valueOf(order.getDeliveryDate()));
            ps.setInt(4, getPickupPointIdByName(order.getPickupPoint()));
            ps.setString(5, order.getClientFio());
            ps.setInt(6, order.getCode());
            ps.setInt(7, getStatusIdByName(order.getStatus()));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Order order) {
        String sql = """
               UPDATE orders SET
                   articul = ?,
                   order_date = ?,
                   delivery_date = ?,
                   pickup_point = ?,
                   client_fio = ?,
                   pickup_code = ?,
                   order_status = ?
               WHERE id = ?
        """;

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);
            ps.setString(1, order.getArticul());
            ps.setDate(2, Date.valueOf(order.getOrderDate()));
            ps.setDate(3, Date.valueOf(order.getDeliveryDate()));
            ps.setInt(4, getPickupPointIdByName(order.getPickupPoint()));
            ps.setString(5, order.getClientFio());
            ps.setInt(6, order.getCode());
            ps.setInt(7, getStatusIdByName(order.getStatus()));
            ps.setInt(8, order.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getPickupPointIdByName(String name) {
        return productDao.getDictionaryIdByName(name, "pickup_points");
    }

    public Integer getStatusIdByName(String name) {
        return productDao.getDictionaryIdByName(name, "order_statuses");
    }

    public List<Order> findAll() {
        List<Order> result = new ArrayList<>();

        String sql = """
            SELECT
             o.id,
             o.articul,
             o.order_date,
             o.delivery_date,
             pp.name as pp_name,
             o.client_fio,
             o.pickup_code,
             os.name as status
            FROM orders o
            JOIN pickup_points pp on o.pickup_point = pp.id
            JOIN order_statuses os on o.order_status = os.id
        """;

        try (var con = DatabaseConnection.getConnection()) {
            var ps = con.prepareStatement(sql);

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Order(
                            rs.getInt("id"),
                            rs.getString("articul"),
                            rs.getDate("order_date").toLocalDate(),
                            rs.getDate("delivery_date").toLocalDate(),
                            rs.getString("pp_name"),
                            rs.getString("client_fio"),
                            rs.getInt("pickup_code"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public List<String> getStatuses() {
        return new ProductDao().getNamesFromDictionary("order_statuses");
    }

    public List<String> getAddresses() {
        return new ProductDao().getNamesFromDictionary("pickup_points");
    }
}
