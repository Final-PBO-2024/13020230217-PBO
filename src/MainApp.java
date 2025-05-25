import config.DatabaseConnection;
import controllers.AuthController;
import views.auth.LoginForm;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        try {
            DatabaseConnection.getConnection();
            System.out.println("Connection successful!");

            AuthController authController = new AuthController();
            LoginForm loginForm = new LoginForm(authController);
            loginForm.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Gagal terhubung ke database:\n" + e.getMessage(),
                "Koneksi Database Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
