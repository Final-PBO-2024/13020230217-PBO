package views.user;

import models.User;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class UserProfileForm extends JFrame {

    private UserDashboardForm parentForm;
    private User currentUser;

    private JLabel nameLabel, usernameLabel, emailLabel, phoneLabel, roleLabel;
    private JButton backButton;

    public UserProfileForm(UserDashboardForm parent, User user) {
        this.parentForm = parent;
        this.currentUser = user;
        initComponents();
        setTitle("Profil Pengguna - " + currentUser.getName());
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBack();
            }
        });
        displayProfileInfo();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255)); // Alice Blue

        JLabel titleLabel = new JLabel("Detail Profil Anda", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(0, 51, 102));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel profileInfoPanel = new JPanel(new GridBagLayout());
        profileInfoPanel.setBackground(new Color(255, 255, 255)); // Putih
        profileInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2), "Informasi Akun", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(0, 51, 102)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; profileInfoPanel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; nameLabel = createInfoLabel(""); profileInfoPanel.add(nameLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; profileInfoPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; usernameLabel = createInfoLabel(""); profileInfoPanel.add(usernameLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; profileInfoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; emailLabel = createInfoLabel(""); profileInfoPanel.add(emailLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; profileInfoPanel.add(new JLabel("No. Telepon:"), gbc);
        gbc.gridx = 1; phoneLabel = createInfoLabel(""); profileInfoPanel.add(phoneLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; profileInfoPanel.add(new JLabel("Peran:"), gbc);
        gbc.gridx = 1; roleLabel = createInfoLabel(""); profileInfoPanel.add(roleLabel, gbc);
        row++;

        mainPanel.add(profileInfoPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        backButton = new JButton("Kembali ke Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(200, 40));
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        backButton.addActionListener(e -> goBack());
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(new Color(50, 50, 50));
        return label;
    }

    private void displayProfileInfo() {
        if (currentUser != null) {
            nameLabel.setText(currentUser.getName());
            usernameLabel.setText(currentUser.getUsername());
            emailLabel.setText(currentUser.getEmail());
            phoneLabel.setText(currentUser.getPhone() != null && !currentUser.getPhone().isEmpty() ? currentUser.getPhone() : "-");
            roleLabel.setText(currentUser.getRole());
        }
    }

    private void goBack() {
        this.dispose();
        if (parentForm != null) {
            parentForm.showUserDashboard();
        } else {
            new UserDashboardForm(currentUser).setVisible(true);
        }
    }
}
