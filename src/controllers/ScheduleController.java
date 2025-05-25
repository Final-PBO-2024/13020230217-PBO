package controllers;

import models.Schedule;
import repositories.ScheduleRepository;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.List;

public class ScheduleController {
    private ScheduleRepository scheduleRepository;

    public ScheduleController() {
        this.scheduleRepository = new ScheduleRepository();
    }

    public void addSchedule(Schedule schedule) {
        try {
            scheduleRepository.save(schedule);
            JOptionPane.showMessageDialog(null, "Schedule added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding schedule: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateSchedule(Schedule schedule) {
        try {
            scheduleRepository.update(schedule);
            JOptionPane.showMessageDialog(null, "Schedule updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating schedule: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Schedule> getSchedulesByFieldId(int fieldId) {
        try {
            return scheduleRepository.findByFieldId(fieldId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching schedules: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}