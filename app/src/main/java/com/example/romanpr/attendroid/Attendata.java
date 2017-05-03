package com.example.romanpr.attendroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by romanpr on 5/3/2017.
 */

public class Attendata {
    private static final String TAG = "Attendata";
    private static Attendata data;

    private DatabaseReference database;

    private String userId;
    private User user;
    private List<Course> courses;

    public static Attendata get(Context context, String userId) {
        if (data == null) {
            data = new Attendata(context, userId);
        }
        return data;
    }

    public static Attendata get(Context context) {
        return data;
    }

    private Attendata(final Context context, final String userId) {
        database = FirebaseDatabase.getInstance().getReference();
        database.child("students").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Student.class);
                database.child("courses").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        courses = new ArrayList<>();
                        for (DataSnapshot courseDataSnapshot : dataSnapshot.getChildren()) {
                            if (user.getAttendanceData().keySet().contains(courseDataSnapshot.getKey().toString())) {
                                courses.add(courseDataSnapshot.getValue(Course.class));
                            }
                        }

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("USER_ID", userId);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "Failed to read value", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log.v() Log.d() Log.i() Log.w() and Log.e() ERROR, WARN, INFO, DEBUG, VERBOSE
                // Failed to read value
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }

    public String getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public List<Course> getCourses() {
        return courses;
    }
}
