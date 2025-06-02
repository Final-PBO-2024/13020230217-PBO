package controllers;

import models.Booking;
import models.Schedule;
import repositories.BookingRepository;
import repositories.ScheduleRepository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;

public class BookingController {

    private BookingRepository bookingRepository;
    private ScheduleRepository scheduleRepository;

    public BookingController() {
        this.bookingRepository = new BookingRepository();
        this.scheduleRepository = new ScheduleRepository();
    }

    public List<Booking> getAllBookings(boolean includeDeleted) {
        return bookingRepository.getAllBookings(includeDeleted);
    }

    public List<Booking> getBookingsByUserId(int userId, boolean includeDeleted) {
        return bookingRepository.getBookingsByUserId(userId, includeDeleted);
    }

    // --- START MODIFIED ---
    // Menambahkan metode getBookingById
    public Booking getBookingById(int bookingId) {
        return bookingRepository.getBookingById(bookingId);
    }
    // --- END MODIFIED ---

    public List<Schedule> getAvailableSchedules(int fieldId, Date bookingDate) {
        // 1. Ambil semua jadwal aktif untuk lapangan tsb
        List<Schedule> allSchedules = scheduleRepository.getSchedulesByFieldId(fieldId, false);

        // 2. Ambil semua waktu yang sudah dibooking pada tanggal tsb
        List<Time> bookedTimes = bookingRepository.getBookedTimes(fieldId, bookingDate);

        // 3. Filter jadwal yang tersedia
        return allSchedules.stream()
                .filter(schedule -> !bookedTimes.contains(schedule.getStartTime()))
                .collect(Collectors.toList());
    }

    public boolean createBooking(Booking booking) {
        return bookingRepository.addBooking(booking);
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        return bookingRepository.updateBookingStatus(bookingId, status);
    }

    public boolean cancelBooking(int bookingId) {
        return bookingRepository.updateBookingStatus(bookingId, "Cancelled");
    }

    public boolean softDeleteBooking(int bookingId) {
        return bookingRepository.setBookingDeletedStatus(bookingId, true);
    }
    public boolean restoreBooking(int bookingId) {
        return bookingRepository.setBookingDeletedStatus(bookingId, false);
    }
}
