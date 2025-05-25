package views.admin;

import controllers.BookingController;
import models.Booking;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BookingManagementForm extends JFrame {
    private JComboBox<Booking> cbBookings;
    private JButton btnConfirm;
    private JButton btnCancel;
    private JButton btnBack;
    private BookingController bookingController;

    public BookingManagementForm() {
        bookingController = new BookingController();
        setTitle("Booking Management");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblSelectBooking = new JLabel("Select Booking:");
        lblSelectBooking.setBounds(20, 20, 100, 25);
        add(lblSelectBooking);

        cbBookings = new JComboBox<>();
        cbBookings.setBounds(100, 20, 260, 25);
        add(cbBookings);

        btnConfirm = new JButton("Confirm");
        btnConfirm.setBounds(20, 60, 100, 25);
        add(btnConfirm);

        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(130, 60, 100, 25);
        add(btnCancel);

        btnBack = new JButton("Back");
        btnBack.setBounds(240, 60, 100, 25);
        add(btnBack);

        refreshBookingList();

        btnConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Booking selectedBooking = (Booking) cbBookings.getSelectedItem();
                if (selectedBooking != null) {
                    bookingController.updateStatus(selectedBooking.getBookingId(), "confirmed");
                    refreshBookingList();
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Booking selectedBooking = (Booking) cbBookings.getSelectedItem();
                if (selectedBooking != null) {
                    bookingController.cancelBooking(selectedBooking.getBookingId());
                    refreshBookingList();
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

    private void refreshBookingList() {
        cbBookings.removeAllItems();
        List<Booking> bookings = bookingController.getAllBookings();
        if (bookings != null) {
            for (Booking booking : bookings) {
                cbBookings.addItem(booking);
            }
        }
    }

    @Override
    public String toString() {
        return "BookingManagementForm";
    }
}