package views.admin;

import controllers.BookingController;
import models.Booking;
import models.User; // Import User untuk fallback di goBack()

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.event.ListSelectionEvent;

public class BookingManagementForm extends JFrame {

    private AdminDashboardForm parentForm;
    private BookingController bookingController;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JButton confirmButton, cancelButton, deleteButton, restoreButton, backButton;
    private JCheckBox showDeletedCheckBox;
    private JTextField searchField;
    private Booking selectedBooking = null;
    private TableRowSorter<DefaultTableModel> sorter;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); // Diperbaiki dari dateTimeFormat
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Tambahkan ini

    public BookingManagementForm(AdminDashboardForm parent) {
        this.parentForm = parent;
        this.bookingController = new BookingController();
        initComponents();
        setTitle("Manajemen Pemesanan");
        setSize(1100, 700); // Ukuran lebih besar
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
         addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBack();
            }
        });
        loadBookings();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255)); // Alice Blue

        // Panel Atas: Pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(new Color(240, 248, 255));
        searchPanel.add(new JLabel("Cari Pemesanan (Pengguna/Lapangan):"));
        searchField = new JTextField(35); // Lebih panjang
        searchPanel.add(searchField);
        showDeletedCheckBox = new JCheckBox("Tampilkan yang Dihapus/Dibatalkan");
        showDeletedCheckBox.setBackground(new Color(240, 248, 255));
        searchPanel.add(showDeletedCheckBox);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Panel Tengah: Tabel
        tableModel = new DefaultTableModel(new String[]{"ID", "Pengguna", "Lapangan", "Tanggal", "Jam", "Harga", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        bookingTable.setRowSorter(sorter);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Styling tabel
        bookingTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingTable.setRowHeight(28);
        bookingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        bookingTable.getTableHeader().setBackground(new Color(230, 240, 255));
        bookingTable.setFillsViewportHeight(true);
        bookingTable.setGridColor(new Color(200, 200, 200));
        bookingTable.setSelectionBackground(new Color(173, 216, 230));
        bookingTable.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel Bawah: Tombol-tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        confirmButton = new JButton("Konfirmasi Pesanan");
        cancelButton = new JButton("Batalkan Pesanan");
        deleteButton = new JButton("Hapus Permanen"); // Ganti teks
        restoreButton = new JButton("Restore Pesanan");
        backButton = new JButton("Kembali ke Dashboard");

        // Styling tombol
        confirmButton.setBackground(new Color(40, 167, 69)); // Hijau
        cancelButton.setBackground(new Color(255, 193, 7)); // Kuning
        deleteButton.setBackground(new Color(220, 53, 69)); // Merah
        restoreButton.setBackground(new Color(23, 162, 184)); // Cyan
        backButton.setBackground(new Color(108, 117, 125)); // Abu-abu

        // Warna teks
        confirmButton.setForeground(Color.WHITE);
        cancelButton.setForeground(Color.BLACK);
        deleteButton.setForeground(Color.WHITE);
        restoreButton.setForeground(Color.WHITE);
        backButton.setForeground(Color.WHITE);

        // Hapus border fokus dan atur kursor, atur font, atur ukuran
        JButton[] buttons = {confirmButton, cancelButton, deleteButton, restoreButton, backButton};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(190, 40)); // Ukuran tombol seragam
        }

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(restoreButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Listeners
        backButton.addActionListener(e -> goBack());
        showDeletedCheckBox.addActionListener(e -> loadBookings());
        confirmButton.addActionListener(e -> updateStatus("Confirmed"));
        cancelButton.addActionListener(e -> updateStatus("Cancelled"));
        deleteButton.addActionListener(e -> deleteBooking());
        restoreButton.addActionListener(e -> restoreBooking());

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        bookingTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            if (!event.getValueIsAdjusting() && bookingTable.getSelectedRow() != -1) {
                int modelRow = bookingTable.convertRowIndexToModel(bookingTable.getSelectedRow());
                int bookingId = (int) tableModel.getValueAt(modelRow, 0);
                // Perlu metode getBookingById di controller & repo
                // Untuk sementara, kita ambil dari list yang sudah ada (kurang ideal, tapi workable)
                boolean showDeleted = showDeletedCheckBox.isSelected();
                List<Booking> currentList = bookingController.getAllBookings(showDeleted);
                selectedBooking = currentList.stream()
                                    .filter(b -> b.getBookingId() == bookingId)
                                    .findFirst().orElse(null);
            } else {
                 selectedBooking = null;
            }
             updateButtonStates();
        });

        updateButtonStates();
    }

    private void filterTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
             // Pencarian tidak case-sensitive pada kolom 1 (Pengguna) dan 2 (Lapangan)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
        }
    }

    private void loadBookings() {
        boolean showDeleted = showDeletedCheckBox.isSelected();
        List<Booking> bookings = bookingController.getAllBookings(showDeleted);
        tableModel.setRowCount(0);
        
        for (Booking booking : bookings) {
            String status = booking.getStatus();
            if (booking.isDeleted()) {
                 status = "Dihapus (" + status + ")";
            }
            tableModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getUserName(),
                    booking.getFieldName(),
                    dateFormat.format(booking.getBookingDate()),
                    timeFormat.format(booking.getStartTime()) + "-" + timeFormat.format(booking.getEndTime()),
                    "Rp " + booking.getTotalPrice().toPlainString(),
                    status
            });
        }
        selectedBooking = null;
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean selected = (selectedBooking != null);
        boolean isPending = selected && "Pending".equals(selectedBooking.getStatus()) && !selectedBooking.isDeleted();
        boolean isConfirmed = selected && "Confirmed".equals(selectedBooking.getStatus()) && !selectedBooking.isDeleted();
        boolean isDeleted = selected && selectedBooking.isDeleted();

        confirmButton.setEnabled(isPending);
        cancelButton.setEnabled(isPending || isConfirmed);
        deleteButton.setEnabled(selected && !isDeleted); // Hanya bisa hapus jika tidak dihapus
        restoreButton.setEnabled(isDeleted); // Hanya bisa restore jika dihapus
    }

    private void goBack() {
        this.dispose();
        // Memanggil metode di parentForm untuk menampilkan kembali dashboard dan me-refresh
        if (parentForm != null) {
            parentForm.showAdminDashboard();
        } else {
            // Fallback jika parentForm null
            new AdminDashboardForm(new User()).setVisible(true); // Ganti dengan user yang valid jika perlu
        }
    }

    private void updateStatus(String newStatus) {
        if (selectedBooking == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin mengubah status booking ID " + selectedBooking.getBookingId() + " menjadi '" + newStatus + "'?",
                "Konfirmasi Status", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingController.updateBookingStatus(selectedBooking.getBookingId(), newStatus)) {
                JOptionPane.showMessageDialog(this, "Status booking berhasil diubah.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status booking.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

     private void deleteBooking() {
        if (selectedBooking == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus booking ID " + selectedBooking.getBookingId() + "?\n(Data akan disembunyikan, bisa direstore)",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingController.softDeleteBooking(selectedBooking.getBookingId())) { // softDeleteBooking
                JOptionPane.showMessageDialog(this, "Booking berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus booking.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void restoreBooking() {
         if (selectedBooking == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin merestore booking ID " + selectedBooking.getBookingId() + "?",
                "Konfirmasi Restore", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingController.restoreBooking(selectedBooking.getBookingId())) {
                JOptionPane.showMessageDialog(this, "Booking berhasil direstore.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal merestore booking.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
