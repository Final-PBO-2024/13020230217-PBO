package views.user;

import controllers.BookingController;
import models.Booking;
import models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.event.ListSelectionEvent;

public class BookingHistoryForm extends JFrame {

    private UserDashboardForm parentForm;
    private User currentUser;
    private BookingController bookingController;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton backButton, cancelButton;
    private Booking selectedBooking = null;

    public BookingHistoryForm(UserDashboardForm parent, User user) {
        this.parentForm = parent;
        this.currentUser = user;
        this.bookingController = new BookingController();
        initComponents();
        setTitle("Riwayat Pemesanan - " + currentUser.getName());
        setSize(1000, 600); // Ukuran lebih besar
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBack();
            }
        });
        loadHistory();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel("Riwayat Pemesanan Anda", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(0, 51, 102));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabel
        tableModel = new DefaultTableModel(new String[]{"ID", "Lapangan", "Tanggal", "Jam", "Harga", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Styling tabel
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyTable.setRowHeight(28);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        historyTable.getTableHeader().setBackground(new Color(230, 240, 255));
        historyTable.setFillsViewportHeight(true);
        historyTable.setGridColor(new Color(200, 200, 200));
        historyTable.setSelectionBackground(new Color(173, 216, 230));
        historyTable.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Tombol-tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        cancelButton = new JButton("Batalkan Pesanan Terpilih");
        cancelButton.setEnabled(false); // Default disabled
        backButton = new JButton("Kembali ke Dashboard");

        // Styling tombol
        cancelButton.setBackground(new Color(255, 193, 7)); // Kuning
        cancelButton.setForeground(Color.BLACK);
        backButton.setBackground(new Color(108, 117, 125)); // Abu-abu
        backButton.setForeground(Color.WHITE);

        JButton[] buttons = {cancelButton, backButton};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(220, 40)); // Ukuran tombol seragam
        }

        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Listeners
        backButton.addActionListener(e -> goBack());
        cancelButton.addActionListener(e -> cancelBooking());

        historyTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = historyTable.getSelectedRow();
            if (!e.getValueIsAdjusting() && selectedRow != -1) { // Periksa !e.getValueIsAdjusting()
                int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
                 // Lebih baik ambil booking dari controller untuk memastikan data terbaru
                 selectedBooking = bookingController.getBookingById(bookingId); // Asumsi ada getBookingById
                                     
                // Hanya bisa batalkan jika status 'Pending' dan belum dihapus
                cancelButton.setEnabled(selectedBooking != null && "Pending".equals(selectedBooking.getStatus()) && !selectedBooking.isDeleted());
            } else {
                selectedBooking = null;
                cancelButton.setEnabled(false);
            }
        });
    }

    private void loadHistory() {
        // Menggunakan getBookingsByUserId dengan includeDeleted=false untuk hanya yang aktif
        List<Booking> bookings = bookingController.getBookingsByUserId(currentUser.getUserId(), false);
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        for (Booking booking : bookings) {
             tableModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getFieldName(),
                    dateFormat.format(booking.getBookingDate()),
                    timeFormat.format(booking.getStartTime()) + "-" + timeFormat.format(booking.getEndTime()),
                    "Rp " + booking.getTotalPrice().toPlainString(),
                    booking.getStatus()
            });
        }
        selectedBooking = null;
        cancelButton.setEnabled(false);
    }

    private void goBack() {
        this.dispose();
        // Memanggil metode di parentForm untuk menampilkan kembali dashboard dan me-refresh
        if (parentForm != null) {
            parentForm.showUserDashboard();
        } else {
            // Fallback jika parentForm null
            new UserDashboardForm(currentUser).setVisible(true);
        }
    }

    private void cancelBooking() {
        if (selectedBooking == null || !"Pending".equals(selectedBooking.getStatus())) {
             JOptionPane.showMessageDialog(this, "Pilih pesanan dengan status 'Pending' untuk dibatalkan.", "Info", JOptionPane.INFORMATION_MESSAGE);
             return;
        }

         int confirm = JOptionPane.showConfirmDialog(this,
                "Anda yakin ingin membatalkan pesanan ID " + selectedBooking.getBookingId() + "?",
                "Konfirmasi Pembatalan", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

         if (confirm == JOptionPane.YES_OPTION) {
             // Asumsi ada metode cancelBooking(int bookingId) di BookingController yang mengubah status menjadi 'Cancelled'
             if (bookingController.cancelBooking(selectedBooking.getBookingId())) {
                 JOptionPane.showMessageDialog(this, "Pesanan berhasil dibatalkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                 loadHistory(); // Muat ulang
             } else {
                 JOptionPane.showMessageDialog(this, "Gagal membatalkan pesanan.", "Gagal", JOptionPane.ERROR_MESSAGE);
             }
         }
    }
}
