package views.user;

import models.User;
import views.auth.LoginForm;

import javax.swing.*;
import java.awt.*;

public class UserDashboardForm extends JFrame {

    private User currentUser;

    public UserDashboardForm(User user) {
        this.currentUser = user;
        initComponents();
        setTitle("User Dashboard - " + currentUser.getName());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(230, 255, 230)); // Warna hijau muda

        // Header
        JLabel headerLabel = new JLabel("SPORT BOOKING SYSTEM", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(0, 100, 0)); // Hijau tua
        JLabel welcomeLabel = new JLabel("Halo, " + currentUser.getName() + "! Siap berolahraga?", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.setBackground(new Color(230, 255, 230));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Menu Panel
        JPanel menuPanel = new JPanel(new GridLayout(1, 2, 50, 0)); // 1 row, 2 cols
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        menuPanel.setBackground(new Color(230, 255, 230));

        JButton bookingButton = createMenuButton("Booking Lapangan Baru", "icons/new_booking.png");
        JButton historyButton = createMenuButton("Riwayat Pemesanan", "icons/history.png");

        menuPanel.add(bookingButton);
        menuPanel.add(historyButton);
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        // Footer (Logout Button)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(230, 255, 230));
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69)); // Merah
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footerPanel.add(logoutButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Listeners
        bookingButton.addActionListener(e -> openForm(new FieldListForm(this, currentUser)));
        historyButton.addActionListener(e -> openForm(new BookingHistoryForm(this, currentUser)));

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
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(220, 150));
        button.setBackground(new Color(144, 238, 144)); // Light green
        button.setForeground(new Color(0, 100, 0)); // Dark green
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        try {
           ImageIcon icon = new ImageIcon(new ImageIcon(getClass().getResource(iconPath)).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
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