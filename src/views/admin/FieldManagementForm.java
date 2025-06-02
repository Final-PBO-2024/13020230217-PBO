package views.admin;

import controllers.FieldController;
import models.Field;
import models.User; // Tambahkan import User untuk fallback di goBack()

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.event.ListSelectionEvent;

public class FieldManagementForm extends JFrame {

    private AdminDashboardForm parentForm;
    private FieldController fieldController;
    private JTable fieldTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, restoreButton, backButton, clearButton;
    private JCheckBox showDeletedCheckBox;
    private JCheckBox statusActiveCheckBox; // Deklarasi JCheckBox untuk status aktif
    private JTextField nameField, typeField, priceField, searchField;
    private Field selectedField = null;
    private TableRowSorter<DefaultTableModel> sorter;

    public FieldManagementForm(AdminDashboardForm parent) {
        this.parentForm = parent;
        this.fieldController = new FieldController();
        initComponents();
        setTitle("Manajemen Lapangan");
        setSize(950, 680); // Ukuran sedikit lebih besar
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

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255)); // Alice Blue

        // Panel Atas: Form & Pencarian
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(new Color(240, 248, 255));
        JPanel formPanel = createFormPanel();
        JPanel searchPanel = createSearchPanel();
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Panel Tengah: Tabel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Panel Bawah: Tombol-tombol
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setupListeners();
        updateButtonStates();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2), "Detail Lapangan", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(0, 51, 102)));
        formPanel.setBackground(new Color(255, 255, 255)); // Putih
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Lebih banyak padding
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label dan TextField
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nama Lapangan:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; nameField = new JTextField(25); formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Tipe Lapangan:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; typeField = new JTextField(25); formPanel.add(typeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Harga per Jam (Rp):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; priceField = new JTextField(25); formPanel.add(priceField, gbc);

        // Checkbox Status Aktif
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Status Aktif:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        statusActiveCheckBox = new JCheckBox("Aktif");
        statusActiveCheckBox.setSelected(true); // Default untuk baru adalah aktif
        statusActiveCheckBox.setBackground(formPanel.getBackground());
        formPanel.add(statusActiveCheckBox, gbc);

        // Tombol Clear
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 4; // Menyesuaikan gridheight
        gbc.fill = GridBagConstraints.VERTICAL; gbc.anchor = GridBagConstraints.CENTER;
        clearButton = new JButton("Clear Form");
        clearButton.setBackground(new Color(255, 193, 7)); // Warna kuning
        clearButton.setForeground(Color.BLACK);
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(clearButton, gbc);

        return formPanel;
    }

     private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Tambah jarak
        searchPanel.setBackground(new Color(240, 248, 255));
        searchPanel.add(new JLabel("Cari Lapangan:"));
        searchField = new JTextField(25); // Ukuran lebih panjang
        searchPanel.add(searchField);
        showDeletedCheckBox = new JCheckBox("Tampilkan yang Dihapus");
        showDeletedCheckBox.setBackground(new Color(240, 248, 255));
        searchPanel.add(showDeletedCheckBox);
        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE); // Latar belakang tabel panel
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Tipe", "Harga/Jam", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        fieldTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        fieldTable.setRowSorter(sorter);
        fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Styling tabel
        fieldTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldTable.setRowHeight(28); // Tinggi baris
        fieldTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        fieldTable.getTableHeader().setBackground(new Color(230, 240, 255));
        fieldTable.setFillsViewportHeight(true);
        fieldTable.setGridColor(new Color(200, 200, 200)); // Warna grid
        fieldTable.setSelectionBackground(new Color(173, 216, 230)); // Warna seleksi
        fieldTable.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(fieldTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2)); // Border untuk scroll pane
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

     private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Tambah jarak antar tombol
        buttonPanel.setBackground(new Color(240, 248, 255));
        addButton = new JButton("Tambah Lapangan");
        editButton = new JButton("Edit Lapangan");
        deleteButton = new JButton("Hapus Lapangan");
        restoreButton = new JButton("Restore Lapangan");
        backButton = new JButton("Kembali ke Dashboard");

        // Styling tombol
        addButton.setBackground(new Color(40, 167, 69)); // Hijau
        editButton.setBackground(new Color(0, 123, 255)); // Biru
        deleteButton.setBackground(new Color(220, 53, 69)); // Merah
        restoreButton.setBackground(new Color(23, 162, 184)); // Cyan
        backButton.setBackground(new Color(108, 117, 125)); // Abu-abu

        // Warna teks
        addButton.setForeground(Color.WHITE);
        editButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);
        restoreButton.setForeground(Color.WHITE);
        backButton.setForeground(Color.WHITE);

        // Hapus border fokus dan atur kursor, atur font
        JButton[] buttons = {addButton, editButton, deleteButton, restoreButton, backButton};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(180, 40)); // Ukuran tombol seragam
        }

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(restoreButton);
        buttonPanel.add(backButton);
        return buttonPanel;
     }

     private void setupListeners() {
        backButton.addActionListener(e -> goBack());
        clearButton.addActionListener(e -> clearForm());
        showDeletedCheckBox.addActionListener(e -> loadFields());
        addButton.addActionListener(e -> addField());
        editButton.addActionListener(e -> editField());
        deleteButton.addActionListener(e -> deleteField());
        restoreButton.addActionListener(e -> restoreField());

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        fieldTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            if (!event.getValueIsAdjusting() && fieldTable.getSelectedRow() != -1) {
                int modelRow = fieldTable.convertRowIndexToModel(fieldTable.getSelectedRow());
                int fieldId = (int) tableModel.getValueAt(modelRow, 0);
                selectedField = fieldController.getFieldById(fieldId);
                if (selectedField != null) {
                    populateForm(selectedField);
                }
            } else if (fieldTable.getSelectedRow() == -1) {
                 selectedField = null;
                 // Jangan kosongkan form secara otomatis, biarkan pengguna mengklik 'Clear'
            }
             updateButtonStates();
        });
     }

    private void filterTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            // Pencarian tidak case-sensitive pada kolom 1 (Nama) dan 2 (Tipe)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
        }
    }

    private void loadFields() {
        boolean showDeleted = showDeletedCheckBox.isSelected();
        List<Field> fields = fieldController.getAllFields(showDeleted);
        tableModel.setRowCount(0);

        for (Field field : fields) {
            tableModel.addRow(new Object[]{
                    field.getFieldId(),
                    field.getName(),
                    field.getType(),
                    "Rp " + field.getPricePerHour().toPlainString(), // Format harga
                    field.isDeleted() ? "Dihapus" : "Aktif"
            });
        }
        clearForm();
        updateButtonStates();
    }

    private void populateForm(Field field) {
        nameField.setText(field.getName());
        typeField.setText(field.getType());
        priceField.setText(field.getPricePerHour().toPlainString());
        statusActiveCheckBox.setSelected(!field.isDeleted()); // Atur status aktif/tidak aktif
    }

    private void clearForm() {
        nameField.setText("");
        typeField.setText("");
        priceField.setText("");
        statusActiveCheckBox.setSelected(true); // Default untuk form kosong: aktif
        fieldTable.clearSelection();
        selectedField = null;
        updateButtonStates();
        nameField.requestFocus();
    }

    private void updateButtonStates() {
        boolean selected = (selectedField != null);
        addButton.setEnabled(!selected);
        editButton.setEnabled(selected && !selectedField.isDeleted());
        deleteButton.setEnabled(selected && !selectedField.isDeleted());
        restoreButton.setEnabled(selected && selectedField.isDeleted());
    }

    private void goBack() {
        this.dispose();
        // Memanggil metode di parentForm untuk menampilkan kembali dashboard dan me-refresh
        if (parentForm != null) {
            parentForm.showAdminDashboard();
        } else {
            // Fallback jika parentForm null (seharusnya tidak terjadi jika dipanggil dengan benar)
            // Ini mungkin membutuhkan objek User yang valid.
            new AdminDashboardForm(new User()).setVisible(true); 
        }
    }

    private Field getFieldFromForm() {
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        String priceStr = priceField.getText().trim();

        if (name.isEmpty() || type.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            Field field = new Field();
            field.setName(name);
            field.setType(type);
            field.setPricePerHour(price);
            field.setDeleted(!statusActiveCheckBox.isSelected()); // Ambil status dari checkbox
            if (selectedField != null) {
                field.setFieldId(selectedField.getFieldId());
            }
            return field;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka yang valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void addField() {
        Field newField = getFieldFromForm();
        if (newField != null) {
            if (fieldController.addField(newField)) {
                JOptionPane.showMessageDialog(this, "Lapangan berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan lapangan.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editField() {
        if (selectedField == null) return;
        Field updatedField = getFieldFromForm();
        if (updatedField != null) {
            // Pastikan ID dari lapangan yang dipilih digunakan untuk update
            updatedField.setFieldId(selectedField.getFieldId());
            if (fieldController.updateField(updatedField)) {
                JOptionPane.showMessageDialog(this, "Lapangan berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui lapangan.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteField() {
        if (selectedField == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus lapangan '" + selectedField.getName() + "'?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (fieldController.setFieldDeletedStatus(selectedField.getFieldId(), true)) {
                JOptionPane.showMessageDialog(this, "Lapangan berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus lapangan.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void restoreField() {
        if (selectedField == null) return;
         int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin merestore lapangan '" + selectedField.getName() + "'?",
                "Konfirmasi Restore", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (fieldController.setFieldDeletedStatus(selectedField.getFieldId(), false)) {
                JOptionPane.showMessageDialog(this, "Lapangan berhasil direstore.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal merestore lapangan.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
