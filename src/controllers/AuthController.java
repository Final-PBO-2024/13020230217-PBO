package controllers;

import models.User;
import repositories.UserRepository;

import javax.swing.JOptionPane;
import java.sql.SQLException;

public class AuthController {
    private UserRepository userRepository;

    public AuthController() {
        this.userRepository = new UserRepository();
    }

    public User login(String username, String password) {
        try {
            User user = userRepository.findByUsernameAndPassword(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(null, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return user;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public boolean register(User user, String confirmPassword) {
        try {
            if (!user.getPassword().equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (userRepository.existsByUsername(user.getUsername())) {
                JOptionPane.showMessageDialog(null, "Username already exists", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                JOptionPane.showMessageDialog(null, "Email already exists", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            user.setRole("user"); // Only users register
            userRepository.save(user);
            JOptionPane.showMessageDialog(null, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
        
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}