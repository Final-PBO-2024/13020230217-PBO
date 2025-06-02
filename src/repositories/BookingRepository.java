package repositories;

import config.DatabaseConnection;
import models.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class BookingRepository {

    public List<Booking> getAllBookings(boolean includeDeleted) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.name as user_name, f.name as field_name " +
                     "FROM bookings b " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "JOIN fields f ON b.field_id = f.field_id";
        if (!includeDeleted) {
            sql += " WHERE b.is_deleted = FALSE";
        }
        sql += " ORDER BY b.booking_date DESC, b.start_time";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getBookingsByUserId(int userId, boolean includeDeleted) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.name as user_name, f.name as field_name " +
                     "FROM bookings b " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "JOIN fields f ON b.field_id = f.field_id " +
                     "WHERE b.user_id = ?";
        if (!includeDeleted) {
            sql += " AND b.is_deleted = FALSE";
        }
        sql += " ORDER BY b.booking_date DESC, b.start_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil riwayat booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return bookings;
    }

    // --- START MODIFIED ---
    // Menambahkan metode getBookingById
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT b.*, u.name as user_name, f.name as field_name " +
                     "FROM bookings b " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "JOIN fields f ON b.field_id = f.field_id " +
                     "WHERE b.booking_id = ?";
        Booking booking = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                booking = mapResultSetToBooking(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booking;
    }
    // --- END MODIFIED ---

    public List<Time> getBookedTimes(int fieldId, Date bookingDate) {
        List<Time> bookedTimes = new ArrayList<>();
        String sql = "SELECT start_time FROM bookings " +
                     "WHERE field_id = ? AND booking_date = ? " +
                     "AND status IN ('Pending', 'Confirmed') AND is_deleted = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, fieldId);
            pstmt.setDate(2, bookingDate);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookedTimes.add(rs.getTime("start_time"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil waktu terbooking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return bookedTimes;
    }

    public boolean addBooking(Booking booking) {
        // --- START MODIFIED ---
        // Menambahkan kolom is_deleted ke query INSERT
        String sql = "INSERT INTO bookings (user_id, field_id, booking_date, start_time, end_time, total_price, status, is_deleted) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, booking.getUserId());
            pstmt.setInt(2, booking.getFieldId());
            pstmt.setDate(3, booking.getBookingDate());
            pstmt.setTime(4, booking.getStartTime());
            pstmt.setTime(5, booking.getEndTime());
            pstmt.setBigDecimal(6, booking.getTotalPrice());
            pstmt.setString(7, booking.getStatus());
            pstmt.setBoolean(8, booking.isDeleted()); // Menggunakan nilai isDeleted dari objek Booking
            // --- END MODIFIED ---
            return pstmt.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "Jadwal yang dipilih sudah dibooking orang lain.", "Booking Gagal", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, "Gagal update status booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

     public boolean setBookingDeletedStatus(int bookingId, boolean isDeleted) {
        String sql = "UPDATE bookings SET is_deleted = ? WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isDeleted);
            pstmt.setInt(2, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal update status hapus booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setFieldId(rs.getInt("field_id"));
        booking.setBookingDate(rs.getDate("booking_date"));
        booking.setStartTime(rs.getTime("start_time"));
        booking.setEndTime(rs.getTime("end_time"));
        booking.setTotalPrice(rs.getBigDecimal("total_price"));
        booking.setStatus(rs.getString("status"));
        booking.setDeleted(rs.getBoolean("is_deleted"));
        booking.setUserName(rs.getString("user_name"));
        booking.setFieldName(rs.getString("field_name"));
        return booking;
    }
}
