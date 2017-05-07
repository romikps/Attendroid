package com.example.romanpr.attendroid;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by romanpr on 4/22/2017.
 */

public class Student extends User {

    long studentId, lastAttendance;
    int points;
    Map<String, Integer> attendanceData;


    public Student() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Student(String firstName, String lastName,
                   String email, String faculty, String department, long studentId) {
        super(firstName, lastName, email, faculty, department, Role.Student);
        this.studentId = studentId;
        this.points = 0;
        this.attendanceData = new HashMap<>();
    }

    public long getStudentId() {
        return studentId;
    }

    public int getPoints() {
        return points;
    }

    public long getLastAttendance() {
        return lastAttendance;
    }

    public Map<String, Integer> getAttendanceData() {
        return attendanceData;
    }

    @Override
    public String toString() {
        return super.toString()
                + "student ID: " + getStudentId();
    }
}
