import views.auth.LoginForm;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Kelas utama untuk menjalankan aplikasi SportBookingSystem.
 */
public class Main {

    public static void main(String[] args) {
        // Mengatur Look and Feel Nimbus untuk tampilan yang lebih modern
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Jika Nimbus tidak tersedia, gunakan Look and Feel default
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Menjalankan LoginForm di Event Dispatch Thread (EDT) Swing
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}