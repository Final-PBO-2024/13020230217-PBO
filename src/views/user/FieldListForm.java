package views.user;

import controllers.FieldController;
import models.Field;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class FieldListForm extends JFrame {

    private UserDashboardForm parentForm;
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
        setSize(800, 600);
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

        // Top: Title and Search
        JPanel topPanel = new JPanel(new BorderLayout(0, 5));
        JLabel titleLabel = new JLabel("Daftar Lapangan Tersedia", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Cari Lapangan (Nama/Tipe):"));
        searchField = new JTextField(25);
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center: Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama Lapangan", "Tipe", "Harga/Jam"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        fieldTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        fieldTable.setRowSorter(sorter);
        fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fieldTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(fieldTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        selectButton = new JButton("Pilih Lapangan & Lihat Jadwal");
        selectButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectButton.setEnabled(false); // Enable after selection
        backButton = new JButton("Kembali");
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

    private void loadFields() {
        List<Field> fields = fieldController.getAllFields(false); // Hanya tampilkan yg aktif
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
        parentForm.setVisible(true);
    }

    private void selectField() {
        int selectedRow = fieldTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = fieldTable.convertRowIndexToModel(selectedRow);
            int fieldId = (int) tableModel.getValueAt(modelRow, 0);
            Field selectedField = fieldController.getFieldById(fieldId);

            if (selectedField != null) {
                 // Buka Booking Form
                BookingForm bookingForm = new BookingForm(this, currentUser, selectedField);
                bookingForm.setVisible(true);
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mendapatkan detail lapangan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}