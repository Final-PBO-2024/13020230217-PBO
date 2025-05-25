package views.auth;

import controllers.AuthController;
import models.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegisterForm extends JFrame {
    private JTextField txtName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JButton btnRegister;
    private JButton btnBack;
    private AuthController authController;

    public RegisterForm(AuthController authController) {
        this.authController = authController;
        setTitle("Register");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(20, 20, 80, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(100, 20, 160, 25);
        add(txtName);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(20, 50, 80, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(100, 50, 160, 25);
        add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(20, 80, 80, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(100, 80, 160, 25);
        add(txtPassword);

        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        lblConfirmPassword.setBounds(20, 110, 100, 25);
        add(lblConfirmPassword);

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setBounds(100, 110, 160, 25);
        add(txtConfirmPassword);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(20, 140, 80, 25);
        add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(100, 140, 160, 25);
        add(txtEmail);

        JLabel lblPhone = new JLabel("Phone:");
        lblPhone.setBounds(20, 170, 80, 25);
        add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(100, 170, 160, 25);
        add(txtPhone);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(20, 210, 100, 25);
        add(btnRegister);

        btnBack = new JButton("Back");
        btnBack.setBounds(130, 210, 100, 25);
        add(btnBack);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = txtName.getText().trim();
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();
                String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
                String email = txtEmail.getText().trim();
                String phone = txtPhone.getText().trim();

                if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterForm.this, "All fields are required.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterForm.this, "Passwords do not match.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User user = new User();
                user.setName(name);
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setPhone(phone);
                user.setRole("user");

                if (authController.register(user, confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterForm.this, "Registration successful! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new LoginForm(authController).setVisible(true); // Pass the existing authController
                }
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginForm(authController).setVisible(true); // Pass the existing authController
            }
        });
    }
}