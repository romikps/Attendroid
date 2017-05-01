package com.example.romanpr.attendroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userId = getIntent().getStringExtra("USER_ID");
        database = FirebaseDatabase.getInstance().getReference().child("students").child(userId);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // Log.d(TAG, "Value is: " + dataSnapshot.getValue().toString());
                Student student = dataSnapshot.getValue(Student.class);
                TextView tvStudentName = (TextView) findViewById(R.id.student_name);
                TextView tvStudentNumber = (TextView) findViewById(R.id.student_number);
                TextView tvTotalPoints = (TextView) findViewById(R.id.total_points);
                tvStudentName.setText(student.getFirstName() + " " + student.getLastName());
                tvStudentNumber.setText(Long.toString(student.getStudentId()));
                tvTotalPoints.setText(Integer.toString(student.getPoints()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log.v() Log.d() Log.i() Log.w() and Log.e() ERROR, WARN, INFO, DEBUG, VERBOSE
                // Failed to read value
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.student_main, menu);
        return true;
    }

    protected void playAround() {
        /* Student stu = new Student("Roman", "Priscepov", "roman.priscepov@hotmail.com",
                user.getUid(), "130144607"); */
        /* Course course = new Course("Mobile Architectures", user.getUid());
        course.addCourseHours(DayOfWeek.Monday, "9:40", "11:20");
        course.addCourseHours(DayOfWeek.Thursday, "13:40", "15:20"); */
        // database.push().setValue(course);

    }
}
