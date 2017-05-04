package com.example.romanpr.attendroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    RecyclerView courseRecyclerView;
    CourseAdapter adapter;
    Attendata userData;
    Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userId = getIntent().getStringExtra("USER_ID");
        userData = Attendata.get(this, userId);
        student = (Student) userData.getUser();

        courseRecyclerView = (RecyclerView) findViewById(R.id.course_recycler_view);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView tvStudentName = (TextView) findViewById(R.id.student_name);
        TextView tvStudentNumber = (TextView) findViewById(R.id.student_number);
        TextView tvTotalPoints = (TextView) findViewById(R.id.total_points);
        tvStudentName.setText(student.getFirstName() + " " + student.getLastName());
        tvStudentNumber.setText(Long.toString(student.getStudentId()));
        String points = getString(R.string.points_format, student.getPoints());
        tvTotalPoints.setText(points);

        adapter = new CourseAdapter(userData.getCourses());
        courseRecyclerView.setAdapter(adapter);
    }

    private class CourseHolder extends RecyclerView.ViewHolder {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.student_main, menu);
        return true;
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
