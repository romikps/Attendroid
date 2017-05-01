package com.example.romanpr.attendroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    DatabaseReference database;
    DatabaseReference studentData;
    RecyclerView courseRecyclerView;
    CourseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userId = getIntent().getStringExtra("USER_ID");
        courseRecyclerView = (RecyclerView) findViewById(R.id.course_recycler_view);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance().getReference();
        studentData = FirebaseDatabase.getInstance().getReference().child("students").child(userId);
        studentData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // Log.d(TAG, "Value is: " + dataSnapshot.getValue().toString());
                final Student student = dataSnapshot.getValue(Student.class);
                TextView tvStudentName = (TextView) findViewById(R.id.student_name);
                TextView tvStudentNumber = (TextView) findViewById(R.id.student_number);
                TextView tvTotalPoints = (TextView) findViewById(R.id.total_points);
                tvStudentName.setText(student.getFirstName() + " " + student.getLastName());
                tvStudentNumber.setText(Long.toString(student.getStudentId()));
                String points = getString(R.string.points_format, student.getPoints());
                tvTotalPoints.setText(points);

                database.child("courses").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Course> courses = new ArrayList<>();
                        for (DataSnapshot courseDataSnapshot : dataSnapshot.getChildren()) {
                            if (student.getAttendanceData().keySet().contains(courseDataSnapshot.getKey().toString())) {
                                courses.add(courseDataSnapshot.getValue(Course.class));
                            }
                        }
                        adapter = new CourseAdapter(courses);
                        courseRecyclerView.setAdapter(adapter);
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

    private class CourseHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public CourseHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView;
        }
    }

    private class CourseAdapter extends RecyclerView.Adapter<CourseHolder> {
        private List<Course> courses;
        public CourseAdapter(List<Course> courses) {
            this.courses = courses;
        }

        @Override
        public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new CourseHolder(view);
        }

        @Override
        public void onBindViewHolder(CourseHolder holder, int position) {
            Course course = courses.get(position);
            holder.titleTextView.setText(course.getCourseName());
        }

        @Override
        public int getItemCount() {
            return courses.size();
        }
    }

    public void addCourseToSchedule(String courseId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(courseId, 0);
        studentData.child("attendanceData").updateChildren(updates);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.student_main, menu);
        return true;
    }

    public void createCourses() {
        Course course = new Course("Mobile Architectures", "xP9uppVBvGdSRlARvw8w8nfC2T83");
        course.addClassTime(DayOfWeek.Monday, "9:40", "11:20");
        course.addClassTime(DayOfWeek.Thursday, "13:40", "15:20");
        database.child("courses").push().setValue(course);

        Course course2 = new Course("Web Development", "xP9uppVBvGdSRlARvw8w8nfC2T83");
        course.addClassTime(DayOfWeek.Monday, "9:40", "11:20");
        course.addClassTime(DayOfWeek.Thursday, "13:40", "15:20");
        database.child("courses").push().setValue(course);

    }
}
