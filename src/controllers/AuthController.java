package controllers;

import models.User;
import repositories.UserRepository;

public class AuthController {

    private UserRepository userRepository;

    public AuthController() {
        this.userRepository = new UserRepository();
    }

    public User login(String username, String password) {
        return userRepository.getUserByUsernameAndPassword(username, password); // Changed from getUserUsernameAndPassword
    }

    public boolean register(String name, String username, String password, String email, String phone) {
        if (userRepository.isUsernameOrEmailExists(username, email)) {
            return false; // User sudah ada
        }
        User newUser = new User();
        newUser.setName(name);
        newUser.setUsername(username);
        newUser.setPassword(password); // TODO: Hash!
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setRole("User"); // Role default saat registrasi adalah User
        newUser.setDeleted(false);

        return userRepository.addUser(newUser);
    }
}