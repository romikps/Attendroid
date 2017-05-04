package com.example.romanpr.attendroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by romanpr on 4/22/2017.
 */

public abstract class User {

    String firstName, lastName, email, faculty, department, userId;
    Role role;
    boolean isActive;
    ArrayList<String> courses;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String lastName, String email,
                String faculty, String department, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.faculty = faculty;
        this.department = department;
        this.role = role;
        this.isActive = true;
        this.userId = "";
        this.courses = new ArrayList<>();
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getCourses() {
        return courses;
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
