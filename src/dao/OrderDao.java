package dao;

import db.DatabaseConnection;
import model.Order;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    private final DictionaryDao dict = new DictionaryDao();

    public boolean hasProductInOrder(String articul) {
        String sql = "SELECT COUNT(*) FROM orders WHERE articul = ?";
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, articul);
            try (var rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void create(Order order) {
        String sql = """
            INSERT INTO orders (articul, order_date, delivery_date, pickup_point, client_fio, pickup_code, order_status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, order.getArticul());
            ps.setDate(2, Date.valueOf(order.getOrderDate()));
            ps.setDate(3, Date.valueOf(order.getDeliveryDate()));
            ps.setInt(4, dict.getIdByName(order.getPickupPoint(), "pickup_points"));
            ps.setString(5, order.getClientFio());
            ps.setInt(6, order.getCode());
            ps.setInt(7, dict.getIdByName(order.getStatus(), "order_statuses"));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Order order) {
        String sql = """
            UPDATE orders SET
                articul      = ?,
                order_date   = ?,
                delivery_date = ?,
                pickup_point = ?,
                client_fio   = ?,
                pickup_code  = ?,
                order_status = ?
            WHERE id = ?
        """;
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, order.getArticul());
            ps.setDate(2, Date.valueOf(order.getOrderDate()));
            ps.setDate(3, Date.valueOf(order.getDeliveryDate()));
            ps.setInt(4, dict.getIdByName(order.getPickupPoint(), "pickup_points"));
            ps.setString(5, order.getClientFio());
            ps.setInt(6, order.getCode());
            ps.setInt(7, dict.getIdByName(order.getStatus(), "order_statuses"));
            ps.setInt(8, order.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Order> findAll() {
        String sql = """
            SELECT
                o.id,
                o.articul,
                o.order_date,
                o.delivery_date,
                pp.name AS pp_name,
                o.client_fio,
                o.pickup_code,
                os.name AS status
            FROM orders o
            JOIN pickup_points  pp ON pp.id = o.pickup_point
            JOIN order_statuses os ON os.id = o.order_status
            ORDER BY o.id
        """;
        List<Order> list = new ArrayList<>();
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Order(
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<String> getStatuses()  { return dict.getAllNames("order_statuses"); }
    public List<String> getAddresses() { return dict.getAllNames("pickup_points"); }
}
