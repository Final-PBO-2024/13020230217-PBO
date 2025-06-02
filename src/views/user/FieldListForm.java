package views.user;

import controllers.FieldController;
import models.Field;
import models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class FieldListForm extends JFrame {

    private UserDashboardForm parentForm; // Referensi ke UserDashboardForm
    private User currentUser;
    private FieldController fieldController;
    private JTable fieldTable;
    private DefaultTableModel tableModel;
    private JButton selectButton, backButton;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public FieldListForm(UserDashboardForm parent, User user) {
        this.parentForm = parent;
        this.currentUser = user;
        this.fieldController = new FieldController();
        initComponents();
        setTitle("Pilih Lapangan");
        setSize(900, 650); // Ukuran lebih besar
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBack();
            }
        });
        loadFields();
    }

    // Getter untuk parent form, dibutuhkan oleh BookingForm
    public UserDashboardForm getParentForm() {
        return parentForm;
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255)); // Alice Blue

        // Atas: Judul dan Pencarian
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(new Color(240, 248, 255));
        JLabel titleLabel = new JLabel("Daftar Lapangan Tersedia", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(0, 51, 102));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(new Color(240, 248, 255));
        searchPanel.add(new JLabel("Cari Lapangan (Nama/Tipe):"));
        searchField = new JTextField(30); // Lebih panjang
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Tengah: Tabel
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama Lapangan", "Tipe", "Harga/Jam"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        fieldTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        fieldTable.setRowSorter(sorter);
        fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Styling tabel
        fieldTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldTable.setRowHeight(28);
        fieldTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        fieldTable.getTableHeader().setBackground(new Color(230, 240, 255));
        fieldTable.setFillsViewportHeight(true);
        fieldTable.setGridColor(new Color(200, 200, 200));
        fieldTable.setSelectionBackground(new Color(173, 216, 230));
        fieldTable.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(fieldTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bawah: Tombol-tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        selectButton = new JButton("Pilih Lapangan & Lihat Jadwal");
        selectButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        selectButton.setBackground(new Color(0, 153, 51)); // Hijau
        selectButton.setForeground(Color.WHITE);
        selectButton.setFocusPainted(false);
        selectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectButton.setEnabled(false); // Default disabled
        selectButton.setPreferredSize(new Dimension(250, 45)); // Ukuran tombol seragam

        backButton = new JButton("Kembali ke Dashboard");
        backButton.setBackground(new Color(108, 117, 125)); // Abu-abu
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(200, 45));


        buttonPanel.add(backButton);
        buttonPanel.add(selectButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Listeners
        backButton.addActionListener(e -> goBack());
        selectButton.addActionListener(e -> selectField());

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        fieldTable.getSelectionModel().addListSelectionListener(e -> {
            selectButton.setEnabled(fieldTable.getSelectedRow() != -1);
        });
    }

    private void filterTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
        }
    }

    public void loadFields() { // Ubah aksesibilitas ke public agar bisa dipanggil dari BookingForm
        List<Field> fields = fieldController.getAllFields(false); // Hanya tampilkan yang aktif
        tableModel.setRowCount(0);

        for (Field field : fields) {
            tableModel.addRow(new Object[]{
                    field.getFieldId(),
                    field.getName(),
                    field.getType(),
                    "Rp " + field.getPricePerHour().toPlainString()
            });
        }
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

    private void selectField() {
        int selectedRow = fieldTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = fieldTable.convertRowIndexToModel(selectedRow);
            int fieldId = (int) tableModel.getValueAt(modelRow, 0);
            Field selectedField = fieldController.getFieldById(fieldId);

            if (selectedField != null) {
                 // Buka Form Pemesanan
                BookingForm bookingForm = new BookingForm(this, currentUser, selectedField);
                bookingForm.setVisible(true);
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mendapatkan detail lapangan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
