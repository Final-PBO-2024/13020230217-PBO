package controllers;

import models.Booking;
import repositories.BookingRepository;
import repositories.ScheduleRepository;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import models.Schedule;

public class BookingController {
    private BookingRepository bookingRepository;
    private ScheduleRepository scheduleRepository;

    public BookingController() {
        this.bookingRepository = new BookingRepository();
        this.scheduleRepository = new ScheduleRepository();
    }

    public void createBooking(Booking booking) {
        try {
            bookingRepository.save(booking);
            Schedule schedule = new Schedule();
            schedule.setScheduleId(booking.getScheduleId());
            schedule.setAvailable(false);
            scheduleRepository.update(schedule);
            JOptionPane.showMessageDialog(null, "Booking created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error creating booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelBooking(int bookingId) {
        try {
            bookingRepository.updateStatus(bookingId, "cancelled");
            JOptionPane.showMessageDialog(null, "Booking cancelled successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cancelling booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Booking> getUserBookings(int userId) {
        try {
            return bookingRepository.findByUserId(userId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching bookings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public List<Booking> getAllBookings() {
        try {
            return bookingRepository.findAll();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching bookings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void updateStatus(int bookingId, String confirmed) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}