package com.example.romanpr.attendroid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romanpr on 4/22/2017.
 */

public class Professor extends User {

    List<String> courseIds;

    public Professor(String firstName, String lastName,
                   String email, String uid, String faculty, String department) {
        super(firstName, lastName, email, uid, faculty, department, Role.Professor);
        this.courseIds = new ArrayList<>();
    }
}
