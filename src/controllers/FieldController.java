package controllers;

import models.Field;
import repositories.FieldRepository;
import java.util.List;

public class FieldController {

    private FieldRepository fieldRepository;

    public FieldController() {
        this.fieldRepository = new FieldRepository();
    }

    public List<Field> getAllFields(boolean includeDeleted) {
        return fieldRepository.getAllFields(includeDeleted);
    }

    public Field getFieldById(int fieldId) {
        return fieldRepository.getFieldById(fieldId);
    }

    public boolean addField(Field field) {
        return fieldRepository.addField(field);
    }

    public boolean updateField(Field field) {
        return fieldRepository.updateField(field);
    }

    public boolean setFieldDeletedStatus(int fieldId, boolean isDeleted) {
        return fieldRepository.setFieldDeletedStatus(fieldId, isDeleted);
    }
}