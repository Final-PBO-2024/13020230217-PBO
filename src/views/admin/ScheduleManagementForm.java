package views.admin;

import controllers.FieldController;
import controllers.ScheduleController;
import models.Field;
import models.Schedule;
import models.User; // Tambahkan import User untuk fallback di goBack()

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Time;
import java.text.ParseException; // Tetap ada jika ada parsing Date/Time dari string
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.event.ListSelectionEvent;

public class ScheduleManagementForm extends JFrame {

    private AdminDashboardForm parentForm;
    private ScheduleController scheduleController;
    private FieldController fieldController;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, restoreButton, backButton, clearButton;
    private JCheckBox showDeletedCheckBox;
    private JCheckBox statusActiveCheckBox; // Deklarasi JCheckBox untuk status aktif
    private JComboBox<Field> fieldComboBox;
    private JSpinner startTimeSpinner, endTimeSpinner;
    private JTextField searchField;
    private Schedule selectedSchedule = null;
    private TableRowSorter<DefaultTableModel> sorter;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private JComboBox<String> dayOfWeekComboBox;

    public ScheduleManagementForm(AdminDashboardForm parent) {
        this.parentForm = parent;
        this.scheduleController = new ScheduleController();
        this.fieldController = new FieldController();
        initComponents();
        setTitle("Manajemen Jadwal");
        setSize(1000, 750); // Ukuran sedikit lebih besar untuk lebih banyak ruang
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        // --- START MODIFIED ---
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Mulai dalam mode maksimal
        // --- END MODIFIED ---
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBack();
            }
        });
        loadFieldsToComboBox();
        loadSchedules();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)); // Jarak antar panel
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255)); // Alice Blue

        JPanel topPanel = new JPanel(new BorderLayout(10, 10)); // Jarak antar form dan search panel
        topPanel.setBackground(new Color(240, 248, 255));
        topPanel.add(createFormPanel(), BorderLayout.CENTER);
        topPanel.add(createSearchPanel(), BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        setupListeners();
        updateButtonStates();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2), "Detail Jadwal", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), new Color(0, 51, 102))); // Font judul border lebih besar
        formPanel.setBackground(new Color(255, 255, 255)); // Putih
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding lebih banyak
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Agar JComboBox dan JSpinner melebar
        gbc.weightx = 0.0; // Default untuk label, tidak melebar

        // Lapangan
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Pilih Lapangan:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; 
        gbc.weightx = 1.0; // ComboBox melebar
        fieldComboBox = new JComboBox<>(); 
        fieldComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font untuk ComboBox
        formPanel.add(fieldComboBox, gbc);

        // Hari
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; formPanel.add(new JLabel("Pilih Hari:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0; // ComboBox melebar
        dayOfWeekComboBox = new JComboBox<>(new String[]{
            "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"
        });
        dayOfWeekComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font untuk ComboBox
        formPanel.add(dayOfWeekComboBox, gbc);
        
        // Jam Mulai
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; formPanel.add(new JLabel("Jam Mulai (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; 
        gbc.weightx = 1.0; // Spinner melebar
        startTimeSpinner = createTimeSpinner(); 
        startTimeSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font untuk Spinner
        formPanel.add(startTimeSpinner, gbc);

        // Jam Selesai
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; formPanel.add(new JLabel("Jam Selesai (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; 
        gbc.weightx = 1.0; // Spinner melebar
        endTimeSpinner = createTimeSpinner(); 
        endTimeSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font untuk Spinner
        formPanel.add(endTimeSpinner, gbc);

        // Status Aktif
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0; formPanel.add(new JLabel("Status Aktif:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; 
        gbc.weightx = 1.0; // CheckBox melebar
        statusActiveCheckBox = new JCheckBox("Aktif");
        statusActiveCheckBox.setSelected(true); // Default untuk baru adalah aktif
        statusActiveCheckBox.setBackground(formPanel.getBackground());
        statusActiveCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font untuk CheckBox
        formPanel.add(statusActiveCheckBox, gbc);
        
        // Tombol Clear
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 5; // Menyesuaikan gridheight untuk 5 baris
        gbc.fill = GridBagConstraints.VERTICAL; gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0; // Tombol clear tidak perlu melebar
        clearButton = new JButton("Clear Form");
        clearButton.setBackground(new Color(255, 193, 7)); // Warna kuning
        clearButton.setForeground(Color.BLACK);
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Font tombol lebih besar
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setPreferredSize(new Dimension(150, 100)); // Ukuran tombol clear lebih besar
        formPanel.add(clearButton, gbc);

        return formPanel;
    }

    private JSpinner createTimeSpinner() {
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new java.util.Date()); // Atur default ke waktu sekarang
        return timeSpinner;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5)); // Jarak antar komponen
        searchPanel.setBackground(new Color(240, 248, 255));
        JLabel searchLabel = new JLabel("Cari Lapangan (Nama):");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font label
        searchPanel.add(searchLabel);
        searchField = new JTextField(30); // Ukuran lebih panjang
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font field
        searchPanel.add(searchField);
        showDeletedCheckBox = new JCheckBox("Tampilkan yang Dihapus");
        showDeletedCheckBox.setBackground(new Color(240, 248, 255));
        showDeletedCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font checkbox
        searchPanel.add(showDeletedCheckBox);
        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tableModel = new DefaultTableModel(new String[]{"ID", "Lapangan", "Hari", "Jam Mulai", "Jam Selesai", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        scheduleTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        scheduleTable.setRowSorter(sorter);
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Styling tabel
        scheduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scheduleTable.setRowHeight(28);
        scheduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        scheduleTable.getTableHeader().setBackground(new Color(230, 240, 255));
        scheduleTable.setFillsViewportHeight(true);
        scheduleTable.setGridColor(new Color(200, 200, 200));
        scheduleTable.setSelectionBackground(new Color(173, 216, 230));
        scheduleTable.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15)); // Jarak antar tombol lebih besar
        buttonPanel.setBackground(new Color(240, 248, 255));
        addButton = new JButton("Tambah Jadwal");
        editButton = new JButton("Edit Jadwal");
        deleteButton = new JButton("Hapus Jadwal");
        restoreButton = new JButton("Restore Jadwal");
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

        // Hapus border fokus dan atur kursor, atur font, atur ukuran
        JButton[] buttons = {addButton, editButton, deleteButton, restoreButton, backButton};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Font tombol lebih besar
            btn.setPreferredSize(new Dimension(200, 50)); // Ukuran tombol seragam lebih besar
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
        showDeletedCheckBox.addActionListener(e -> loadSchedules());
        addButton.addActionListener(e -> addSchedule());
        editButton.addActionListener(e -> editSchedule());
        deleteButton.addActionListener(e -> deleteSchedule());
        restoreButton.addActionListener(e -> restoreSchedule());

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        scheduleTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            if (!event.getValueIsAdjusting() && scheduleTable.getSelectedRow() != -1) {
                int modelRow = scheduleTable.convertRowIndexToModel(scheduleTable.getSelectedRow());
                int scheduleId = (int) tableModel.getValueAt(modelRow, 0);
                selectedSchedule = scheduleController.getScheduleById(scheduleId);
                if (selectedSchedule != null) {
                    populateForm(selectedSchedule);
                }
            } else if (scheduleTable.getSelectedRow() == -1) {
                selectedSchedule = null;
            }
            updateButtonStates();
        });
    }

    private void filterTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // Cari di kolom Lapangan
        }
    }


    private void loadFieldsToComboBox() {
        fieldComboBox.removeAllItems();
        List<Field> fields = fieldController.getAllFields(false); // Hanya tampilkan field aktif
        for (Field field : fields) {
            fieldComboBox.addItem(field);
        }
    }

    private void loadSchedules() {
        boolean showDeleted = showDeletedCheckBox.isSelected();
        List<Schedule> schedules = scheduleController.getAllSchedules(showDeleted);
        tableModel.setRowCount(0);

        for (Schedule schedule : schedules) {
            tableModel.addRow(new Object[]{
                schedule.getScheduleId(),
                schedule.getFieldName(),
                schedule.getDayOfWeek(),
                timeFormat.format(schedule.getStartTime()),
                timeFormat.format(schedule.getEndTime()),
                schedule.isDeleted() ? "Dihapus" : "Aktif"
            });
        }
        clearForm();
        updateButtonStates();
    }

    private void populateForm(Schedule schedule) {
        // Cari Field di ComboBox berdasarkan ID
        for (int i = 0; i < fieldComboBox.getItemCount(); i++) {
            if (fieldComboBox.getItemAt(i).getFieldId() == schedule.getFieldId()) {
                fieldComboBox.setSelectedIndex(i);
                break;
            }
        }
        dayOfWeekComboBox.setSelectedItem(schedule.getDayOfWeek());
        startTimeSpinner.setValue(schedule.getStartTime());
        endTimeSpinner.setValue(schedule.getEndTime());
        statusActiveCheckBox.setSelected(!schedule.isDeleted()); // Atur status aktif/tidak aktif
    }

    private void clearForm() {
        fieldComboBox.setSelectedIndex(-1);
        dayOfWeekComboBox.setSelectedIndex(-1);
        startTimeSpinner.setValue(new java.util.Date()); // Reset ke waktu sekarang
        endTimeSpinner.setValue(new java.util.Date());
        statusActiveCheckBox.setSelected(true); // Default untuk form kosong: aktif
        scheduleTable.clearSelection();
        selectedSchedule = null;
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean selected = (selectedSchedule != null);
        addButton.setEnabled(!selected);
        editButton.setEnabled(selected && !selectedSchedule.isDeleted());
        deleteButton.setEnabled(selected && !selectedSchedule.isDeleted());
        restoreButton.setEnabled(selected && selectedSchedule.isDeleted());
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

    private Schedule getScheduleFromForm() {
        Field selectedFieldItem = (Field) fieldComboBox.getSelectedItem();
        String selectedDayOfWeek = (String) dayOfWeekComboBox.getSelectedItem();        
        
        if (selectedFieldItem == null) {
            JOptionPane.showMessageDialog(this, "Pilih lapangan terlebih dahulu!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        if (selectedDayOfWeek == null || selectedDayOfWeek.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih hari untuk jadwal!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        java.util.Date startTimeUtil = (java.util.Date) startTimeSpinner.getValue();
        java.util.Date endTimeUtil = (java.util.Date) endTimeSpinner.getValue();

        Time startTime = new Time(startTimeUtil.getTime());
        Time endTime = new Time(endTimeUtil.getTime());

        if (startTime.after(endTime) || startTime.equals(endTime)) {
            JOptionPane.showMessageDialog(this, "Jam mulai harus sebelum jam selesai!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Schedule schedule = new Schedule();
        schedule.setFieldId(selectedFieldItem.getFieldId());
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setDayOfWeek(selectedDayOfWeek);
        schedule.setDeleted(!statusActiveCheckBox.isSelected()); // Ambil status dari checkbox

        if (selectedSchedule != null) {
            schedule.setScheduleId(selectedSchedule.getScheduleId());
        }
        return schedule;
    }

    private void addSchedule() {
        Schedule newSchedule = getScheduleFromForm();
        if (newSchedule != null) {
            if (scheduleController.addSchedule(newSchedule)) {
                JOptionPane.showMessageDialog(this, "Jadwal berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadSchedules();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan jadwal.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSchedule() {
        if (selectedSchedule == null) return;
        Schedule updatedSchedule = getScheduleFromForm();
        if (updatedSchedule != null) {
            // Pastikan ID dari jadwal yang dipilih digunakan untuk update
            updatedSchedule.setScheduleId(selectedSchedule.getScheduleId());
            if (scheduleController.updateSchedule(updatedSchedule)) {
                JOptionPane.showMessageDialog(this, "Jadwal berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadSchedules();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui jadwal.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSchedule() {
        if (selectedSchedule == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus jadwal ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (scheduleController.setScheduleDeletedStatus(selectedSchedule.getScheduleId(), true)) {
                JOptionPane.showMessageDialog(this, "Jadwal berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadSchedules();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus jadwal.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void restoreSchedule() {
        if (selectedSchedule == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin merestore jadwal ini?",
                "Konfirmasi Restore", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (scheduleController.setScheduleDeletedStatus(selectedSchedule.getScheduleId(), false)) {
                JOptionPane.showMessageDialog(this, "Jadwal berhasil direstore.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadSchedules();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal merestore jadwal.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
