package dao;

import db.DatabaseConnection;
import model.User;

import java.sql.SQLException;

public class UserDao {

    // Один JOIN-запрос вместо двух отдельных соединений
    public User getUserByLoginAndPassword(String login, String password) {
        String sql = """
            SELECT u.id, u.login, u.password, u.fio, r.name AS role
            FROM users u
            JOIN roles r ON r.id = u.role_id
            WHERE u.login = ? AND u.password = ?
        """;
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, password);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("fio"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
