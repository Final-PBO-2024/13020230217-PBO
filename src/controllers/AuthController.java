package controllers;

import models.User;
import repositories.UserRepository;

public class AuthController {

    private UserRepository userRepository;

    public AuthController() {
        this.userRepository = new UserRepository();
    }

    public User login(String username, String password) {
        return userRepository.getUserByUsernameAndPassword(username, password);
    }

    // --- START MODIFIED ---
    // Mengubah metode register untuk menerima objek User
    public boolean register(User user) {
        if (userRepository.isUsernameOrEmailExists(user.getUsername(), user.getEmail())) {
            return false; // User sudah ada
        }
        // Asumsi password akan di-hash di UserRepository atau sebelum dipanggil di sini jika diperlukan
        // new User() sudah diisi di RegisterForm, jadi langsung pakai objek user yang diterima
        return userRepository.addUser(user);
    }
    // --- END MODIFIED ---
}
