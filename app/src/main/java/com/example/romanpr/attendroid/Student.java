package com.example.romanpr.attendroid;

/**
 * Created by romanpr on 4/22/2017.
 */

public class Student extends User {

    long studentId;
    int points;

    public Student() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Student(String firstName, String lastName,
                   String email, String faculty, String department, long studentId) {
        super(firstName, lastName, email, faculty, department, Role.Student);
        this.studentId = studentId;
        this.points = 0;
    }

    public long getStudentId() {
        return studentId;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return super.toString()
                + "student ID: " + getStudentId();
    }
}
