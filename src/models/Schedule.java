package models;

import java.time.LocalDateTime;

public class Schedule {
    private int scheduleId;
    private int fieldId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isAvailable;

    public Schedule() {}

    public Schedule(int fieldId, LocalDateTime startTime, LocalDateTime endTime, boolean isAvailable) {
        this.fieldId = fieldId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }
    public int getFieldId() { return fieldId; }
    public void setFieldId(int fieldId) { this.fieldId = fieldId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}