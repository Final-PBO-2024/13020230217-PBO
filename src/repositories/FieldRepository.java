package repositories;

import config.DatabaseConnection;
import models.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class FieldRepository {

    public List<Field> getAllFields(boolean includeDeleted) {
        List<Field> fields = new ArrayList<>();
        String sql = "SELECT * FROM fields";
        if (!includeDeleted) {
            sql += " WHERE is_deleted = FALSE";
        }
        sql += " ORDER BY field_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fields.add(mapResultSetToField(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data lapangan: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return fields;
    }

    public Field getFieldById(int fieldId) {
        String sql = "SELECT * FROM fields WHERE field_id = ?";
        Field field = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fieldId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                field = mapResultSetToField(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return field;
    }

    public boolean addField(Field field) {
        // --- START MODIFIED ---
        // Menambahkan kolom is_deleted ke query INSERT
        String sql = "INSERT INTO fields (name, type, price_per_hour, is_deleted) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, field.getName());
            pstmt.setString(2, field.getType());
            pstmt.setBigDecimal(3, field.getPricePerHour());
            pstmt.setBoolean(4, field.isDeleted()); // Menggunakan nilai isDeleted dari objek Field
            // --- END MODIFIED ---
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah lapangan: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateField(Field field) {
        // --- START MODIFIED ---
        // Menambahkan kolom is_deleted ke query UPDATE
        String sql = "UPDATE fields SET name = ?, type = ?, price_per_hour = ?, is_deleted = ? WHERE field_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, field.getName());
            pstmt.setString(2, field.getType());
            pstmt.setBigDecimal(3, field.getPricePerHour());
            pstmt.setBoolean(4, field.isDeleted()); // Menggunakan nilai isDeleted dari objek Field
            pstmt.setInt(5, field.getFieldId());
            // --- END MODIFIED ---
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, "Gagal update lapangan: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public boolean setFieldDeletedStatus(int fieldId, boolean isDeleted) {
        String sql = "UPDATE fields SET is_deleted = ? WHERE field_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isDeleted);
            pstmt.setInt(2, fieldId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal update status lapangan: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private Field mapResultSetToField(ResultSet rs) throws SQLException {
        Field field = new Field();
        field.setFieldId(rs.getInt("field_id"));
        field.setName(rs.getString("name"));
        field.setType(rs.getString("type"));
        field.setPricePerHour(rs.getBigDecimal("price_per_hour"));
        field.setDeleted(rs.getBoolean("is_deleted"));
        return field;
    }
}
