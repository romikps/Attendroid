package com.example.romanpr.attendroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, String> allCourses;
    private Map<String, String> allStudents;
    private Map<String, String> allProfessors;


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
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courses = new ArrayList<>();
                allCourses = new HashMap<>();
                allStudents = new HashMap<>();
                allProfessors = new HashMap<>();

                DataSnapshot dsStudents = dataSnapshot.child("students");
                DataSnapshot dsCourses = dataSnapshot.child("courses");
                DataSnapshot dsProfessors = dataSnapshot.child("professors");

                // Retrieving students
                Student student;
                for (DataSnapshot dsStudent : dsStudents.getChildren()) {
                    student = dsStudent.getValue(Student.class);
                    allStudents.put(dsStudent.getKey(),
                            student.firstName + " " + student.lastName);
                    if (dsStudent.getKey().equals(userId)) {
                        user = dsStudent.getValue(Student.class);
                    }
                }

                // Retrieving professors
                Professor professor;
                for (DataSnapshot dsProfessor : dsProfessors.getChildren()) {
                    professor = dsProfessor.getValue(Professor.class);
                    allProfessors.put(dsProfessor.getKey(),
                            professor.getFirstName() + " " + professor.getLastName());
                    if (dsProfessor.getKey().equals(userId)) {
                        user = dsProfessor.getValue(Student.class);
                    }
                }

                // Retrieving courses
                Course course;
                for (DataSnapshot dsCourse : dsCourses.getChildren()) {
                    course = dsCourse.getValue(Course.class);
                    allCourses.put(dsCourse.getKey(), course.getCourseName());
                    if (user.getCourses() != null
                            && user.getCourses().keySet().contains(dsCourse.getKey())) {
                        courses.add(course);
                    }
                }

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("USER_ID", userId);
                context.startActivity(intent);
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

    public Map<String, String> getAllCourses() {
        return allCourses;
    }

    public Map<String, String> getAllStudents() {
        return allStudents;
    }

    public Map<String, String> getAllProfessors() {
        return allProfessors;
    }

    public void addCourse(String courseId, String studentId) {
        // Simultaneous
        Map<String, Object> updates = new HashMap<>();
        updates.put("students/" + studentId + "/attendanceData/" + courseId, 0);
        updates.put("students/" + studentId + "/courses/" + courseId, true);
        updates.put("courses/" + courseId + "/students/" + studentId, true);
        database.updateChildren(updates);
    }

    public void dropCourse(String courseId, String studentId) {
        // Simultaneous
        Map<String, Object> updates = new HashMap<>();
        updates.put("students/" + studentId + "/attendanceData/" + courseId, null);
        updates.put("students/" + studentId + "/courses/" + courseId, null);
        updates.put("courses/" + courseId + "/students/" + studentId, null);
        database.updateChildren(updates);
    }

    public void createCourses() {
        String courseId = database.child("courses").push().getKey();
        Course course = new Course("Algorithms & Data Structures", "xP9uppVBvGdSRlARvw8w8nfC2T83", 50, courseId);
        database.child("courses").child(courseId).setValue(course);

        String dayOfWeek = "Friday", startingTime = "10:40", endingTime = "11:20";
        database.child("courses/" + courseId + "/schedule/")
                .push().setValue(new ClassTime(DayOfWeek.valueOf(dayOfWeek), startingTime, endingTime));

        dayOfWeek = "Thursday";
        startingTime = "13:40";
        endingTime = "15:30";

        database.child("courses/" + courseId + "/schedule/")
                .push().setValue(new ClassTime(DayOfWeek.valueOf(dayOfWeek), startingTime, endingTime));


    }
}
