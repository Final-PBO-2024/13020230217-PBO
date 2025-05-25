package repositories;

import config.DatabaseConnection;
import models.Booking;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    public void save(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (user_id, schedule_id, booking_date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getScheduleId());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getBookingDate()));
            stmt.setString(4, booking.getStatus());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                booking.setBookingId(rs.getInt(1));
            }
        }
    }

    public void updateStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();
        }
    }

    public List<Booking> findByUserId(int userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setScheduleId(rs.getInt("schedule_id"));
                booking.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
                booking.setStatus(rs.getString("status"));
                bookings.add(booking);
            }
        }
        return bookings;
    }

    public List<Booking> findAll() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setScheduleId(rs.getInt("schedule_id"));
                booking.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
                booking.setStatus(rs.getString("status"));
                bookings.add(booking);
            }
        }
        return bookings;
    }
}