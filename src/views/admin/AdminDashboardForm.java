package views.admin;

import models.User;
import views.auth.LoginForm;
import controllers.FieldController;
import controllers.ScheduleController;
import controllers.BookingController; // Import BookingController
import models.Field;
import models.Schedule;
import models.Booking; // Import Booking

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminDashboardForm extends JFrame {

    private User adminUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Komponen untuk tampilan overview
    private JTable overviewFieldTable;
    private DefaultTableModel overviewFieldTableModel;
    private JTable overviewScheduleTable;
    private DefaultTableModel overviewScheduleTableModel;
    private JTable overviewBookingTable; // Tabel untuk overview pemesanan
    private DefaultTableModel overviewBookingTableModel; // Model untuk overview pemesanan

    private FieldController fieldController;
    private ScheduleController scheduleController;
    private BookingController bookingController; // Inisialisasi BookingController
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public AdminDashboardForm(User user) {
        this.adminUser = user;
        this.fieldController = new FieldController();
        this.scheduleController = new ScheduleController();
        this.bookingController = new BookingController();
        initComponents();
        setTitle("Admin Dashboard - " + adminUser.getName());
        setSize(1300, 750); // Ukuran lebih besar untuk dashboard
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
//        showOverviewPanel(); // Tampilkan overview saat pertama kali masuk
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)); // Jarak lebih besar antar komponen
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(230, 245, 255)); // Light Blue Background

        // Panel Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 255, 255)); // Header putih
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(173, 216, 230))); // Border bawah

        JLabel headerLabel = new JLabel("ADMINISTRATOR DASHBOARD", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerLabel.setForeground(new Color(25, 25, 112)); // Biru gelap
        
        JLabel welcomeLabel = new JLabel("Selamat Datang, " + adminUser.getName() + "!", SwingConstants.RIGHT);
        welcomeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        welcomeLabel.setForeground(new Color(50, 50, 150));
        
        JPanel welcomeContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        welcomeContainer.setBackground(headerPanel.getBackground());
        welcomeContainer.add(welcomeLabel);

        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.add(welcomeContainer, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Sidebar (Menu di sisi kiri)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS)); // Layout vertikal
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        sidebarPanel.setBackground(new Color(173, 216, 230)); // Sidebar biru muda
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight())); // Lebar sidebar

        JLabel menuTitle = new JLabel("MENU NAVIGASI");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        menuTitle.setForeground(new Color(25, 25, 112));
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(menuTitle);
        sidebarPanel.add(Box.createVerticalStrut(30)); // Spasi vertikal

        JButton overviewButton = createSidebarButton("Dashboard Overview");
        JButton fieldManagementButton = createSidebarButton("Manajemen Lapangan");
        JButton scheduleManagementButton = createSidebarButton("Manajemen Jadwal");
        JButton bookingManagementButton = createSidebarButton("Manajemen Pemesanan");

        sidebarPanel.add(overviewButton);
        sidebarPanel.add(fieldManagementButton);
        sidebarPanel.add(scheduleManagementButton);
        sidebarPanel.add(bookingManagementButton);
        sidebarPanel.add(Box.createVerticalGlue()); // Dorong tombol ke atas
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoutButton.setBackground(new Color(220, 53, 69)); // Merah
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Panel Konten (menggunakan CardLayout untuk mengganti tampilan)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(new Color(255, 255, 255)); // Latar belakang konten putih
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Tambahkan Panel Overview ke contentPanel
        contentPanel.add(createOverviewPanel(), "overview");
        
        // Action Listeners untuk tombol sidebar
        overviewButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "overview");
            loadOverviewData(); // Refresh data saat menampilkan overview
        });
        fieldManagementButton.addActionListener(e -> openManagementForm(new FieldManagementForm(this)));
        scheduleManagementButton.addActionListener(e -> openManagementForm(new ScheduleManagementForm(this)));
        bookingManagementButton.addActionListener(e -> openManagementForm(new BookingManagementForm(this)));

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Anda yakin ingin logout?", "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginForm().setVisible(true);
            }
        });
    }

    // Metode bantuan untuk membuat tombol sidebar
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55)); // Tombol lebih besar
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10)); // Padding lebih banyak
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // Cornflower Blue saat hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }

    // Metode untuk membuka form manajemen baru
    private void openManagementForm(JFrame form) {
        form.setVisible(true);
        this.setVisible(false); // Sembunyikan dashboard saat form manajemen terbuka
    }

    // Metode ini dipanggil oleh form anak ketika mereka ditutup
    public void showAdminDashboard() {
        this.setVisible(true); // Tampilkan kembali dashboard
        loadOverviewData(); // Muat ulang semua data overview
        cardLayout.show(contentPanel, "overview"); // Pastikan panel overview yang terlihat
    }

    // --- Panel Overview ---
    private JPanel createOverviewPanel() {
        JPanel overviewPanel = new JPanel(new GridLayout(3, 1, 15, 15)); // 3 baris untuk Lapangan, Jadwal, Pemesanan
        overviewPanel.setBackground(Color.WHITE);

        // Overview Lapangan
        JPanel fieldOverviewPanel = createTableOverviewPanel("Daftar Lapangan Aktif");
        overviewFieldTableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Tipe", "Harga/Jam"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        overviewFieldTable = new JTable(overviewFieldTableModel);
        setupOverviewTable(overviewFieldTable);
        fieldOverviewPanel.add(new JScrollPane(overviewFieldTable), BorderLayout.CENTER);
        overviewPanel.add(fieldOverviewPanel);

        // Overview Jadwal
        JPanel scheduleOverviewPanel = createTableOverviewPanel("Jadwal Aktif Terdekat");
        overviewScheduleTableModel = new DefaultTableModel(new String[]{"ID", "Lapangan", "Hari", "Jam Mulai", "Jam Selesai"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        overviewScheduleTable = new JTable(overviewScheduleTableModel);
        setupOverviewTable(overviewScheduleTable);
        scheduleOverviewPanel.add(new JScrollPane(overviewScheduleTable), BorderLayout.CENTER);
        overviewPanel.add(scheduleOverviewPanel);

        // Overview Pemesanan (Terbaru Pending/Dikonfirmasi)
        JPanel bookingOverviewPanel = createTableOverviewPanel("Pemesanan Terbaru (Pending/Dikonfirmasi)");
        overviewBookingTableModel = new DefaultTableModel(new String[]{"ID Pemesanan", "Pengguna", "Lapangan", "Tanggal", "Jam", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        overviewBookingTable = new JTable(overviewBookingTableModel);
        setupOverviewTable(overviewBookingTable);
        bookingOverviewPanel.add(new JScrollPane(overviewBookingTable), BorderLayout.CENTER);
        overviewPanel.add(bookingOverviewPanel);

        loadOverviewData(); // Muat data awal
        return overviewPanel;
    }

    // Metode bantuan untuk membuat panel overview tabel
    private JPanel createTableOverviewPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2), // Border biru, tebal 2px
            title, TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 18), new Color(0, 51, 102) // Judul biru gelap
        ));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    // Metode bantuan untuk mengatur styling tabel overview
    private void setupOverviewTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28); // Tinggi baris
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(230, 240, 255)); // Header lebih terang
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(200, 200, 200)); // Garis grid lebih terang
        table.setSelectionBackground(new Color(173, 216, 230)); // Seleksi biru muda
        table.setSelectionForeground(Color.BLACK);
    }

    // Metode untuk memuat data ke tabel overview
    public void loadOverviewData() {
        // Muat Lapangan Aktif
        overviewFieldTableModel.setRowCount(0);
        List<Field> activeFields = fieldController.getAllFields(false);
        for (Field field : activeFields) {
            overviewFieldTableModel.addRow(new Object[]{
                field.getFieldId(),
                field.getName(),
                field.getType(),
                "Rp " + field.getPricePerHour().toPlainString()
            });
        }

        // Muat Jadwal Aktif
        overviewScheduleTableModel.setRowCount(0);
        List<Schedule> activeSchedules = scheduleController.getAllSchedules(false);
        for (Schedule schedule : activeSchedules) {
            overviewScheduleTableModel.addRow(new Object[]{
                schedule.getScheduleId(),
                schedule.getFieldName(),
                schedule.getDayOfWeek(),
                timeFormat.format(schedule.getStartTime()),
                timeFormat.format(schedule.getEndTime())
            });
        }

        // Muat Pemesanan Terbaru (misal 10 pemesanan pending/dikonfirmasi terakhir)
        overviewBookingTableModel.setRowCount(0);
        List<Booking> recentBookings = bookingController.getAllBookings(false); // Ambil yang aktif/tidak dihapus
        // Filter berdasarkan status jika perlu, atau urutkan berdasarkan tanggal dan batasi
        recentBookings.stream()
                .filter(b -> "Pending".equals(b.getStatus()) || "Confirmed".equals(b.getStatus()))
                .limit(10) // Hanya tampilkan 10 yang terbaru
                .forEach(booking -> overviewBookingTableModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getUserName(),
                    booking.getFieldName(),
                    dateFormat.format(booking.getBookingDate()),
                    timeFormat.format(booking.getStartTime()) + "-" + timeFormat.format(booking.getEndTime()),
                    booking.getStatus()
                }));
    }
}
