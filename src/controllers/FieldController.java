package controllers;

import models.Field;
import repositories.FieldRepository;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.List;

public class FieldController {
    private FieldRepository fieldRepository;

    public FieldController() {
        this.fieldRepository = new FieldRepository();
    }

    public void addField(Field field) {
        try {
            fieldRepository.save(field);
            JOptionPane.showMessageDialog(null, "Field added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding field: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateField(Field field) {
        try {
            fieldRepository.update(field);
            JOptionPane.showMessageDialog(null, "Field updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating field: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteField(int fieldId) {
        try {
            fieldRepository.delete(fieldId);
            JOptionPane.showMessageDialog(null, "Field deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting field: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Field> getAllFields() {
        try {
            return fieldRepository.findAll();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching fields: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public Field getFieldById(int fieldId) {
        try {
            return fieldRepository.findById(fieldId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching field: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}