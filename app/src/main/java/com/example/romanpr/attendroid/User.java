package com.example.romanpr.attendroid;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by romanpr on 4/22/2017.
 */

public abstract class User {

    String firstName, lastName, email, faculty, department;
    Role role;
    boolean isActive;
    Map<String, Double> attendanceData;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String lastName, String email, String faculty, String department, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.faculty = faculty;
        this.department = department;
        this.role = role;
        this.isActive = true;
        this.attendanceData = new HashMap<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role.name();
    }

    public String getEmail() {
        return email;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getDepartment() {
        return department;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Map<String, Double> getAttendanceData() {
        return attendanceData;
    }

    @Override
    public String toString() {
        return
                "name: " + getFirstName() + " " + getLastName() + "\n"
                        + "e-mail: " + getEmail() + "\n"
                        + "faculty: " + getFaculty() + "\n"
                        + "department: " + getDepartment() + "\n"
                        + "role: " + getRole() + "\n";
    }
}
