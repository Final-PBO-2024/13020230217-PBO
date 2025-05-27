package repositories;

import config.DatabaseConnection;
import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UserRepository {

//    public User getUserByUsernameAndPassword(String username, String password) {
//        // Cek admin hardcoded dulu
//        if ("admin".equals(username) && "admin123".equals(password)) {
//            User admin = new User();
//            admin.setUserId(0); // ID khusus untuk admin
//            admin.setUsername("admin");
//            admin.setName("Administrator");
//            admin.setRole("Admin");
//            admin.setDeleted(false);
//            return admin;
//        }
//
//        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_deleted = FALSE";
//        User user = null;
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, username);
//            pstmt.setString(2, password); // TODO: Hash password di dunia nyata!
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                user = mapResultSetToUser(rs);
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(null, "Error saat login: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//        return user;
//    }

   public boolean isUsernameOrEmailExists(String username, String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE (username = ? OR email = ?) AND is_deleted = FALSE";
        try (Connection conn = DatabaseConnection.getConnection(); // Koneksi baru
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return true; // Anggap ada jika koneksi gagal (pencegahan)

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (name, username, password, email, phone, role) VALUES (?, ?, ?, ?, ?, ?)";
        // Gunakan try-with-resources. Ini akan meminta koneksi BARU
        // dan akan MENUTUPNYA secara otomatis, TAPI karena koneksi
        // yang didapat selalu baru, ini tidak akan memengaruhi operasi lain.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // PENTING: Cek apakah koneksi berhasil didapat
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Tidak bisa mendapatkan koneksi database.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword()); // TODO: Hash!
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getRole());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // Tangani error dengan lebih spesifik jika bisa
            JOptionPane.showMessageDialog(null, "Gagal menambah user: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

     public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setDeleted(rs.getBoolean("is_deleted"));
        return user;
    }

     public User getUserByUsernameAndPassword(String username, String password) {
        // Cek admin hardcoded dulu
        if ("admin".equals(username) && "admin123".equals(password)) {
            // ... (kode admin tetap sama) ...
            User admin = new User();
            admin.setUserId(0);
            admin.setUsername("admin");
            admin.setName("Administrator");
            admin.setRole("Admin");
            admin.setDeleted(false);
            return admin;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_deleted = FALSE";
        User user = null;
        try (Connection conn = DatabaseConnection.getConnection(); // Koneksi baru
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return null; // Cek koneksi

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = mapResultSetToUser(rs); // Pastikan ada metode ini
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error saat login: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return user;
    }

}