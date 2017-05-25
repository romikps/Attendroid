package com.example.romanpr.attendroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private User user;
    private List<Course> courses;
    private Map<String, String> allCourses;
    private Map<String, String> allStudents;
    private Map<String, String> allProfessors;
    // private static String userId;
    private String userId;
    private FirebaseAuth mAuth;
    private boolean start;

    public static Attendata get(Context context) {
        Log.d(TAG, "Inside Attendata");
        if (data == null) {
            Log.d(TAG, "Creating new Attendata");
            data = new Attendata(context);
        }
        return data;
    }

    private Attendata(final Context context) {
        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        start = true;
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data changed");
                courses = new ArrayList<>();
                allCourses = new HashMap<>();
                allStudents = new HashMap<>();
                allProfessors = new HashMap<>();
                userId = mAuth.getCurrentUser().getUid();

                DataSnapshot dsStudents = dataSnapshot.child("students");
                DataSnapshot dsCourses = dataSnapshot.child("courses");
                DataSnapshot dsProfessors = dataSnapshot.child("professors");

                // Retrieving students
                Student student;
                for (DataSnapshot dsStudent : dsStudents.getChildren()) {
                    student = dsStudent.getValue(Student.class);
                    allStudents.put(dsStudent.getKey(),
                            student.getFirstName() + " " + student.getLastName() + " " + student.getStudentId());
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
                        user = dsProfessor.getValue(Professor.class);
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


                    if (start) {
                        Intent intent = new Intent();
                        //TeacherMainActivity.class
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        start = false;
                        switch (Role.valueOf(user.getRole())) {
                            case Student:
                                intent.setClass(context, MainActivity.class);
                                break;
                            case Professor:
                                intent.setClass(context, TeacherMainActivity.class);
                                break;
                            case Admin:
                                intent.setClass(context, AdminMainActivity.class);
                                break;
                        }
                        context.startActivity(intent);
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log.v() Log.d() Log.i() Log.w() and Log.e() ERROR, WARN, INFO, DEBUG, VERBOSE
                // Failed to read value
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });
    }

    public DatabaseReference getDatabase() {
        return database;
    }

    /*public static void setUserId(String userId) {
        Attendata.userId = userId;
    }*/

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
        updates.put("students/" + studentId + "/courses/" + courseId, false);
        updates.put("courses/" + courseId + "/students/" + studentId, false);
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

    public void createCourse(String courseName, String professorId, int totalHours, List<ClassTime> classTimes) {
        String courseId = database.child("courses").push().getKey();
        Course course = new Course(courseName, professorId, totalHours, courseId);
        database.child("courses").child(courseId).setValue(course);

        for (ClassTime classTime : classTimes) {
            database.child("courses/" + courseId + "/schedule/")
                    .push().setValue(classTime);
        }
    }

    public static String getKey(Map<String, String> map, String value) {
        for (String key : map.keySet()) {
            if (map.get(key).equals(value)) {
                return key;
            }
        }
        return null;
    }

    public void storeProfessorLocation(GPSLocation location) {
        database.child("professors/" + userId + "/location/").setValue(location);
    }

    public void setTakingAttendance(String courseId, boolean isTakingAttendance) {
        database.child("courses/" + courseId + "/isTakingAttendance/").setValue(isTakingAttendance);
    }

    public void clearClassAttendance(String courseId) {
        Map<String, Object> updates = new HashMap<>();
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId) && course.getStudents() != null) {
                for (String studentId : course.getStudents().keySet()) {
                    updates.put(studentId, false);
                }
            }
        }
        database.child("courses/" + courseId + "/students/").updateChildren(updates);
    }

    public void submitAttendance(String courseId, String studentId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("courses/" + courseId + "/students/" + studentId, true);
        updates.put("students/" + studentId + "/attendanceData/" + courseId,
                ((Student) user).getAttendanceData().get(courseId) + 1);
        database.updateChildren(updates);
    }

    public void setLastAttendance(String userId, long mins) {
        database.child("students/" + userId + "/lastAttendance").setValue(mins);
    }

    public void signOut() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            clearAttendata();
            mAuth.signOut();
            Log.d(TAG, "Signed out: " + currentUser.getUid());
        } else {
            Log.d(TAG, "No one to sign out");
        }
    }

    public void clearAttendata() {
        this.data = null;
    }
}
