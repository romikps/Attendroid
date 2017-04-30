package com.example.romanpr.attendroid;

/**
 * Created by romanpr on 4/22/2017.
 */

public class Student extends User {

    long studentId;

    public Student() {}

    public Student(String firstName, String lastName,
                   String email, String uid, String faculty, String department, long studentId) {
        super(firstName, lastName, email, uid, faculty, department, Role.Student);
        this.studentId = studentId;
    }

    public long getStudentId() {
        return studentId;
    }

    @Override
    public String toString() {
        return super.toString()
                + "student ID: " + getStudentId();
    }
}
