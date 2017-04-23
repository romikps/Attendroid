package com.example.romanpr.attendroid;

/**
 * Created by romanpr on 4/22/2017.
 */

public abstract class User {

    String firstName, lastName, email, uid;
    Role role;

    public User() {}

    public User(String firstName, String lastName, String email, String uid, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uid = uid;
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

    public String getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return
                "name: " + getFirstName() + " " + getLastName() + "\n"
                + "e-mail: " + getEmail() + "\n"
                + "uid: " + getUid() + "\n"
                + "role: " + getRole() + "\n";
    }
}
