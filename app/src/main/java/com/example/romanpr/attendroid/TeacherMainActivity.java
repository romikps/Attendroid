package com.example.romanpr.attendroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TeacherMainActivity extends AppCompatActivity {

    private static final String TAG = "TeacherMainActivity";
    TextView time_remain;
    TextView time_text;
    Switch take_attend_switch;
    Spinner spinner;
    Button student_list;
    CountDownTimer count;
    Professor professor;
    List<String> professorCourses;
    String selectedCourseId;
    Attendata profData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        this.setTitle("Professor Dashboard");

        profData = Attendata.get(this);
        professor = (Professor) profData.getUser();

        spinner = (Spinner) findViewById(R.id.spinner);
        student_list = (Button) findViewById(R.id.student_list);
        take_attend_switch = (Switch) findViewById(R.id.take_attendance);
        take_attend_switch.setChecked(false);
        time_remain = (TextView) findViewById(R.id.time_remain);
        time_text = (TextView) findViewById(R.id.time_text);
        time_remain.setVisibility(View.INVISIBLE);
        time_text.setVisibility(View.INVISIBLE);

        String welcomeMssg = getString(R.string.welcome_format, professor.getFirstName() + " " + professor.getLastName());
        TextView tvWelcomeMssg = (TextView) findViewById(R.id.welcomeText);
        tvWelcomeMssg.setText(welcomeMssg);
        //DATABASE
        //Spinner includes courses which are given by an instructor. Teacher's ID or full name will match
        //in the database(login part) and spinner shows courses according to the teacher.
        count = new CountDownTimer(10000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                time_remain.setText(String.format("%d min %d sec",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                time_remain.setText("Time is up!");
                take_attend_switch.setEnabled(false);
                profData.storeProfessorLocation(new GPSLocation(0, 0));
                profData.setTakingAttendance(selectedCourseId, false);
            }
        };

        //attach a listener to check for changes in state
        take_attend_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(!isChecked){
                    time_remain.setVisibility(View.INVISIBLE);
                    time_text.setVisibility(View.INVISIBLE);
                    // Clear professor's location
                    profData.storeProfessorLocation(new GPSLocation(0, 0));
                    profData.setTakingAttendance(selectedCourseId, false);
                }
                else {
                    time_remain.setVisibility(View.VISIBLE);
                    time_text.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Trying to access location");
                    GPSLocation profLocation = GPSLocation.getLocation(TeacherMainActivity.this);
                    if (profLocation != null) {
                        profData.storeProfessorLocation(profLocation);
                        profData.setTakingAttendance(selectedCourseId, true);
                        count.start();
                    }
                }
            }
        });

        professorCourses = getProfessorCourses();
        professorCourses.add(0, "Please choose a course");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, professorCourses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCourse = professorCourses.get(position);
                    selectedCourseId = Attendata.getKey(profData.getAllCourses(), selectedCourse);
                    take_attend_switch.setChecked(false);
                    take_attend_switch.setEnabled(true);
                    student_list.setEnabled(true);
                } else {
                    take_attend_switch.setChecked(false);
                    take_attend_switch.setEnabled(false);
                    student_list.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void showStudentList(View v){
        Intent intent = new Intent(this, TeacherCourseActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_main, menu);
        return true;
    }

    public List<String> getProfessorCourses() {
        ArrayList<String> professorCourses = new ArrayList<>();
        for (Course course : profData.getCourses()) {
            professorCourses.add(course.getCourseName());
        }
        return professorCourses;
    }
}
