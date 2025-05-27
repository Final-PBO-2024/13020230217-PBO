package views.admin;

import controllers.BookingController;
import models.Booking;

import javax.swing.*;
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
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public BookingManagementForm(AdminDashboardForm parent) {
        this.parentForm = parent;
        this.bookingController = new BookingController();
        initComponents();
        setTitle("Manajemen Pemesanan");
        setSize(1000, 600);
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Cari (User/Lapangan):"));
        searchField = new JTextField(30);
        searchPanel.add(searchField);
        showDeletedCheckBox = new JCheckBox("Tampilkan yang Dihapus/Batal");
        searchPanel.add(showDeletedCheckBox);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Center Panel: Table
        tableModel = new DefaultTableModel(new String[]{"ID", "User", "Lapangan", "Tanggal", "Jam", "Harga", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        bookingTable.setRowSorter(sorter);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        confirmButton = new JButton("Konfirmasi");
        cancelButton = new JButton("Batalkan");
        deleteButton = new JButton("Hapus");
        restoreButton = new JButton("Restore");
        backButton = new JButton("Kembali");

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
                // Untuk sementara, kita ambil dari list yang sudah ada (kurang ideal)
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
             // Case-insensitive search on column 1 (User) and 2 (Lapangan)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
        }
    }

    private void loadBookings() {
        boolean showDeleted = showDeletedCheckBox.isSelected();
        List<Booking> bookings = bookingController.getAllBookings(showDeleted);
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

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
                    booking.getTotalPrice(),
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
        deleteButton.setEnabled(selected && !isDeleted);
        restoreButton.setEnabled(isDeleted);
    }

    private void goBack() {
        this.dispose();
        parentForm.setVisible(true);
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
            if (bookingController.softDeleteBooking(selectedBooking.getBookingId())) {
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