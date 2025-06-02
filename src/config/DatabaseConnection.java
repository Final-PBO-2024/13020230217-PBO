package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;


public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/sport_booking_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Koneksi Database Gagal: " + e.getMessage(),
                    "Error Koneksi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return conn; 
    }

    public static void closeConnection(Connection connToClose) {
        if (connToClose != null) {
            try {
                connToClose.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}