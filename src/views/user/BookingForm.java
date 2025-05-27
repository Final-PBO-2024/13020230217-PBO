package views.user;

import com.toedter.calendar.JDateChooser; // Anda perlu JCalendar library!
import controllers.BookingController;
import controllers.FieldController;
import models.Booking;
import models.Field;
import models.Schedule;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class BookingForm extends JFrame {

    private FieldListForm parentForm;
    private User currentUser;
    private Field selectedField;
    private BookingController bookingController;

    private JDateChooser dateChooser;
    private JList<Schedule> scheduleList;
    private DefaultListModel<Schedule> listModel;
    private JButton bookButton, backButton;
    private JLabel fieldNameLabel, priceLabel, totalLabel;

    public BookingForm(FieldListForm parent, User user, Field field) {
        this.parentForm = parent;
        this.currentUser = user;
        this.selectedField = field;
        this.bookingController = new BookingController();

        // Check if JCalendar is available
        try {
            Class.forName("com.toedter.calendar.JDateChooser");
        } catch (ClassNotFoundException e) {
             JOptionPane.showMessageDialog(null,
                    "Library JCalendar tidak ditemukan!\nSilakan unduh dan tambahkan ke project.",
                    "Library Error", JOptionPane.ERROR_MESSAGE);
             goBack(); // Kembali jika library tidak ada
             return; // Stop inisialisasi
        }


        initComponents();
        setTitle("Booking Lapangan: " + selectedField.getName());
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBack();
            }
        });
        updateFieldInfo();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Detail Lapangan"));
        fieldNameLabel = new JLabel("Lapangan: ");
        fieldNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel = new JLabel("Harga/Jam: ");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(fieldNameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        infoPanel.add(priceLabel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Booking Panel
        JPanel bookingPanel = new JPanel(new GridBagLayout());
        bookingPanel.setBorder(BorderFactory.createTitledBorder("Pilih Tanggal & Jadwal"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; bookingPanel.add(new JLabel("Pilih Tanggal:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd MMMM yyyy");
        dateChooser.setMinSelectableDate(new java.util.Date()); // Hanya bisa pilih hari ini atau ke depan
        bookingPanel.add(dateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        bookingPanel.add(new JLabel("Pilih Jadwal Tersedia:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        listModel = new DefaultListModel<>();
        scheduleList = new JList<>(listModel);
        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane listScrollPane = new JScrollPane(scheduleList);
        bookingPanel.add(listScrollPane, gbc);

        mainPanel.add(bookingPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        totalLabel = new JLabel("Total Harga: Rp 0", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(Color.BLUE);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButton = new JButton("Kembali");
        bookButton = new JButton("Booking Sekarang!");
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookButton.setBackground(new Color(0, 153, 51));
        bookButton.setForeground(Color.WHITE);
        bookButton.setEnabled(false);
        buttonPanel.add(backButton);
        buttonPanel.add(bookButton);
        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Listeners
        backButton.addActionListener(e -> goBack());
        dateChooser.getDateEditor().addPropertyChangeListener("date", evt -> loadAvailableSchedules());
        scheduleList.addListSelectionListener(e -> {
            bookButton.setEnabled(scheduleList.getSelectedIndex() != -1);
            updateTotal();
        });
        bookButton.addActionListener(e -> performBooking());
    }

    private void updateFieldInfo() {
        fieldNameLabel.setText("Lapangan: " + selectedField.getName() + " (" + selectedField.getType() + ")");
        priceLabel.setText("Harga/Jam: Rp " + selectedField.getPricePerHour().toPlainString());
    }

     private void updateTotal() {
        if(scheduleList.getSelectedIndex() != -1){
            totalLabel.setText("Total Harga: Rp " + selectedField.getPricePerHour().toPlainString());
        } else {
            totalLabel.setText("Total Harga: Rp 0");
        }
     }

    private void loadAvailableSchedules() {
        listModel.clear();
        bookButton.setEnabled(false);
        totalLabel.setText("Total Harga: Rp 0");

        java.util.Date selectedDateUtil = dateChooser.getDate();
        if (selectedDateUtil == null) {
            listModel.addElement(new Schedule() { // Placeholder
                @Override public String toString() { return "--- Pilih Tanggal Dahulu ---"; }
            });
            return;
        }

        // Convert java.util.Date to java.sql.Date
        java.sql.Date selectedSqlDate = new java.sql.Date(selectedDateUtil.getTime());

        List<Schedule> availableSchedules = bookingController.getAvailableSchedules(selectedField.getFieldId(), selectedSqlDate);

        if (availableSchedules.isEmpty()) {
            listModel.addElement(new Schedule() { // Placeholder
                @Override public String toString() { return "--- Tidak Ada Jadwal Tersedia ---"; }
            });
        } else {
            for (Schedule schedule : availableSchedules) {
                listModel.addElement(schedule);
            }
        }
    }

    private void goBack() {
        this.dispose();
        parentForm.setVisible(true);
    }

    private void performBooking() {
        Schedule selectedTime = scheduleList.getSelectedValue();
        java.util.Date selectedDateUtil = dateChooser.getDate();

        if (selectedTime == null || selectedDateUtil == null || selectedTime.getScheduleId() == 0) { // Check if not placeholder
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
                "\n\nLanjutkan pemesanan?", "Konfirmasi Booking",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingController.createBooking(newBooking)) {
                JOptionPane.showMessageDialog(this,
                        "Booking Berhasil!\nStatus booking Anda saat ini 'Pending'.\nSilakan tunggu konfirmasi dari Admin.",
                        "Booking Sukses", JOptionPane.INFORMATION_MESSAGE);
                goBack(); // Kembali ke list lapangan atau dashboard
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal melakukan booking. Jadwal mungkin sudah terisi.\nSilakan pilih jadwal lain atau muat ulang jadwal.",
                        "Booking Gagal", JOptionPane.ERROR_MESSAGE);
                loadAvailableSchedules(); // Muat ulang untuk melihat update
            }
        }
    }
}