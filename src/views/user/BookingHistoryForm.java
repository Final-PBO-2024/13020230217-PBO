package views.user;

import controllers.BookingController;
import models.Booking;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

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
        setSize(900, 500);
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
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Riwayat Pemesanan Anda", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Lapangan", "Tanggal", "Jam", "Harga", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelButton = new JButton("Batalkan Pesanan Terpilih");
        cancelButton.setEnabled(false);
        backButton = new JButton("Kembali");
        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Listeners
        backButton.addActionListener(e -> goBack());
        cancelButton.addActionListener(e -> cancelBooking());

        historyTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = historyTable.getSelectedRow();
            if (selectedRow != -1) {
                int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
                 List<Booking> currentList = bookingController.getBookingsByUserId(currentUser.getUserId(), false);
                 selectedBooking = currentList.stream()
                                     .filter(b -> b.getBookingId() == bookingId)
                                     .findFirst().orElse(null);

                // Hanya bisa cancel jika status 'Pending'
                cancelButton.setEnabled(selectedBooking != null && "Pending".equals(selectedBooking.getStatus()));
            } else {
                selectedBooking = null;
                cancelButton.setEnabled(false);
            }
        });
    }

    private void loadHistory() {
        List<Booking> bookings = bookingController.getBookingsByUserId(currentUser.getUserId(), false); // Hanya tampilkan yg aktif
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
        parentForm.setVisible(true);
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
             if (bookingController.cancelBooking(selectedBooking.getBookingId())) {
                 JOptionPane.showMessageDialog(this, "Pesanan berhasil dibatalkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                 loadHistory(); // Muat ulang
             } else {
                 JOptionPane.showMessageDialog(this, "Gagal membatalkan pesanan.", "Gagal", JOptionPane.ERROR_MESSAGE);
             }
         }
    }
}