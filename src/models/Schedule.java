package models;

import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * Model untuk merepresentasikan data Jadwal.
 */
public class Schedule {
    private int scheduleId;
    private int fieldId;
    private Time startTime;
    private Time endTime;
    private String dayOfWeek;
    private boolean isDeleted;

    // Untuk Tampilan / Join
    private String fieldName;

    public Schedule() {}

    public Schedule(int scheduleId, int fieldId, Time startTime, Time endTime, boolean isDeleted) {
        this.scheduleId = scheduleId;
        this.fieldId = fieldId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.isDeleted = isDeleted;
    }

    // --- Getters and Setters ---
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }
    public int getFieldId() { return fieldId; }
    public void setFieldId(int fieldId) { this.fieldId = fieldId; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek;}
    
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(startTime) + " - " + sdf.format(endTime);
    }
}