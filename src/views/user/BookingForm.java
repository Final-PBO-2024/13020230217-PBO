package views.user;

import controllers.BookingController;
import controllers.ScheduleController;
import models.Booking;
import models.Field;
import models.Schedule;
import models.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingForm extends JFrame {
    private JComboBox<Schedule> cbSchedules;
    private JButton btnBook;
    private JButton btnBack;
    private BookingController bookingController;
    private ScheduleController scheduleController;
    private User user;
    private Field field;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public BookingForm(User user, Field field) {
        this.user = user;
        this.field = field;
        bookingController = new BookingController();
        scheduleController = new ScheduleController();
        setTitle("Book Field");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblSelectSchedule = new JLabel("Select Schedule:");
        lblSelectSchedule.setBounds(20, 20, 100, 25);
        add(lblSelectSchedule);

        cbSchedules = new JComboBox<>();
        cbSchedules.setBounds(100, 20, 260, 25);
        add(cbSchedules);

        btnBook = new JButton("Book");
        btnBook.setBounds(20, 60, 100, 25);
        add(btnBook);

        btnBack = new JButton("Back");
        btnBack.setBounds(130, 60, 100, 25);
        add(btnBack);

        refreshScheduleList();

        btnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Schedule selectedSchedule = (Schedule) cbSchedules.getSelectedItem();
                if (selectedSchedule != null) {
                    Booking booking = new Booking();
                    booking.setUserId(user.getUserId());
                    booking.setScheduleId(selectedSchedule.getScheduleId());
                    booking.setBookingDate(LocalDateTime.now());
                    booking.setStatus("pending");
                    bookingController.createBooking(booking);
                    refreshScheduleList();
                }
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void refreshScheduleList() {
        cbSchedules.removeAllItems();
        List<Schedule> schedules = scheduleController.getSchedulesByFieldId(field.getFieldId());
        if (schedules != null) {
            for (Schedule schedule : schedules) {
                cbSchedules.addItem(schedule);
            }
        }
    }

    @Override
    public String toString() {
        return "BookingForm";
    }
}