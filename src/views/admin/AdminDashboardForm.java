package views.admin;

import models.User;
import views.auth.LoginForm;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardForm extends JFrame {

    private User adminUser;

    public AdminDashboardForm(User user) {
        this.adminUser = user;
        initComponents();
        setTitle("Admin Dashboard - " + adminUser.getName());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(50, 50, 150));
        JLabel welcomeLabel = new JLabel("Welcome, " + adminUser.getName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.setBackground(Color.WHITE);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Menu Panel
        JPanel menuPanel = new JPanel(new GridLayout(1, 3, 30, 0)); // 1 row, 3 cols
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        menuPanel.setBackground(Color.WHITE);

        JButton fieldButton = createMenuButton("Manajemen Lapangan", "icons/field.png"); // Ganti dg path icon
        JButton scheduleButton = createMenuButton("Manajemen Jadwal", "icons/schedule.png");
        JButton bookingButton = createMenuButton("Manajemen Pemesanan", "icons/booking.png");

        menuPanel.add(fieldButton);
        menuPanel.add(scheduleButton);
        menuPanel.add(bookingButton);
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        // Footer (Logout Button)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69)); // Merah
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footerPanel.add(logoutButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Listeners
        fieldButton.addActionListener(e -> openForm(new FieldManagementForm(this)));
        scheduleButton.addActionListener(e -> openForm(new ScheduleManagementForm(this)));
        bookingButton.addActionListener(e -> openForm(new BookingManagementForm(this)));

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Anda yakin ingin logout?", "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginForm().setVisible(true);
            }
        });
    }

    private JButton createMenuButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(180, 120));
        button.setBackground(new Color(230, 240, 255));
        button.setForeground(new Color(0, 51, 102));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Coba load icon, jika gagal tidak apa-apa
        try {
           ImageIcon icon = new ImageIcon(new ImageIcon(getClass().getResource(iconPath)).getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
           button.setIcon(icon);
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }
        return button;
    }

    private void openForm(JFrame form) {
        form.setVisible(true);
        this.setVisible(false);
    }
}