package com.example.romanpr.attendroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by romanpr on 4/22/2017.
 */

public class Course {

    String courseName;
    String professor;
    
    Map<String, ClassTime> schedule;
    Map<String, Boolean> students;
    int totalHours;
    boolean isActive;
    String courseId;

    public Course() {}

    public Course(String name, String professor, int totalHours, String courseId) {
        this.courseName = name;
        this.professor = professor;
        this.schedule = new HashMap<>();
        this.students = new HashMap<>();
        this.totalHours = totalHours;
        this.isActive = true;
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getProfessor() {
        return professor;
    }

    public Map<String, ClassTime> getSchedule() {
        return schedule;
    }

    public Map<String, Boolean> getStudents() {
        return students;
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

    public String getClassHours() {
        String strClassHours = "";
        List<ClassTime> classHours = new ArrayList<>();
        classHours.addAll(schedule.values());
        Collections.sort(classHours);
        for (ClassTime time : classHours) {
            strClassHours += time.toString() + "\n";
        }
        return strClassHours.trim();
    }
}
