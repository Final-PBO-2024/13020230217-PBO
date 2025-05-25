package views.user;

import controllers.FieldController;
import models.Field;
import models.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FieldListForm extends JFrame {
    private JComboBox<Field> cbFields;
    private JButton btnBook;
    private JButton btnBack;
    private FieldController fieldController;
    private User user;

    public FieldListForm(User user) {
        this.user = user;
        fieldController = new FieldController();
        setTitle("Field List");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblSelectField = new JLabel("Select Field:");
        lblSelectField.setBounds(20, 20, 80, 25);
        add(lblSelectField);

        cbFields = new JComboBox<>();
        cbFields.setBounds(100, 20, 260, 25);
        add(cbFields);

        btnBook = new JButton("Book");
        btnBook.setBounds(20, 60, 100, 25);
        add(btnBook);

        btnBack = new JButton("Back");
        btnBack.setBounds(130, 60, 100, 25);
        add(btnBack);

        refreshFieldList();

        btnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Field selectedField = (Field) cbFields.getSelectedItem();
                if (selectedField != null) {
                    new BookingForm(user, selectedField).setVisible(true);
                }
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
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

    @Override
    public String toString() {
        return "FieldListForm";
    }
}