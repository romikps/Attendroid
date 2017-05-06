package com.example.romanpr.attendroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AdminCourseActivity extends AppCompatActivity {
    private AutoCompleteTextView tvProfessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_course);

        ArrayList<String> allProfessors = new ArrayList<>();
        allProfessors.addAll(Attendata.get(this).getAllProfessors().values());
        tvProfessor = (AutoCompleteTextView) findViewById(R.id.autoTeacher);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, allProfessors);
        tvProfessor.setAdapter(adapter);
    }

    public void addCourse(View v){
        //Add the course info to the database from the values in editTexts
        //Select a lecturer from spinner
        //if CheckBox.isChecked()
        //Add lecture name, lecturer, the value of the checked checkbox, the lecture hours corresponding to the checkbox to the lectures table
        //Also add lecture to the selected lecturer
        String courseName = ((EditText) findViewById(R.id.etCourseName)).getText().toString();
        CheckBox cbMonday = (CheckBox) findViewById(R.id.cbMon);
        CheckBox cbTuesday = (CheckBox) findViewById(R.id.cbTue);
        CheckBox cbWednesday = (CheckBox) findViewById(R.id.cbWed);
        CheckBox cbThursday = (CheckBox) findViewById(R.id.cbThu);
        CheckBox cbFriday = (CheckBox) findViewById(R.id.cbFri);

        List<ClassTime> classTimes = new ArrayList<>();
        if (!courseName.isEmpty()) {
            String startTime, finishTime;
            if (cbMonday.isChecked()) {
                startTime = ((EditText) findViewById(R.id.etMonStart)).getText().toString();
                finishTime = ((EditText) findViewById(R.id.etMonFinish)).getText().toString();
                classTimes.add(new ClassTime(DayOfWeek.Monday, startTime, finishTime));
            }
            if (cbTuesday.isChecked()) {
                startTime = ((EditText) findViewById(R.id.etTueStart)).getText().toString();
                finishTime = ((EditText) findViewById(R.id.etTueFinish)).getText().toString();
                classTimes.add(new ClassTime(DayOfWeek.Tuesday, startTime, finishTime));
            }
            if (cbWednesday.isChecked()) {
                startTime = ((EditText) findViewById(R.id.etWedStart)).getText().toString();
                finishTime = ((EditText) findViewById(R.id.etWedFinish)).getText().toString();
                classTimes.add(new ClassTime(DayOfWeek.Wednesday, startTime, finishTime));
            }
            if (cbThursday.isChecked()) {
                startTime = ((EditText) findViewById(R.id.etThuStart)).getText().toString();
                finishTime = ((EditText) findViewById(R.id.etThuFinish)).getText().toString();
                classTimes.add(new ClassTime(DayOfWeek.Thursday, startTime, finishTime));
            }
            if (cbFriday.isChecked()) {
                startTime = ((EditText) findViewById(R.id.etFriStart)).getText().toString();
                finishTime = ((EditText) findViewById(R.id.etFriFinish)).getText().toString();
                classTimes.add(new ClassTime(DayOfWeek.Friday, startTime, finishTime));
            }
        } else {
            Toast.makeText(this, R.string.invalid_fields, Toast.LENGTH_LONG).show();
        }
        String selectedProfessorId = Attendata.getKey(Attendata.get(this).getAllProfessors(),
                tvProfessor.getText().toString());
        int totalHours = Integer.parseInt(((EditText) findViewById(R.id.etTotalHours)).getText().toString());
        Attendata.get(this).createCourse(courseName, selectedProfessorId, totalHours, classTimes);
    }
}
