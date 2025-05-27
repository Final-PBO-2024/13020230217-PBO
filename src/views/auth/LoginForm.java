package views.auth;

import controllers.AuthController;
import models.User;
import views.admin.AdminDashboardForm;
import views.user.UserDashboardForm;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private AuthController authController;

    public LoginForm() {
        authController = new AuthController();
        initComponents();
        setTitle("Login - Sport Booking System");
        setSize(400, 280); // Sedikit lebih tinggi untuk judul
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255)); // Alice Blue - contoh warna
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Lebih banyak spasi
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("SPORT BOOKING SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Font lebih modern
        titleLabel.setForeground(new Color(0, 102, 204)); // Biru
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(titleLabel, gbc);

        // Spacer
        gbc.gridy = 1;
        panel.add(new JLabel(" "), gbc);


        gbc.gridwidth = 1; // Reset gridwidth
        gbc.weightx = 0;
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 153, 51)); // Hijau
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Buat tombol full width
        gbc.ipady = 10; // Buat tombol lebih tinggi
        panel.add(loginButton, gbc);

        registerButton = new JButton("Belum punya akun? Register");
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(Color.BLUE.darker());
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        gbc.ipady = 0; // Reset
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        add(panel);

        // Action Listeners
        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> {
            RegisterForm registerForm = new RegisterForm(this);
            registerForm.setVisible(true);
            this.setVisible(false);
        });

        // Login on Enter
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!", "Input Kosong", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authController.login(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat Datang, " + user.getName() + "!", "Login Sukses", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();

            if ("Admin".equals(user.getRole())) {
                AdminDashboardForm adminDashboard = new AdminDashboardForm(user);
                adminDashboard.setVisible(true);
            } else {
                UserDashboardForm userDashboard = new UserDashboardForm(user);
                userDashboard.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}