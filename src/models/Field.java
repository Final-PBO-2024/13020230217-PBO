package models;

import java.math.BigDecimal;

public class Field {
    private int fieldId;
    private String name;
    private String type;
    private BigDecimal pricePerHour;
    private boolean isDeleted;

    public Field() {}

    public Field(int fieldId, String name, String type, BigDecimal pricePerHour, boolean isDeleted) {
        this.fieldId = fieldId;
        this.name = name;
        this.type = type;
        this.pricePerHour = pricePerHour;
        this.isDeleted = isDeleted;
    }

    // --- Getters and Setters ---
    public int getFieldId() { return fieldId; }
    public void setFieldId(int fieldId) { this.fieldId = fieldId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(BigDecimal pricePerHour) { this.pricePerHour = pricePerHour; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
