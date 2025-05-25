package views.admin;

import controllers.FieldController;
import models.Field;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FieldManagementForm extends JFrame {
    private JTextField txtName;
    private JTextField txtSportType;
    private JTextField txtCapacity;
    private JComboBox<Field> cbFields;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnBack;
    private FieldController fieldController;

    public FieldManagementForm() {
        fieldController = new FieldController();
        setTitle("Field Management");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(20, 20, 80, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(100, 20, 260, 25);
        add(txtName);

        JLabel lblSportType = new JLabel("Sport Type:");
        lblSportType.setBounds(20, 50, 80, 25);
        add(lblSportType);

        txtSportType = new JTextField();
        txtSportType.setBounds(100, 50, 260, 25);
        add(txtSportType);

        JLabel lblCapacity = new JLabel("Capacity:");
        lblCapacity.setBounds(20, 80, 80, 25);
        add(lblCapacity);

        txtCapacity = new JTextField();
        txtCapacity.setBounds(100, 80, 260, 25);
        add(txtCapacity);

        JLabel lblSelectField = new JLabel("Select Field:");
        lblSelectField.setBounds(20, 110, 80, 25);
        add(lblSelectField);

        cbFields = new JComboBox<>();
        cbFields.setBounds(100, 110, 260, 25);
        add(cbFields);

        btnAdd = new JButton("Add");
        btnAdd.setBounds(20, 150, 80, 25);
        add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(110, 150, 80, 25);
        add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(200, 150, 80, 25);
        add(btnDelete);

        btnBack = new JButton("Back");
        btnBack.setBounds(290, 150, 80, 25);
        add(btnBack);

        refreshFieldList();

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Field field = new Field();
                    field.setName(txtName.getText());
                    field.setSportType(txtSportType.getText());
                    field.setCapacity(Integer.parseInt(txtCapacity.getText()));
                    fieldController.addField(field);
                    refreshFieldList();
                    clearFields();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid capacity", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Field selectedField = (Field) cbFields.getSelectedItem();
                if (selectedField != null) {
                    try {
                        selectedField.setName(txtName.getText());
                        selectedField.setSportType(txtSportType.getText());
                        selectedField.setCapacity(Integer.parseInt(txtCapacity.getText()));
                        fieldController.updateField(selectedField);
                        refreshFieldList();
                        clearFields();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid capacity", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Field selectedField = (Field) cbFields.getSelectedItem();
                if (selectedField != null) {
                    fieldController.deleteField(selectedField.getFieldId());
                    refreshFieldList();
                    clearFields();
                }
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        cbFields.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Field selectedField = (Field) cbFields.getSelectedItem();
                if (selectedField != null) {
                    txtName.setText(selectedField.getName());
                    txtSportType.setText(selectedField.getSportType());
                    txtCapacity.setText(String.valueOf(selectedField.getCapacity()));
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

    private void clearFields() {
        txtName.setText("");
        txtSportType.setText("");
        txtCapacity.setText("");
    }

    @Override
    public String toString() {
        return "FieldManagementForm";
    }
}