package com.example.romanpr.attendroid;

/**
 * Created by romanpr on 4/22/2017.
 */

public class Student extends User {

    String studentId;

    public Student() {}

    public Student(String firstName, String lastName,
                   String email, String uid, String studentId) {
        super(firstName, lastName, email, uid, Role.Student);
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public String toString() {
        return super.toString()
                + "student ID: " + getStudentId();
    }
}
