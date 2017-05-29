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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    RecyclerView mCourseRecyclerView;
    CourseAdapter mAdapter;
    String mStudentId;
    Student mStudent;
    GPSLocation mProfessorLocation;
    Button mAttendanceBtn;
    String mOpenCourseId;
    DatabaseReference database;
    ValueEventListener mStudentListener;
    ChildEventListener mCourseListener;
    TextView tvStudentName;
    TextView tvStudentNumber;
    TextView tvTotalPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Map<String, Course> mStudentCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStudentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference();
        mStudentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mStudent = dataSnapshot.getValue(Student.class);
                updateStudent();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mCourseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (mStudent.getCourses().keySet().contains(dataSnapshot.getKey())) {
                    mStudentCourses.put(dataSnapshot.getKey(), dataSnapshot.getValue(Course.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (mStudent.getCourses().keySet().contains(dataSnapshot.getKey())) {
                    mStudentCourses.remove(dataSnapshot.getKey());
                    mStudentCourses.put(dataSnapshot.getKey(), dataSnapshot.getValue(Course.class));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mCourseRecyclerView = (RecyclerView) findViewById(R.id.course_recycler_view);
        mCourseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAttendanceBtn = (Button) findViewById(R.id.attendance_button);
        mAttendanceBtn.setEnabled(false);

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

        mStudentCourses = new HashMap<>();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        database.child("students/" + mStudentId).addValueEventListener(mStudentListener);
        database.child("courses").addChildEventListener(mCourseListener);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        database.child("students/" + mStudentId).removeEventListener(mStudentListener);
        database.child("courses").removeEventListener(mCourseListener);
        super.onStop();
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
            int hoursAttended = mStudent.getAttendanceData().get(course.getCourseId());
            tvCourseName.setText(course.getCourseName());
            tvAttendanceStatus.setText(hoursAttended + "/" + course.getTotalHours());
            attendanceProgressBar.setMax(course.getTotalHours());
            attendanceProgressBar.setProgress(hoursAttended);
            if (course.getIsTakingAttendance()) {
                mOpenCourseId = course.getCourseId();
                attendanceProgressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
                if (getTime() - mStudent.getLastAttendance() >= 45) {
                    mAttendanceBtn.setEnabled(true);
                }
                database.child("professors/" + course.getProfessor() + "/location/")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mProfessorLocation = dataSnapshot.getValue(GPSLocation.class);
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
        tvStudentName.setText(mStudent.getFirstName() + " " + mStudent.getLastName());
        tvStudentNumber.setText(Long.toString(mStudent.getStudentId()));
        String points = getString(R.string.points_format, mStudent.getPoints());
        tvTotalPoints.setText(points);
        List<Course> courses = new ArrayList<>();
        courses.addAll(mStudentCourses.values());
        mAdapter = new CourseAdapter(courses);
        mCourseRecyclerView.setAdapter(mAdapter);
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
                intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_show_leader_board:
                return true;
            case R.id.menu_item_show_map_activity:
                intent = new Intent(MainActivity.this, LocationActivity.class);
                intent.putExtra("USER_ID", mStudentId);
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
        if (mLastLocation != null && mProfessorLocation != null) {
            GPSLocation studentLocation = new GPSLocation(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude());
            double distance = GPSLocation.getDistance(studentLocation, mProfessorLocation);
            Toast.makeText(this,
                    "Distance: " + distance,
                    Toast.LENGTH_SHORT).show();
            if (distance >= 0 && distance < 10 && mOpenCourseId != null) {
                mAttendanceBtn.setEnabled(false);
                submitAttendance(mOpenCourseId);
            }
        } else {
            Toast.makeText(this, "Professor's or student's location is missing", Toast.LENGTH_SHORT).show();
        }

    }

    public void submitAttendance(String courseId) {
        Map<String, Object> updates = new HashMap<>();
        long time = getTime();
        updates.put("courses/" + courseId + "/students/" + mStudentId, true);
        updates.put("students/" + mStudentId + "/attendanceData/" + courseId,
                mStudent.getAttendanceData().get(courseId) + 1);
        updates.put("students/" + mStudentId + "/lastAttendance", time);
        database.updateChildren(updates);
    }

    public long getTime() {
        return System.currentTimeMillis() / (1000 * 60);
    }
}
