package views.user;

import controllers.BookingController;
import models.Booking;
import models.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BookingHistoryForm extends JFrame {
    private JComboBox<Booking> cbBookings;
    private JButton btnCancel;
    private JButton btnBack;
    private BookingController bookingController;
    private User user;

    public BookingHistoryForm(User user) {
        this.user = user;
        bookingController = new BookingController();
        setTitle("Booking History");
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

        btnCancel = new JButton("Cancel Booking");
        btnCancel.setBounds(20, 60, 120, 25);
        add(btnCancel);

        btnBack = new JButton("Back");
        btnBack.setBounds(150, 60, 100, 25);
        add(btnBack);

        refreshBookingList();

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Booking selectedBooking = (Booking) cbBookings.getSelectedItem();
                if (selectedBooking != null && selectedBooking.getStatus().equals("pending")) {
                    bookingController.cancelBooking(selectedBooking.getBookingId());
                    refreshBookingList();
                } else {
                    JOptionPane.showMessageDialog(null, "Cannot cancel this booking", "Error", JOptionPane.ERROR_MESSAGE);
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
        List<Booking> bookings = bookingController.getUserBookings(user.getUserId());
        if (bookings != null) {
            for (Booking booking : bookings) {
                cbBookings.addItem(booking);
            }
        }
    }

    @Override
    public String toString() {
        return "BookingHistoryForm";
    }
}