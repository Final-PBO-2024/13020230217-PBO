package views.admin;

import controllers.FieldController;
import controllers.ScheduleController;
import models.Field;
import models.Schedule;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScheduleManagementForm extends JFrame {
    private JComboBox<Field> cbFields;
    private JTextField txtStartTime;
    private JTextField txtEndTime;
    private JComboBox<Schedule> cbSchedules;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnBack;
    private FieldController fieldController;
    private ScheduleController scheduleController;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ScheduleManagementForm() {
        fieldController = new FieldController();
        scheduleController = new ScheduleController();
        setTitle("Schedule Management");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblField = new JLabel("Field:");
        lblField.setBounds(20, 20, 80, 25);
        add(lblField);

        cbFields = new JComboBox<>();
        cbFields.setBounds(100, 20, 260, 25);
        add(cbFields);

        JLabel lblStartTime = new JLabel("Start Time:");
        lblStartTime.setBounds(20, 50, 80, 25);
        add(lblStartTime);

        txtStartTime = new JTextField();
        txtStartTime.setBounds(100, 50, 260, 25);
        add(txtStartTime);

        JLabel lblEndTime = new JLabel("End Time:");
        lblEndTime.setBounds(20, 80, 80, 25);
        add(lblEndTime);

        txtEndTime = new JTextField();
        txtEndTime.setBounds(100, 80, 260, 25);
        add(txtEndTime);

        JLabel lblSelectSchedule = new JLabel("Select Schedule:");
        lblSelectSchedule.setBounds(20, 110, 100, 25);
        add(lblSelectSchedule);

        cbSchedules = new JComboBox<>();
        cbSchedules.setBounds(100, 110, 260, 25);
        add(cbSchedules);

        btnAdd = new JButton("Add");
        btnAdd.setBounds(20, 150, 80, 25);
        add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(110, 150, 80, 25);
        add(btnUpdate);

        btnBack = new JButton("Back");
        btnBack.setBounds(200, 150, 80, 25);
        add(btnBack);

        refreshFieldList();

        cbFields.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshScheduleList();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Field selectedField = (Field) cbFields.getSelectedItem();
                    if (selectedField != null) {
                        Schedule schedule = new Schedule();
                        schedule.setFieldId(selectedField.getFieldId());
                        schedule.setStartTime(LocalDateTime.parse(txtStartTime.getText(), formatter));
                        schedule.setEndTime(LocalDateTime.parse(txtEndTime.getText(), formatter));
                        schedule.setAvailable(true);
                        scheduleController.addSchedule(schedule);
                        refreshScheduleList();
                        clearFields();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Schedule selectedSchedule = (Schedule) cbSchedules.getSelectedItem();
                if (selectedSchedule != null) {
                    try {
                        Field selectedField = (Field) cbFields.getSelectedItem();
                        selectedSchedule.setFieldId(selectedField.getFieldId());
                        selectedSchedule.setStartTime(LocalDateTime.parse(txtStartTime.getText(), formatter));
                        selectedSchedule.setEndTime(LocalDateTime.parse(txtEndTime.getText(), formatter));
                        scheduleController.updateSchedule(selectedSchedule);
                        refreshScheduleList();
                        clearFields();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        cbSchedules.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Schedule selectedSchedule = (Schedule) cbSchedules.getSelectedItem();
                if (selectedSchedule != null) {
                    txtStartTime.setText(selectedSchedule.getStartTime().format(formatter));
                    txtEndTime.setText(selectedSchedule.getEndTime().format(formatter));
                }
            }
        });
    }

    private void refreshFieldList() {
        cbFields.removeAllItems();
        List<Field> fields = fieldController.getAllFields();
        if (fields != null) {
            for (Field field : fields) {
                cbFields.addItem(field);
            }
        }
    }

    private void refreshScheduleList() {
        cbSchedules.removeAllItems();
        Field selectedField = (Field) cbFields.getSelectedItem();
        if (selectedField != null) {
            List<Schedule> schedules = scheduleController.getSchedulesByFieldId(selectedField.getFieldId());
            if (schedules != null) {
                for (Schedule schedule : schedules) {
                    cbSchedules.addItem(schedule);
                }
            }
        }
    }

    private void clearFields() {
        txtStartTime.setText("");
        txtEndTime.setText("");
    }

    @Override
    public String toString() {
        return "ScheduleManagementForm";
    }
}