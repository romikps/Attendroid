package com.example.romanpr.attendroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userId = getIntent().getStringExtra("USER_ID");
        Attendata userData = Attendata.get(this, userId);

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
                student = dataSnapshot.getValue(Student.class);
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
        private Course course;
        private TextView tvCourseName;
        private TextView tvAttendanceStatus;
        private ProgressBar attendanceProgressBar;
        public CourseHolder(View itemView) {
            super(itemView);
            tvCourseName = (TextView) itemView.findViewById(R.id.course_name);
            tvAttendanceStatus = (TextView) itemView.findViewById(R.id.attendance_status);
            attendanceProgressBar = (ProgressBar) itemView.findViewById(R.id.attendance_progress_bar);
        }

        public void bindCourse(Course course) {
            this.course = course;
            int hoursAttended = student.getAttendanceData().get(course.getCourseId());
            tvCourseName.setText(course.getCourseName());
            tvAttendanceStatus.setText(hoursAttended + "/" + course.getTotalHours());
            attendanceProgressBar.setMax(course.getTotalHours());
            attendanceProgressBar.setProgress(hoursAttended);
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
                    .inflate(R.layout.list_item_course, parent, false);
            return new CourseHolder(view);
        }

        @Override
        public void onBindViewHolder(CourseHolder holder, int position) {
            Course course = courses.get(position);
            holder.bindCourse(course);
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
        String key = database.child("courses").push().getKey();
        Course course = new Course("Mobile Architectures", "xP9uppVBvGdSRlARvw8w8nfC2T83", 50, key);
        course.addClassTime(DayOfWeek.Monday, "9:40", "11:20");
        course.addClassTime(DayOfWeek.Thursday, "13:40", "15:20");
        database.child("courses").child(key).setValue(course);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_show_schedule:
                Intent intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_show_leader_board:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
