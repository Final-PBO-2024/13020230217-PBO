package models;

public class Field {
    private int fieldId;
    private String name;
    private String sportType;
    private int capacity;
    private boolean isDeleted;

    public Field() {}

    public Field(String name, String sportType, int capacity) {
        this.name = name;
        this.sportType = sportType;
        this.capacity = capacity;
        this.isDeleted = false;
    }

    // Getters and Setters
    public int getFieldId() { return fieldId; }
    public void setFieldId(int fieldId) { this.fieldId = fieldId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
}