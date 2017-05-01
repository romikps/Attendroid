package com.example.romanpr.attendroid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romanpr on 4/22/2017.
 */

public class Course {

    String courseName;
    String professorUid;
    
    List<ClassTime> schedule;
    List<String> studentUids;
    boolean isActive;

    public Course() {}

    public Course(String name, String professorUid) {
        this.courseName = name;
        this.professorUid = professorUid;
        schedule = new ArrayList<>();
        studentUids = new ArrayList<>();
        this.isActive = true;
    }

    public void addClassTime(DayOfWeek dayOfWeek, String startingTime, String endingTime) {
        schedule.add(new ClassTime(dayOfWeek, startingTime, endingTime));
    }

    public String getCourseName() {
        return courseName;
    }

    public String getProfessorUid() {
        return professorUid;
    }

    public List<ClassTime> getSchedule() {
        return schedule;
    }

    public List<String> getStudentUids() {
        return studentUids;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
