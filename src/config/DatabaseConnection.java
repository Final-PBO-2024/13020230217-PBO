package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Kelas untuk menyediakan koneksi database BARU setiap kali diminta.
 */
public class DatabaseConnection {

    // Hapus variabel 'connection' statis

    // --- Konfigurasi Database ---
    private static final String URL = "jdbc:mysql://localhost:3306/sport_booking_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    // ---------------------------

    /**
     * Membuat dan mengembalikan instance koneksi database BARU.
     *
     * @return Connection object atau null jika gagal.
     */
    public static Connection getConnection() {
        Connection conn = null; // Buat variabel lokal
        try {
            // Mendaftarkan driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Membuat koneksi BARU
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            // System.out.println("Koneksi Database Baru Dibuat!"); // Opsional

        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Koneksi Database Gagal: " + e.getMessage(),
                    "Error Koneksi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Anda mungkin ingin melempar exception atau keluar di sini
        }
        return conn; // Kembalikan koneksi baru (atau null jika gagal)
    }

    /**
     * Menutup koneksi database. (Metode ini tidak lagi relevan
     * jika kita menggunakan try-with-resources, tapi biarkan saja
     * jika ada kode lama yang mungkin menggunakannya).
     * Disarankan untuk TIDAK MENGGUNAKAN metode ini jika memakai
     * try-with-resources.
     */
    public static void closeConnection(Connection connToClose) {
        if (connToClose != null) {
            try {
                connToClose.close();
                // System.out.println("Koneksi Ditutup.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}