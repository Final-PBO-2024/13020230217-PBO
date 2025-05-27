package views.admin;

import controllers.FieldController;
import controllers.ScheduleController;
import models.Field;
import models.Schedule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Time;
import java.text.ParseException;
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
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
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
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Detail Jadwal"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Agar JComboBox dan JSpinner melebar

        // Lapangan
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Lapangan:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; fieldComboBox = new JComboBox<>(); formPanel.add(fieldComboBox, gbc);

        // Hari
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Hari:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        dayOfWeekComboBox = new JComboBox<>(new String[]{
            "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"
        });
        formPanel.add(dayOfWeekComboBox, gbc);
        
        // Jam Mulai
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Jam Mulai (HH:mm):"), gbc); // Adjusted gridy
        gbc.gridx = 1; gbc.gridy = 2; startTimeSpinner = createTimeSpinner(); formPanel.add(startTimeSpinner, gbc); // Adjusted gridy

        // Jam Selesai
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Jam Selesai (HH:mm):"), gbc); // Adjusted gridy
        gbc.gridx = 1; gbc.gridy = 3; endTimeSpinner = createTimeSpinner(); formPanel.add(endTimeSpinner, gbc); // Adjusted gridy

        // Clear button
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 4; // Adjusted gridheight
        gbc.fill = GridBagConstraints.VERTICAL; gbc.anchor = GridBagConstraints.CENTER;
        clearButton = new JButton("Clear");
        formPanel.add(clearButton, gbc);

        return formPanel;
    }

    private JSpinner createTimeSpinner() {
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new java.util.Date()); // Set default ke waktu sekarang
        return timeSpinner;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Cari (Lapangan):"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        showDeletedCheckBox = new JCheckBox("Tampilkan yang Dihapus");
        searchPanel.add(showDeletedCheckBox);
        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new String[]{"ID", "Lapangan", "Hari", "Jam Mulai", "Jam Selesai", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        scheduleTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        scheduleTable.setRowSorter(sorter);
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
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
    }

    private void clearForm() {
        fieldComboBox.setSelectedIndex(-1);
        dayOfWeekComboBox.setSelectedIndex(-1);
        startTimeSpinner.setValue(new java.util.Date()); // Reset ke waktu sekarang
        endTimeSpinner.setValue(new java.util.Date());
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
        parentForm.setVisible(true);
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