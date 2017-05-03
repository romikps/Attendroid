package com.example.romanpr.attendroid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        ListView classList = (ListView) findViewById(R.id.class_list);
        // Create the adapter to convert the array to views
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(this, Attendata.get(this).getCourses());
        // Attach the adapter to a ListView
        classList.setAdapter(scheduleAdapter);
    }

    private class ScheduleAdapter extends ArrayAdapter<Course> {
        public ScheduleAdapter(Context context, List<Course> courses) {
            super(context, 0, courses);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Course course = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_item, parent, false);
            }
            // Lookup view for data population
            TextView courseName = (TextView) convertView.findViewById(R.id.course_name);
            TextView classHours = (TextView) convertView.findViewById(R.id.class_hours);
            // Populate the data into the template view using the data object
            courseName.setText(course.courseName);
            classHours.setText(course.getClassHours());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

