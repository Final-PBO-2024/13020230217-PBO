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
        setTitle("Login - Sistem Pemesanan Lapangan");
        
        // Ukuran awal yang lebih pas, tidak langsung layar penuh
        setSize(700, 500); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); // Memungkinkan jendela untuk diubah ukurannya
        
        // Tidak langsung setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // Padding disesuaikan
        panel.setBackground(new Color(240, 248, 255)); // Alice Blue - warna latar
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spasi antar komponen
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Beri bobot agar komponen di kolom ini melebar

        JLabel titleLabel = new JLabel("SISTEM PEMESANAN LAPANGAN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36)); // Ukuran font disesuaikan
        titleLabel.setForeground(new Color(0, 51, 102)); // Biru gelap
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Membentang 2 kolom
        panel.add(titleLabel, gbc);

        // Spacer
        gbc.gridy = 1;
        panel.add(Box.createRigidArea(new Dimension(0, 20)), gbc); // Tambah spasi vertikal

        gbc.gridwidth = 1; // Reset gridwidth ke 1
        gbc.weightx = 0; // Label tidak perlu melebar
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Ukuran font disesuaikan
        panel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.weightx = 1.0; // TextField melebar
        panel.add(usernameField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0; // Label tidak perlu melebar
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.weightx = 1.0; // TextField melebar
        panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Ukuran font disesuaikan
        loginButton.setBackground(new Color(40, 167, 69)); // Hijau gelap
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Membentang 2 kolom
        gbc.ipady = 15; // Padding internal vertikal
        panel.add(loginButton, gbc);

        // Tombol Register sebagai link
        registerButton = new JButton("<html><a href=\"#\" style='color:#007BFF;'>Belum punya akun? Daftar di sini.</a></html>"); // Warna link di HTML
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Ukuran font disesuaikan
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        // registerButton.setForeground(new Color(0, 123, 255)); // Dihapus karena warna diatur di HTML
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        gbc.ipady = 0; // Reset padding internal
        gbc.fill = GridBagConstraints.NONE; // Tidak perlu melebar
        gbc.anchor = GridBagConstraints.CENTER; // Pusatkan
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
                adminDashboard.setExtendedState(JFrame.MAXIMIZED_BOTH); // Dashboard Admin layar penuh
                adminDashboard.setVisible(true);
            } else {
                UserDashboardForm userDashboard = new UserDashboardForm(user);
                userDashboard.setExtendedState(JFrame.MAXIMIZED_BOTH); // Dashboard User layar penuh
                userDashboard.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}