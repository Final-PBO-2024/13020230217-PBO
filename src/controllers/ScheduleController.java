package controllers;

import models.Schedule;
import repositories.ScheduleRepository;
import java.util.List;

public class ScheduleController {

    private ScheduleRepository scheduleRepository;

    public ScheduleController() {
        this.scheduleRepository = new ScheduleRepository();
    }

    public List<Schedule> getAllSchedules(boolean includeDeleted) {
        return scheduleRepository.getAllSchedules(includeDeleted);
    }

     public List<Schedule> getSchedulesByFieldId(int fieldId, boolean includeDeleted) {
        return scheduleRepository.getSchedulesByFieldId(fieldId, includeDeleted);
     }

    public Schedule getScheduleById(int scheduleId) {
        return scheduleRepository.getScheduleById(scheduleId);
    }

    public boolean addSchedule(Schedule schedule) {
        return scheduleRepository.addSchedule(schedule);
    }

    public boolean updateSchedule(Schedule schedule) {
        return scheduleRepository.updateSchedule(schedule);
    }

    public boolean setScheduleDeletedStatus(int scheduleId, boolean isDeleted) {
        return scheduleRepository.setScheduleDeletedStatus(scheduleId, isDeleted);
    }
}