package com.example.romanpr.attendroid;

/**
 * Created by romanpr on 4/22/2017.
 */

public abstract class User {

    String firstName, lastName, email, faculty, department;
    Role role;

    public User() {}

    public User(String firstName, String lastName, String email, String faculty, String department, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.faculty = faculty;
        this.department = department;
        this.role = role;
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
