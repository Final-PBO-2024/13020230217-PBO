package repositories;

import config.DatabaseConnection;
import models.Schedule;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleRepository {
    public void save(Schedule schedule) throws SQLException {
        String sql = "INSERT INTO schedules (field_id, start_time, end_time, is_available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, schedule.getFieldId());
            stmt.setTimestamp(2, Timestamp.valueOf(schedule.getStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(schedule.getEndTime()));
            stmt.setBoolean(4, schedule.isAvailable());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                schedule.setScheduleId(rs.getInt(1));
            }
        }
    }

    public void update(Schedule schedule) throws SQLException {
        String sql = "UPDATE schedules SET field_id = ?, start_time = ?, end_time = ?, is_available = ? WHERE schedule_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, schedule.getFieldId());
            stmt.setTimestamp(2, Timestamp.valueOf(schedule.getStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(schedule.getEndTime()));
            stmt.setBoolean(4, schedule.isAvailable());
            stmt.setInt(5, schedule.getScheduleId());
            stmt.executeUpdate();
        }
    }

    public List<Schedule> findByFieldId(int fieldId) throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM schedules WHERE field_id = ? AND is_available = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fieldId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Schedule schedule = new Schedule();
                schedule.setScheduleId(rs.getInt("schedule_id"));
                schedule.setFieldId(rs.getInt("field_id"));
                schedule.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                schedule.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                schedule.setAvailable(rs.getBoolean("is_available"));
                schedules.add(schedule);
            }
        }
        return schedules;
    }
}