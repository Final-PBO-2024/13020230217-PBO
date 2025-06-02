package repositories;

import config.DatabaseConnection;
import models.Schedule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ScheduleRepository {

    public List<Schedule> getAllSchedules(boolean includeDeleted) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT s.*, f.name as field_name FROM schedules s " +
                     "JOIN fields f ON s.field_id = f.field_id";
        if (!includeDeleted) {
            sql += " WHERE s.is_deleted = FALSE";
        }
        sql += " ORDER BY f.name, s.start_time";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data jadwal: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return schedules;
    }

    public List<Schedule> getSchedulesByFieldId(int fieldId, boolean includeDeleted) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT s.*, f.name as field_name FROM schedules s " +
                     "JOIN fields f ON s.field_id = f.field_id " +
                     "WHERE s.field_id = ?";
        if (!includeDeleted) {
            sql += " AND s.is_deleted = FALSE";
        }
        sql += " ORDER BY s.start_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, fieldId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, "Gagal mengambil data jadwal by Field: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return schedules;
    }

    public Schedule getScheduleById(int scheduleId) {
        String sql = "SELECT s.*, f.name as field_name FROM schedules s " +
                     "JOIN fields f ON s.field_id = f.field_id " +
                     "WHERE s.schedule_id = ?";
        Schedule schedule = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scheduleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                schedule = mapResultSetToSchedule(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedule;
    }

    public boolean addSchedule(Schedule schedule) {
        String sql = "INSERT INTO schedules (field_id, start_time, end_time, day_of_week, is_deleted) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, schedule.getFieldId());
            pstmt.setTime(2, schedule.getStartTime());
            pstmt.setTime(3, schedule.getEndTime());
            pstmt.setString(4, schedule.getDayOfWeek());
            pstmt.setBoolean(5, schedule.isDeleted());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah jadwal: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSchedule(Schedule schedule) {
        String sql = "UPDATE schedules SET field_id = ?, start_time = ?, end_time = ?, day_of_week = ?, is_deleted = ? WHERE schedule_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, schedule.getFieldId());
            pstmt.setTime(2, schedule.getStartTime());
            pstmt.setTime(3, schedule.getEndTime());
            pstmt.setString(4, schedule.getDayOfWeek());
            pstmt.setBoolean(5, schedule.isDeleted());
            pstmt.setInt(6, schedule.getScheduleId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal update jadwal: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public boolean setScheduleDeletedStatus(int scheduleId, boolean isDeleted) {
        String sql = "UPDATE schedules SET is_deleted = ? WHERE schedule_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isDeleted);
            pstmt.setInt(2, scheduleId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal update status jadwal: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private Schedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(rs.getInt("schedule_id"));
        schedule.setFieldId(rs.getInt("field_id"));
        schedule.setStartTime(rs.getTime("start_time"));
        schedule.setEndTime(rs.getTime("end_time"));
        schedule.setDayOfWeek(rs.getString("day_of_week"));
        schedule.setDeleted(rs.getBoolean("is_deleted"));
        
        // --- START MODIFIED ---
        // Coba ambil field_name dengan lebih hati-hati
        String fieldName = null;
        try {
            // Coba ambil berdasarkan alias "field_name"
            fieldName = rs.getString("field_name");
            // Jika null, coba ambil berdasarkan nama kolom asli "name" dari tabel fields
            if (fieldName == null || fieldName.trim().isEmpty()) {
                // Periksa apakah kolom 'name' dari tabel fields ada di ResultSet (jika tidak pakai alias)
                if (hasColumn(rs, "name")) { // Cek kolom 'name'
                    fieldName = rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil field_name atau name dari ResultSet: " + e.getMessage());
        }
        
        if (fieldName != null && !fieldName.trim().isEmpty()) {
            schedule.setFieldName(fieldName);
        } else {
            // Fallback jika nama lapangan tidak ditemukan atau kosong
            schedule.setFieldName("Nama Lapangan Tidak Tersedia");
            System.err.println("Warning: field_name untuk schedule ID " + schedule.getScheduleId() + " kosong atau null.");
        }
        // --- END MODIFIED ---
        
        return schedule;
    }

    // Helper untuk cek kolom ada atau tidak (menghindari error saat join)
    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            // Menggunakan equalsIgnoreCase untuk perbandingan case-insensitive
            // dan juga cek getColumnLabel untuk kompatibilitas alias
            if (columnName.equalsIgnoreCase(rsmd.getColumnName(x)) || 
                columnName.equalsIgnoreCase(rsmd.getColumnLabel(x))) {
                return true;
            }
        }
        return false;
    }
}
