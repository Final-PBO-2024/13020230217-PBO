package models;

import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private int userId;
    private int scheduleId;
    private LocalDateTime bookingDate;
    private String status;

    public Booking() {}

    public Booking(int userId, int scheduleId, LocalDateTime bookingDate, String status) {
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}