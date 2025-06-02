package views.auth;

import controllers.AuthController;
import models.User; // Import User untuk membuat objek user yang akan diregister

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
        setTitle("Registrasi Akun Baru");
        
        // Ukuran awal yang lebih pas, tidak langsung layar penuh
        setSize(800, 700); // Sedikit diperbesar vertikal untuk lebih banyak ruang
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Agar tidak exit aplikasi
        setLocationRelativeTo(null);
        setResizable(true); // Memungkinkan jendela untuk diubah ukurannya
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // Padding disesuaikan
        panel.setBackground(new Color(240, 248, 255)); // Alice Blue
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spasi antar komponen
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Beri bobot agar komponen di kolom ini melebar

        JLabel titleLabel = new JLabel("BUAT AKUN BARU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 38)); // Ukuran font disesuaikan
        titleLabel.setForeground(new Color(0, 51, 102));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(titleLabel, gbc);

        // Spacer
        gbc.gridy = 1;
        panel.add(Box.createRigidArea(new Dimension(0, 20)), gbc); // Spasi vertikal

        gbc.gridwidth = 1; // Reset
        gbc.weightx = 0; // Label tidak perlu melebar
        gbc.gridy = 2; gbc.gridx = 0; panel.add(createLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; nameField = createTextField(); panel.add(nameField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.weightx = 0; panel.add(createLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; usernameField = createTextField(); panel.add(usernameField, gbc);

        gbc.gridy = 4; gbc.gridx = 0; gbc.weightx = 0; panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; emailField = createTextField(); panel.add(emailField, gbc);

        gbc.gridy = 5; gbc.gridx = 0; gbc.weightx = 0; panel.add(createLabel("No. Telepon:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; phoneField = createTextField(); panel.add(phoneField, gbc);

        gbc.gridy = 6; gbc.gridx = 0; gbc.weightx = 0; panel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; passwordField = createPasswordField(); panel.add(passwordField, gbc);

        gbc.gridy = 7; gbc.gridx = 0; gbc.weightx = 0; panel.add(createLabel("Konfirmasi Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; confirmPasswordField = createPasswordField(); panel.add(confirmPasswordField, gbc);

        // Spacer
        gbc.gridy = 8;
        panel.add(Box.createRigidArea(new Dimension(0, 20)), gbc);

        registerButton = new JButton("Daftar Akun");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Ukuran font disesuaikan
        registerButton.setBackground(new Color(40, 167, 69)); // Hijau
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 9; gbc.gridx = 0; gbc.gridwidth = 2; gbc.ipady = 18; // Padding internal lebih tinggi
        panel.add(registerButton, gbc);

        // Tombol Kembali ke Login sebagai link
        backButton = new JButton("<html><a href=\"#\" style='color:#007BFF;'>Sudah punya akun? Kembali ke Login.</a></html>");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Ukuran font disesuaikan
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 10; gbc.ipady = 0; // Reset padding internal
        gbc.fill = GridBagConstraints.NONE; // Tidak perlu melebar
        gbc.anchor = GridBagConstraints.CENTER; // Pusatkan
        
        // --- START MODIFIED ---
        // Alokasikan ruang ekstra untuk tombol kembali agar tidak terpotong
        JPanel backButtonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonContainer.setBackground(panel.getBackground());
        backButtonContainer.add(backButton);
        gbc.gridy = 10; gbc.gridx = 0; gbc.gridwidth = 2; // Pastikan membentang 2 kolom
        gbc.weightx = 1.0; // Penting: berikan bobot horizontal agar container melebar
        gbc.fill = GridBagConstraints.HORIZONTAL; // Penting: container mengisi ruang
        panel.add(backButtonContainer, gbc);
        // --- END MODIFIED ---

        add(panel);
        
        // Tambahkan action listeners
        registerButton.addActionListener(e -> performRegister());
        backButton.addActionListener(e -> {
            this.dispose();
            loginForm.setVisible(true);
        });
    }

    // Metode bantuan untuk styling yang konsisten
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Ukuran font disesuaikan
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(25);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(25);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        return field;
    }

    private void performRegister() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim(); // Telepon bisa kosong
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama, Username, Email, dan Password tidak boleh kosong!", "Input Wajib", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Password dan Konfirmasi Password tidak cocok!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Format email tidak valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Buat objek User
        User newUser = new User();
        newUser.setName(name);
        newUser.setUsername(username);
        newUser.setPassword(password); // Password akan di-hash di AuthController
        newUser.setEmail(email);
        newUser.setPhone(phone); // Bisa kosong
        newUser.setRole("User"); // Role default untuk registrasi adalah "User"
        newUser.setDeleted(false); // Default tidak dihapus

        boolean success = authController.register(newUser); // Mengirim objek User

        if (success) {
            JOptionPane.showMessageDialog(this, "Registrasi Berhasil! Silakan login dengan akun Anda.", "Registrasi Sukses", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            loginForm.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Registrasi Gagal! Username atau Email mungkin sudah terdaftar.", "Registrasi Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Metode validasi email sederhana
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
