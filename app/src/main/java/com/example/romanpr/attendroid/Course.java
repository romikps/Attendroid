package com.example.romanpr.attendroid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romanpr on 4/22/2017.
 */

public class Course {

    String courseName;
    String professorUid;
    List<ClassTime> hours;
    List<String> studentUids;

    public Course() {}

    public Course(String name, String professorUid) {
        this.courseName = name;
        this.professorUid = professorUid;
        hours = new ArrayList<>();
        studentUids = new ArrayList<>();
    }

    public void addCourseHours(DayOfWeek dayOfWeek, String startingTime, String endingTime) {
        hours.add(new ClassTime(dayOfWeek, startingTime, endingTime));
    }

    public String getCourseName() {
        return courseName;
    }

    public String getProfessorUid() {
        return professorUid;
    }

    public List<ClassTime> getHours() {
        return hours;
    }

    public List<String> getStudentUids() {
        return studentUids;
    }
}
