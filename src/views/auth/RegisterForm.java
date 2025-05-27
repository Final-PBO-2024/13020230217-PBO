package views.auth;

import controllers.AuthController;

import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JFrame {

    private LoginForm loginForm; // Untuk kembali ke login form
    private JTextField nameField, usernameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, backButton;
    private AuthController authController;

    public RegisterForm(LoginForm loginForm) {
        this.loginForm = loginForm;
        this.authController = new AuthController();
        initComponents();
        setTitle("Registrasi User Baru");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Agar tidak exit aplikasi
        setLocationRelativeTo(null);
        setResizable(false);

        // Menangani penutupan window
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                loginForm.setVisible(true); // Tampilkan lagi login form
            }
        });
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Form Registrasi", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset
        gbc.gridy = 1; gbc.gridx = 0; panel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; nameField = new JTextField(20); panel.add(nameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; usernameField = new JTextField(20); panel.add(usernameField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; passwordField = new JPasswordField(20); panel.add(passwordField, gbc);

        gbc.gridy = 4; gbc.gridx = 0; panel.add(new JLabel("Konfirmasi Password:"), gbc);
        gbc.gridx = 1; confirmPasswordField = new JPasswordField(20); panel.add(confirmPasswordField, gbc);

        gbc.gridy = 5; gbc.gridx = 0; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; emailField = new JTextField(20); panel.add(emailField, gbc);

        gbc.gridy = 6; gbc.gridx = 0; panel.add(new JLabel("No. Telepon:"), gbc);
        gbc.gridx = 1; phoneField = new JTextField(20); panel.add(phoneField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        registerButton = new JButton("Register");
        backButton = new JButton("Kembali ke Login");
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; panel.add(buttonPanel, gbc);

        add(panel);

        // Action Listeners
        registerButton.addActionListener(e -> performRegister());
        backButton.addActionListener(e -> {
            this.dispose();
            loginForm.setVisible(true);
        });
    }

    private void performRegister() {
        String name = nameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field (kecuali telepon) wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Password dan Konfirmasi Password tidak cocok!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // TODO: Tambah validasi email format

        boolean success = authController.register(name, username, password, email, phone);

        if (success) {
            JOptionPane.showMessageDialog(this, "Registrasi Berhasil! Silakan login.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            loginForm.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Registrasi Gagal! Username atau Email mungkin sudah terdaftar.", "Registrasi Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}