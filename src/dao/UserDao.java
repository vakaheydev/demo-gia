package dao;

import db.DatabaseConnection;
import model.User;

import java.sql.SQLException;

public class UserDao {
    public String getRoleById(int id) {
        String sql = "SELECT * FROM roles WHERE id = ?";

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

    public User getUserByLoginAndPassword(String login, String password) {
        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";

         try (var con = DatabaseConnection.getConnection()) {
             var ps = con.prepareStatement(sql);
             ps.setString(1, login);
             ps.setString(2, password);

             try (var rs = ps.executeQuery()) {
                 if (rs.next()) {
                     int roleId = rs.getInt("role_id");
                     String role = getRoleById(roleId);

                     return new User(
                             rs.getInt("id"),
                             rs.getString("login"),
                             rs.getString("password"),
                             rs.getString("fio"),
                             role
                     );
                 }
             }
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }

         return null;
    }
}
