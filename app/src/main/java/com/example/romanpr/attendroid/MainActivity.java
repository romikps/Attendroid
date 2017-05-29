package com.example.romanpr.attendroid;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final String KEY_LAST_ATTENDANCE_TIMESTAMP = "last_attendance_timestamp";
    RecyclerView courseRecyclerView;
    CourseAdapter adapter;
    Attendata userData;
    Student student;
    GPSLocation profLocation;
    Button attendanceBtn;
    String openCourseId;
    DatabaseReference database;
    ValueEventListener listener;
    TextView tvStudentName;
    TextView tvStudentNumber;
    TextView tvTotalPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userData = Attendata.get(this);
        student = (Student) userData.getUser();
        database = FirebaseDatabase.getInstance().getReference("students/" + student.getUserId());
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student = dataSnapshot.getValue(Student.class);
                updateStudent();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        database.addValueEventListener(listener);

        courseRecyclerView = (RecyclerView) findViewById(R.id.course_recycler_view);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendanceBtn = (Button) findViewById(R.id.attendance_button);
        attendanceBtn.setEnabled(false);

        tvStudentName = (TextView) findViewById(R.id.student_name);
        tvStudentNumber = (TextView) findViewById(R.id.student_number);
        tvTotalPoints = (TextView) findViewById(R.id.total_points);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location access permission not granted", Toast.LENGTH_SHORT).show();
            GPSLocation.requestLocationPermissions(this);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(this, "Your location: " + mLastLocation.getLatitude()
                    + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "The location is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void bindCourse(Course course) {
            int hoursAttended = student.getAttendanceData().get(course.getCourseId());
            tvCourseName.setText(course.getCourseName());
            tvAttendanceStatus.setText(hoursAttended + "/" + course.getTotalHours());
            attendanceProgressBar.setMax(course.getTotalHours());
            attendanceProgressBar.setProgress(hoursAttended);
            Log.d(TAG, course.toString());
            if (course.getIsTakingAttendance()) {
                Log.d(TAG, course.getCourseName() + " is taking attendance!");
                openCourseId = course.getCourseId();
                Toast.makeText(MainActivity.this,
                        "Submit attendance for " + course.getCourseName(), Toast.LENGTH_LONG).show();
                attendanceProgressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
                Log.d(TAG, "Current: " + getCurrentMin());
                Log.d(TAG, "Last: " + student.getLastAttendance());
                if (getCurrentMin() - student.getLastAttendance() >= 45) {
                    attendanceBtn.setEnabled(true);
                    attendanceBtn.setBackgroundResource(android.R.drawable.btn_default);
                }
                Attendata.get(MainActivity.this).getDatabase()
                        .child("professors/" + course.getProfessor() + "/location/")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                profLocation = dataSnapshot.getValue(GPSLocation.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, databaseError.toString());
                            }
                        });
            }
        }
    }

    public void updateStudent() {
        tvStudentName.setText(student.getFirstName() + " " + student.getLastName());
        tvStudentNumber.setText(Long.toString(student.getStudentId()));
        String points = getString(R.string.points_format, student.getPoints());
        tvTotalPoints.setText(points);
        adapter = new CourseAdapter(userData.getCourses());
        courseRecyclerView.setAdapter(adapter);
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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_item_show_schedule:
                Log.d(TAG, "Show schedule from student");
                intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_show_leader_board:
                return true;
            case R.id.menu_item_show_map_activity:
                intent = new Intent(MainActivity.this, LocationActivity.class);
                intent.putExtra("USER_ID", student.getUserId());
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

    public void onGiveAttendanceClicked(View view) {
        updateLocation();
        if (mLastLocation != null && profLocation != null) {
            GPSLocation studentLocation = new GPSLocation(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude());
            double distance = GPSLocation.getDistance(studentLocation, profLocation);
            Toast.makeText(this,
                    "Distance: " + distance,
                    Toast.LENGTH_SHORT).show();
            if (distance >= 0 && distance < 10 && openCourseId != null) {
                attendanceBtn.setBackgroundColor(Color.GREEN);
                attendanceBtn.setEnabled(false);
                userData.submitAttendance(openCourseId, student.getUserId());
                userData.setLastAttendance(student.getUserId(), getCurrentMin());
            }
        } else {
            Toast.makeText(this, "Some location is missing :(", Toast.LENGTH_SHORT).show();
        }

    }

    private long getCurrentMin() {
        return System.currentTimeMillis() / (1000 * 60);
    }
}
