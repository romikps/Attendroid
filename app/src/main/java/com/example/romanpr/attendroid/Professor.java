package com.example.romanpr.attendroid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romanpr on 4/22/2017.
 */

public class Professor extends User {

    GPSLocation location;

    public Professor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Professor(String firstName, String lastName,
                   String email, String faculty, String department) {
        super(firstName, lastName, email, faculty, department, Role.Professor);
        this.location = new GPSLocation(0, 0);
    }

    public GPSLocation getLocation() {
        return location;
    }
}
