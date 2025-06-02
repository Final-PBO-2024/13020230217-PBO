package views.user;

import com.toedter.calendar.JDateChooser;
import controllers.BookingController;
import models.Booking;
import models.Field;
import models.Schedule;
import models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class BookingForm extends JFrame {

    // Mengganti parentForm dari FieldListForm ke UserDashboardForm untuk konsistensi
    // Jika Anda ingin tetap ke FieldListForm, ubah kembali tipe datanya.
    private UserDashboardForm parentForm; // Mengubah tipe parentForm
    private FieldListForm fieldListParentForm; // Jika Anda masih ingin bisa kembali ke FieldListForm
    private User currentUser;
    private Field selectedField;
    private BookingController bookingController;

    private JDateChooser dateChooser;
    private JList<Schedule> scheduleList;
    private DefaultListModel<Schedule> listModel;
    private JButton bookButton, backButton;
    private JLabel fieldNameLabel, priceLabel, totalLabel;

    // Constructor yang menerima FieldListForm sebagai parent, lalu bisa kembali ke UserDashboardForm
    public BookingForm(FieldListForm parent, User user, Field field) {
        this.fieldListParentForm = parent; // Simpan referensi FieldListForm
        this.parentForm = parent.getParentForm(); // Ambil referensi UserDashboardForm dari FieldListForm
        this.currentUser = user;
        this.selectedField = field;
        this.bookingController = new BookingController();

        try {
            Class.forName("com.toedter.calendar.JDateChooser");
        } catch (ClassNotFoundException e) {
             JOptionPane.showMessageDialog(null,
                    "Library JCalendar tidak ditemukan!\nSilakan unduh dan tambahkan ke project.",
                    "Library Error", JOptionPane.ERROR_MESSAGE);
             // Memanggil goBack() yang sesuai dengan struktur parent.
             // Jika fieldListParentForm null, mungkin kita langsung ke UserDashboardForm.
             if (fieldListParentForm != null) {
                 goBackToFieldList();
             } else if (parentForm != null) {
                 goBackToDashboard();
             } else {
                 dispose();
             }
             return;
        }

        initComponents();
        setTitle("Pemesanan Lapangan: " + selectedField.getName());
        setSize(550, 650); // Ukuran lebih besar
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBackToFieldList(); // Default kembali ke FieldListForm
            }
        });
        updateFieldInfo();
        // Muat jadwal segera untuk tanggal hari ini jika diatur
        dateChooser.setDate(new java.util.Date());
        loadAvailableSchedules(); // Muat jadwal saat form dibuka
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255)); // Alice Blue

        // Panel Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2), "Detail Lapangan", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(0, 51, 102)));
        infoPanel.setBackground(new Color(255, 255, 255)); // Putih
        fieldNameLabel = new JLabel("Lapangan: ");
        fieldNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        fieldNameLabel.setForeground(new Color(0, 51, 102));
        priceLabel = new JLabel("Harga/Jam: ");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        infoPanel.add(fieldNameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        infoPanel.add(priceLabel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Panel Pemesanan
        JPanel bookingPanel = new JPanel(new GridBagLayout());
        bookingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2), "Pilih Tanggal & Jadwal", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(0, 51, 102)));
        bookingPanel.setBackground(new Color(255, 255, 255)); // Putih
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; bookingPanel.add(new JLabel("Pilih Tanggal:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; 
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd MMMM yyyy");
        dateChooser.setMinSelectableDate(new java.util.Date()); // Hanya bisa pilih hari ini atau ke depan
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingPanel.add(dateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        bookingPanel.add(new JLabel("Jadwal Tersedia:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        listModel = new DefaultListModel<>();
        scheduleList = new JList<>(listModel);
        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleList.setFont(new Font("Monospaced", Font.PLAIN, 15));
        scheduleList.setFixedCellHeight(25); // Tinggi baris list
        scheduleList.setBackground(new Color(245, 245, 245)); // Abu-abu terang
        JScrollPane listScrollPane = new JScrollPane(scheduleList);
        listScrollPane.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230))); // Border biru muda
        bookingPanel.add(listScrollPane, gbc);

        mainPanel.add(bookingPanel, BorderLayout.CENTER);

        // Panel Tombol
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(new Color(240, 248, 255));

        totalLabel = new JLabel("Total Harga: Rp 0", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(new Color(0, 102, 0)); // Hijau gelap
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Padding

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(bottomPanel.getBackground());
        backButton = new JButton("Kembali");
        backButton.setBackground(new Color(108, 117, 125)); // Abu-abu
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bookButton = new JButton("Pesan Sekarang!");
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        bookButton.setBackground(new Color(40, 167, 69)); // Hijau
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setEnabled(false); // Default disabled

        buttonPanel.add(backButton);
        buttonPanel.add(bookButton);
        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Listeners
        backButton.addActionListener(e -> goBackToFieldList()); // Kembali ke FieldListForm
        dateChooser.getDateEditor().addPropertyChangeListener("date", evt -> loadAvailableSchedules());
        scheduleList.addListSelectionListener(e -> {
            boolean isPlaceholderSelected = selectedTimeIsPlaceholder();
            bookButton.setEnabled(scheduleList.getSelectedIndex() != -1 && !isPlaceholderSelected);
            updateTotal();
        });
        bookButton.addActionListener(e -> performBooking());
    }

    private void updateFieldInfo() {
        fieldNameLabel.setText("Lapangan: " + selectedField.getName() + " (" + selectedField.getType() + ")");
        priceLabel.setText("Harga/Jam: Rp " + selectedField.getPricePerHour().toPlainString());
    }

    private void updateTotal() {
        if(scheduleList.getSelectedIndex() != -1 && !selectedTimeIsPlaceholder()){
            totalLabel.setText("Total Harga: Rp " + selectedField.getPricePerHour().toPlainString());
        } else {
            totalLabel.setText("Total Harga: Rp 0");
        }
    }

    private boolean selectedTimeIsPlaceholder() {
        Schedule selected = scheduleList.getSelectedValue();
        return selected != null && (selected.getScheduleId() == 0 || selected.getFieldName() == null); // Asumsi placeholder memiliki ID 0 atau fieldName null
    }

    private void loadAvailableSchedules() {
        listModel.clear();
        bookButton.setEnabled(false);
        totalLabel.setText("Total Harga: Rp 0");

        java.util.Date selectedDateUtil = dateChooser.getDate();
        if (selectedDateUtil == null) {
            listModel.addElement(new Schedule() {
                @Override public String toString() { return "--- Pilih Tanggal Dahulu ---"; }
            });
            return;
        }

        // Konversi java.util.Date ke java.sql.Date
        java.sql.Date selectedSqlDate = new java.sql.Date(selectedDateUtil.getTime());

        // Asumsi BookingController memiliki getAvailableSchedules(fieldId, date)
        List<Schedule> availableSchedules = bookingController.getAvailableSchedules(selectedField.getFieldId(), selectedSqlDate);

        if (availableSchedules.isEmpty()) {
            listModel.addElement(new Schedule() {
                @Override public String toString() { return "--- Tidak Ada Jadwal Tersedia ---"; }
            });
        } else {
            for (Schedule schedule : availableSchedules) {
                listModel.addElement(schedule);
            }
        }
    }

    // Metode kembali ke FieldListForm
    private void goBackToFieldList() {
        this.dispose();
        if (fieldListParentForm != null) {
            fieldListParentForm.setVisible(true);
            fieldListParentForm.loadFields(); // Refresh daftar lapangan
        } else if (parentForm != null) { // Fallback ke UserDashboard jika FieldListForm tidak tersedia
            parentForm.showUserDashboard();
        } else {
            new UserDashboardForm(currentUser).setVisible(true); // Fallback jika tidak ada parent
        }
    }

    // Metode kembali ke UserDashboardForm (jika dipanggil langsung dari dashboard)
    private void goBackToDashboard() {
        this.dispose();
        if (parentForm != null) {
            parentForm.showUserDashboard();
        } else {
            new UserDashboardForm(currentUser).setVisible(true);
        }
    }


    private void performBooking() {
        Schedule selectedTime = scheduleList.getSelectedValue();
        java.util.Date selectedDateUtil = dateChooser.getDate();

        if (selectedTime == null || selectedDateUtil == null || selectedTime.getScheduleId() == 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih tanggal dan jadwal yang valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(selectedDateUtil.getTime());

        Booking newBooking = new Booking();
        newBooking.setUserId(currentUser.getUserId());
        newBooking.setFieldId(selectedField.getFieldId());
        newBooking.setBookingDate(sqlDate);
        newBooking.setStartTime(selectedTime.getStartTime());
        newBooking.setEndTime(selectedTime.getEndTime());
        newBooking.setTotalPrice(selectedField.getPricePerHour()); // Asumsi 1 jam
        newBooking.setStatus("Pending"); // Status awal

        int confirm = JOptionPane.showConfirmDialog(this,
                "Anda akan memesan:\nLapangan: " + selectedField.getName() +
                "\nTanggal: " + new SimpleDateFormat("dd-MM-yyyy").format(sqlDate) +
                "\nJam: " + selectedTime.toString() +
                "\nTotal: Rp " + newBooking.getTotalPrice().toPlainString() +
                "\n\nLanjutkan pemesanan?", "Konfirmasi Pemesanan",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingController.createBooking(newBooking)) {
                JOptionPane.showMessageDialog(this,
                        "Pemesanan Berhasil!\nStatus pemesanan Anda saat ini 'Pending'.\nSilakan tunggu konfirmasi dari Admin.",
                        "Pemesanan Sukses", JOptionPane.INFORMATION_MESSAGE);
                goBackToDashboard(); // Kembali ke dashboard pengguna setelah pemesanan
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal melakukan pemesanan. Jadwal mungkin sudah terisi.\nSilakan pilih jadwal lain atau muat ulang jadwal.",
                        "Pemesanan Gagal", JOptionPane.ERROR_MESSAGE);
                loadAvailableSchedules(); // Muat ulang untuk melihat update
            }
        }
    }
}
