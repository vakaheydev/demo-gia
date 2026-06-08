package dao;

import db.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Общие операции для справочных таблиц (id <-> name).
// Используется всеми DAO, которым нужны словари.
public class DictionaryDao {

    public Integer getIdByName(String name, String tableName) {
        String sql = "SELECT id FROM " + tableName + " WHERE name = ?";
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<String> getAllNames(String tableName) {
        String sql = "SELECT name FROM " + tableName + " ORDER BY name";
        List<String> list = new ArrayList<>();
        try (var con = DatabaseConnection.getConnection();
             var ps = con.prepareStatement(sql)) {
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
