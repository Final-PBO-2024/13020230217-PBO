package views.user;

import models.User;
import views.auth.LoginForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserDashboardForm extends JFrame {
    private JButton btnViewFields;
    private JButton btnViewBookings;
    private JButton btnLogout;
    private User user;

    public UserDashboardForm(User user) {
        this.user = user;
        setTitle("User Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Changed to DISPOSE_ON_CLOSE
        setLocationRelativeTo(null);
        setLayout(null);

        btnViewFields = new JButton("View Fields");
        btnViewFields.setBounds(50, 50, 300, 40);
        add(btnViewFields);

        btnViewBookings = new JButton("View Bookings");
        btnViewBookings.setBounds(50, 100, 300, 40);
        add(btnViewBookings);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(50, 150, 300, 40);
        add(btnLogout);

        btnViewFields.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FieldListForm(user).setVisible(true); // Ensure FieldListForm exists
            }
        });

        btnViewBookings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BookingHistoryForm(user).setVisible(true); // Ensure BookingHistoryForm exists
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginForm().setVisible(true); // Consider passing authController if needed
            }
        });
    }
}