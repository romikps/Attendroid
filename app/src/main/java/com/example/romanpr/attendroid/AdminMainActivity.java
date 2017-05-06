package com.example.romanpr.attendroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.*;

import java.util.ArrayList;
import java.util.Map;

public class AdminMainActivity extends AppCompatActivity {
    AutoCompleteTextView autoStudentName;
    AutoCompleteTextView autoCourseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        ArrayList<String> allStudents = new ArrayList<>();
        allStudents.addAll(Attendata.get(this).getAllStudents().values());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, allStudents);

        ArrayList<String> allCourses = new ArrayList<>();
        allCourses.addAll(Attendata.get(this).getAllCourses().values());

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, allCourses);

        autoStudentName = (AutoCompleteTextView)
                findViewById(R.id.tvStudentName);
        autoCourseName = (AutoCompleteTextView)
                findViewById(R.id.tvCourseName);

        autoCourseName.setAdapter(adapter2);
        autoStudentName.setAdapter(adapter);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getKey(Map<String, String> map, String value) {
        for (String key : map.keySet()) {
            if (map.get(key).equals(value)) {
                return key;
            }
        }
        return null;
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
        String selectedStudentId = getKey(Attendata.get(this).getAllStudents(), studentName);
        String selectedCourseId = getKey(Attendata.get(this).getAllCourses(), courseName);
        Attendata.get(this).addCourse(selectedCourseId, selectedStudentId);
    }

    public void removeLectureStudent(View v){
        //If student has the selected lecture
        //remove the lecture from student
        //else
        //error message
        String studentName = autoStudentName.getText().toString();
        String courseName = autoCourseName.getText().toString();
        String selectedStudentId = getKey(Attendata.get(this).getAllStudents(), studentName);
        String selectedCourseId = getKey(Attendata.get(this).getAllCourses(), courseName);
        Attendata.get(this).dropCourse(selectedCourseId, selectedStudentId);

    }
}