package views.admin;

import controllers.AuthController;
import views.auth.LoginForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboardForm extends JFrame {
    private JButton btnManageFields;
    private JButton btnManageSchedules;
    private JButton btnManageBookings;
    private JButton btnLogout;

    public AdminDashboardForm() {
        setTitle("Admin Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Changed to DISPOSE_ON_CLOSE
        setLocationRelativeTo(null);
        setLayout(null);

        btnManageFields = new JButton("Manage Fields");
        btnManageFields.setBounds(50, 50, 300, 40);
        add(btnManageFields);

        btnManageSchedules = new JButton("Manage Schedules");
        btnManageSchedules.setBounds(50, 100, 300, 40);
        add(btnManageSchedules);

        btnManageBookings = new JButton("Manage Bookings");
        btnManageBookings.setBounds(50, 150, 300, 40);
        add(btnManageBookings);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(50, 200, 300, 40);
        add(btnLogout);

        btnManageFields.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FieldManagementForm().setVisible(true); // Ensure FieldManagementForm exists
            }
        });

        btnManageSchedules.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ScheduleManagementForm().setVisible(true); // Ensure ScheduleManagementForm exists
            }
        });

        btnManageBookings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BookingManagementForm().setVisible(true); // Ensure BookingManagementForm exists
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AuthController authController = null;
                new LoginForm(authController).setVisible(true);
            }
        });
    }
}