package models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * Model untuk merepresentasikan data Booking.
 */
public class Booking {
    private int bookingId;
    private int userId;
    private int fieldId;
    private Date bookingDate;
    private Time startTime;
    private Time endTime;
    private BigDecimal totalPrice;
    private String status;
    private boolean isDeleted;

    // Untuk tampilan di tabel
    private String userName;
    private String fieldName;

    public Booking() {}

    // --- Getters and Setters ---
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getFieldId() { return fieldId; }
    public void setFieldId(int fieldId) { this.fieldId = fieldId; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    @Override
    public String toString() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return "ID: " + bookingId + ", User: " + userName + ", Lapangan: " + fieldName + ", Tgl: " + dateFormat.format(bookingDate) + ", Jam: " + timeFormat.format(startTime) + "-" + timeFormat.format(endTime) + ", Status: " + status;
    }
}