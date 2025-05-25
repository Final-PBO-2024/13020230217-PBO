package repositories;

import config.DatabaseConnection;
import models.Field;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FieldRepository {
    public void save(Field field) throws SQLException {
        String sql = "INSERT INTO fields (name, sport_type, capacity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, field.getName());
            stmt.setString(2, field.getSportType());
            stmt.setInt(3, field.getCapacity());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                field.setFieldId(rs.getInt(1));
            }
        }
    }

    public void update(Field field) throws SQLException {
        String sql = "UPDATE fields SET name = ?, sport_type = ?, capacity = ? WHERE field_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, field.getName());
            stmt.setString(2, field.getSportType());
            stmt.setInt(3, field.getCapacity());
            stmt.setInt(4, field.getFieldId());
            stmt.executeUpdate();
        }
    }

    public void delete(int fieldId) throws SQLException {
        String sql = "UPDATE fields SET is_deleted = TRUE WHERE field_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fieldId);
            stmt.executeUpdate();
        }
    }

    public List<Field> findAll() throws SQLException {
        List<Field> fields = new ArrayList<>();
        String sql = "SELECT * FROM fields WHERE is_deleted = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Field field = new Field();
                field.setFieldId(rs.getInt("field_id"));
                field.setName(rs.getString("name"));
                field.setSportType(rs.getString("sport_type"));
                field.setCapacity(rs.getInt("capacity"));
                field.setDeleted(rs.getBoolean("is_deleted"));
                fields.add(field);
            }
        }
        return fields;
    }

    public Field findById(int fieldId) throws SQLException {
        String sql = "SELECT * FROM fields WHERE field_id = ? AND is_deleted = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fieldId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Field field = new Field();
                field.setFieldId(rs.getInt("field_id"));
                field.setName(rs.getString("name"));
                field.setSportType(rs.getString("sport_type"));
                field.setCapacity(rs.getInt("capacity"));
                field.setDeleted(rs.getBoolean("is_deleted"));
                return field;
            }
            return null;
        }
    }
}