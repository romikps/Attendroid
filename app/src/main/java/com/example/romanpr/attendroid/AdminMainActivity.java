package com.example.romanpr.attendroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {
    AutoCompleteTextView autoStudentName;
    AutoCompleteTextView autoCourseName;
    List<Student> mAllStudents;
    List<Course> mAllCourses;
    List<String> mStudentNames;
    List<String> mCourseNames;
    ArrayAdapter<String> studentAdapter;
    ArrayAdapter<String> courseAdapter;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        setTitle("Admin Dashboard");

        database = FirebaseDatabase.getInstance().getReference();
        mAllStudents = new ArrayList<>();
        mAllCourses = new ArrayList<>();
        mStudentNames = new ArrayList<>();
        mCourseNames = new ArrayList<>();

        studentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, mStudentNames);
        courseAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, mCourseNames);

        autoStudentName = (AutoCompleteTextView)
                findViewById(R.id.tvStudentName);
        autoCourseName = (AutoCompleteTextView)
                findViewById(R.id.tvCourseName);

        autoCourseName.setAdapter(courseAdapter);
        autoStudentName.setAdapter(studentAdapter);
    }

    String getStudentId(String studentName) {
        for (Student student : mAllStudents) {
            String currentStudentName = student.getFirstName() + " " + student.getLastName() + " ("
                    + student.getStudentId() + ")";
            if (currentStudentName.equals(studentName))
                return student.getUserId();
        }
        return null;
    }

    String getCourseId(String courseName) {
        for (Course course : mAllCourses) {
            if (course.getCourseName().equals(courseName)) {
                return course.getCourseId();
            }
        }
        return null;
    }



    @Override
    protected void onResume() {
        super.onResume();
        mAllStudents.clear();
        mAllCourses.clear();
        mStudentNames.clear();
        mCourseNames.clear();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dsStudents = dataSnapshot.child("students");
                DataSnapshot dsCourses = dataSnapshot.child("courses");
                // Retrieving students
                Student student;
                for (DataSnapshot dsStudent : dsStudents.getChildren()) {
                    student = dsStudent.getValue(Student.class);
                    mAllStudents.add(student);
                    mStudentNames.add(student.getFirstName() + " " + student.getLastName() + " ("
                    + student.getStudentId() + ")");
                }
                // Retrieving courses
                Course course;
                for (DataSnapshot dsCourse : dsCourses.getChildren()) {
                    course = dsCourse.getValue(Course.class);
                    mAllCourses.add(course);
                    mCourseNames.add(course.getCourseName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        studentAdapter.notifyDataSetChanged();
        courseAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addcourse:
                Intent intent = new Intent(this, AdminCourseActivity.class);
                startActivity(intent);
                return true;
            case R.id.log_out_menu_item:
                Attendata.get(this).signOut();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addLectureToStudent(View v){
        //If student doesn't have the lecture,
        // Check if there's any collision with the lecture hours
        //  if not
        // add the lecture to student
        //  else
        // error message
        //Else
        // error message saying they already have the lecture
        String studentName = autoStudentName.getText().toString();
        String courseName = autoCourseName.getText().toString();
        String selectedStudentId = getStudentId(studentName);
        String selectedCourseId = getCourseId(courseName);
        if (selectedStudentId.isEmpty() || selectedCourseId.isEmpty()) {
            Toast.makeText(this, "Selected course or student doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }
        Attendata.get(this).addCourse(selectedCourseId, selectedStudentId);
        Toast.makeText(this, studentName + " is successfully enrolled to " + courseName, Toast.LENGTH_SHORT).show();
    }

    public void removeLectureStudent(View v){
        //If student has the selected lecture
        //remove the lecture from student
        //else
        //error message
        String studentName = autoStudentName.getText().toString();
        String courseName = autoCourseName.getText().toString();
        String selectedStudentId = getStudentId(studentName);
        String selectedCourseId = getCourseId(courseName);
        if (selectedStudentId.isEmpty() || selectedCourseId.isEmpty()) {
            Toast.makeText(this, "Selected course or student doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }
        Attendata.get(this).dropCourse(selectedCourseId, selectedStudentId);
        Toast.makeText(this, studentName + " dropped " + courseName, Toast.LENGTH_SHORT).show();
    }
}