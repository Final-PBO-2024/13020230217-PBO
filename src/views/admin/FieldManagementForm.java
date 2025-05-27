package views.admin;

import controllers.FieldController;
import models.Field;

import javax.swing.*;
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
    private JTextField nameField, typeField, priceField, searchField;
    private Field selectedField = null;
    private TableRowSorter<DefaultTableModel> sorter;

    public FieldManagementForm(AdminDashboardForm parent) {
        this.parentForm = parent;
        this.fieldController = new FieldController();
        initComponents();
        setTitle("Manajemen Lapangan");
        setSize(900, 650);
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel: Form & Search
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        JPanel formPanel = createFormPanel();
        JPanel searchPanel = createSearchPanel();
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel: Table
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Bottom Panel: Buttons
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setupListeners();
        updateButtonStates();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Detail Lapangan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; nameField = new JTextField(25); formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Tipe:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; typeField = new JTextField(25); formPanel.add(typeField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Harga/Jam:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; priceField = new JTextField(25); formPanel.add(priceField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        clearButton = new JButton("Clear");
        formPanel.add(clearButton, gbc);

        return formPanel;
    }

     private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Cari:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        showDeletedCheckBox = new JCheckBox("Tampilkan yang Dihapus");
        searchPanel.add(showDeletedCheckBox);
        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Tipe", "Harga/Jam", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        fieldTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        fieldTable.setRowSorter(sorter);
        fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(fieldTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

     private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Tambah");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Hapus");
        restoreButton = new JButton("Restore");
        backButton = new JButton("Kembali");

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
                 // Don't clear form automatically, let user click 'Clear'
            }
             updateButtonStates();
        });
     }

    private void filterTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            // Case-insensitive search on column 1 (Nama) and 2 (Tipe)
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
                    field.getPricePerHour(),
                    field.isDeleted() ? "Dihapus" : "Aktif"
            });
        }
        clearForm();
        updateButtonStates();
    }

    private void populateForm(Field field) {
        nameField.setText(field.getName());
        typeField.setText(field.getType());
        priceField.setText(field.getPricePerHour().toString());
    }

    private void clearForm() {
        nameField.setText("");
        typeField.setText("");
        priceField.setText("");
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
        parentForm.setVisible(true);
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