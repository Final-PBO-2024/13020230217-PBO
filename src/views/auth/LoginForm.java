package views.auth;

import controllers.AuthController;
import models.User;
import views.admin.AdminDashboardForm;
import views.user.UserDashboardForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private AuthController authController;

    public LoginForm(AuthController authController) {
        this.authController = authController; // Store the passed AuthController instance
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(20, 20, 80, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(100, 20, 160, 25);
        add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(20, 50, 80, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(100, 50, 160, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(20, 90, 100, 25);
        add(btnLogin);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(130, 90, 100, 25);
        add(btnRegister);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Username and password cannot be empty.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                User user = authController.login(username, password);
                if (user != null) {
                    dispose();
                    if (user.getRole().equals("admin")) {
                        new AdminDashboardForm().setVisible(true);
                    } else {
                        new UserDashboardForm(user).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterForm(authController).setVisible(true); // Pass the authController instance
            }
        });
    }

    public LoginForm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}