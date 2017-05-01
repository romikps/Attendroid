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
    int totalHours;
    boolean isActive;
    String courseId;

    public Course() {}

    public Course(String name, String professorUid, int totalHours, String courseId) {
        this.courseName = name;
        this.professorUid = professorUid;
        this.schedule = new ArrayList<>();
        this.studentUids = new ArrayList<>();
        this.totalHours = totalHours;
        this.isActive = true;
        this.courseId = courseId;
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

    public int getTotalHours() {
        return totalHours;
    }

    public String getCourseId() {
        return courseId;
    }
}
