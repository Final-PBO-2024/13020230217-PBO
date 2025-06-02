package views.user;

import models.User;
import views.auth.LoginForm;
import controllers.FieldController;
import controllers.BookingController;
import models.Field;
import models.Booking;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class UserDashboardForm extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Komponen overview
    private JTable overviewAvailableFieldTable;
    private DefaultTableModel overviewAvailableFieldTableModel;
    private JTable overviewMyBookingTable;
    private DefaultTableModel overviewMyBookingTableModel;

    private FieldController fieldController;
    private BookingController bookingController;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public UserDashboardForm(User user) {
        this.currentUser = user;
        this.fieldController = new FieldController();
        this.bookingController = new BookingController();
        initComponents();
        setTitle("User Dashboard - " + currentUser.getName());
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
//        showOverviewPanel(); // Tampilkan overview saat pertama kali masuk
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(230, 245, 255)); // Latar belakang biru muda

        // Panel Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 255, 255));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(173, 216, 230)));

        JLabel headerLabel = new JLabel("SELAMAT DATANG DI SPORT BOOKING", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerLabel.setForeground(new Color(25, 25, 112));
        
        JLabel welcomeLabel = new JLabel("Halo, " + currentUser.getName() + "!", SwingConstants.RIGHT);
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
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        sidebarPanel.setBackground(new Color(173, 216, 230));
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));

        JLabel menuTitle = new JLabel("MENU NAVIGASI");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        menuTitle.setForeground(new Color(25, 25, 112));
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(menuTitle);
        sidebarPanel.add(Box.createVerticalStrut(30));

        JButton selectFieldButton = createSidebarButton("Pilih Lapangan");
        JButton myBookingsButton = createSidebarButton("Pemesanan Saya");
        JButton profileButton = createSidebarButton("Profil Saya");

        sidebarPanel.add(selectFieldButton);
        sidebarPanel.add(myBookingsButton);
        sidebarPanel.add(profileButton);
        sidebarPanel.add(Box.createVerticalGlue());
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Panel Konten (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(new Color(255, 255, 255));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Tambahkan Panel Overview ke contentPanel
        contentPanel.add(createOverviewPanel(), "overview");
        
        // Action Listeners untuk tombol sidebar
        selectFieldButton.addActionListener(e -> openUserForm(new FieldListForm(this, currentUser)));
        myBookingsButton.addActionListener(e -> openUserForm(new BookingHistoryForm(this, currentUser))); // Mengganti UserBookingForm
        profileButton.addActionListener(e -> openUserForm(new UserProfileForm(this, currentUser))); // Asumsi ada UserProfileForm

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

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }

    private void openUserForm(JFrame form) {
        form.setVisible(true);
        this.setVisible(false);
    }

    public void showUserDashboard() {
        this.setVisible(true);
        loadOverviewData();
        cardLayout.show(contentPanel, "overview");
    }

    // --- Panel Overview ---
    private JPanel createOverviewPanel() {
        JPanel overviewPanel = new JPanel(new GridLayout(2, 1, 15, 15)); // 2 baris untuk Lapangan Tersedia & Pemesanan Saya
        overviewPanel.setBackground(Color.WHITE);

        // Overview Lapangan Tersedia
        JPanel availableFieldOverviewPanel = createTableOverviewPanel("Lapangan Tersedia Saat Ini");
        overviewAvailableFieldTableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Tipe", "Harga/Jam"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        overviewAvailableFieldTable = new JTable(overviewAvailableFieldTableModel);
        setupOverviewTable(overviewAvailableFieldTable);
        availableFieldOverviewPanel.add(new JScrollPane(overviewAvailableFieldTable), BorderLayout.CENTER);
        overviewPanel.add(availableFieldOverviewPanel);

        // Overview Pemesanan Saya
        JPanel myBookingOverviewPanel = createTableOverviewPanel("Pemesanan Saya");
        overviewMyBookingTableModel = new DefaultTableModel(new String[]{"ID Pemesanan", "Lapangan", "Tanggal", "Jam", "Harga", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        overviewMyBookingTable = new JTable(overviewMyBookingTableModel);
        setupOverviewTable(overviewMyBookingTable);
        myBookingOverviewPanel.add(new JScrollPane(overviewMyBookingTable), BorderLayout.CENTER);
        overviewPanel.add(myBookingOverviewPanel);

        loadOverviewData();
        return overviewPanel;
    }

    // Metode bantuan untuk membuat panel overview tabel
    private JPanel createTableOverviewPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            title, TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 18), new Color(0, 51, 102)
        ));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    // Metode bantuan untuk mengatur styling tabel overview
    private void setupOverviewTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(230, 240, 255));
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(200, 200, 200));
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.BLACK);
    }

    public void loadOverviewData() {
        // Muat Lapangan Tersedia (hanya yang aktif)
        overviewAvailableFieldTableModel.setRowCount(0);
        List<Field> availableFields = fieldController.getAllFields(false);
        for (Field field : availableFields) {
            overviewAvailableFieldTableModel.addRow(new Object[]{
                field.getFieldId(),
                field.getName(),
                field.getType(),
                "Rp " + field.getPricePerHour().toPlainString()
            });
        }

        // Muat Pemesanan Saya (untuk pengguna saat ini, aktif/tidak dihapus)
        overviewMyBookingTableModel.setRowCount(0);
        List<Booking> myBookings = bookingController.getBookingsByUserId(currentUser.getUserId(), false);
        for (Booking booking : myBookings) {
            overviewMyBookingTableModel.addRow(new Object[]{
                booking.getBookingId(),
                booking.getFieldName(),
                dateFormat.format(booking.getBookingDate()),
                timeFormat.format(booking.getStartTime()) + "-" + timeFormat.format(booking.getEndTime()),
                "Rp " + booking.getTotalPrice().toPlainString(),
                booking.getStatus()
            });
        }
    }
}
