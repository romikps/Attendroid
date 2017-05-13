package com.example.romanpr.attendroid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    EditText currentActivity;
    Button provideLocation;
    ListView userLocations;
    String userId;
    DatabaseReference database;
    List<LocationInfo> locations;
    LocationListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        userId = getIntent().getStringExtra("USER_ID");
        locations = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference();
        currentActivity = (EditText) findViewById(R.id.current_activity_edit_text);
        provideLocation= (Button) findViewById(R.id.provide_location_button);
        userLocations = (ListView) findViewById(R.id.user_locations_list_view);
        adapter = new LocationListAdapter(this, locations);
        userLocations.setAdapter(adapter);

        database.child("locations/" + userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locations.clear();
                for (DataSnapshot loc : dataSnapshot.getChildren()) {
                    locations.add(loc.getValue(LocationInfo.class));
                }
                Collections.sort(locations, new Comparator<LocationInfo>() {
                    @Override
                    public int compare(LocationInfo loc2, LocationInfo loc1)
                    {
                        return  loc1.getTimestamp().compareTo(loc2.getTimestamp());
                    }
                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onGiveMyLocationClicked(View view) {
        GPSLocation gpsLocation = GPSLocation.getLocation(this);
        if (gpsLocation != null) {
            long currentTimme = getTimestamp();
            LocationInfo locationInfo = new LocationInfo(gpsLocation,
                    currentActivity.getText().toString());
            currentActivity.setText("");
            storeUserLocationInfo(locationInfo);
        } else {
            Toast.makeText(this, "Can't access your location!", Toast.LENGTH_SHORT).show();
        }
    }

    public void storeUserLocationInfo(LocationInfo location) {
        database.child("locations/" + userId).push().setValue(location);
    }

    public long getTimestamp() {
        return System.currentTimeMillis();
    }

    private class LocationListAdapter extends ArrayAdapter<LocationInfo> {
        public LocationListAdapter(Context context, List<LocationInfo> locations) {
            super(context, 0, locations);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LocationInfo location = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_item, parent, false);
            }
            // Lookup view for data population
            TextView activity = (TextView) convertView.findViewById(R.id.activity_text_view);
            TextView datetime = (TextView) convertView.findViewById(R.id.datetime_text_view);
            TextView latitude = (TextView) convertView.findViewById(R.id.latitude_text_view);
            TextView longitude = (TextView) convertView.findViewById(R.id.longitude_text_view);
            // Populate the data into the template view using the data object
            activity.setText(location.getActivity());
            datetime.setText(location.getTimestamp().toString());
            latitude.setText(getString(R.string.latitude_format, location.getLocation().getLatitude()));
            longitude.setText(getString(R.string.longitude_format, location.getLocation().getLongitude()));
            // Return the completed view to render on screen
            return convertView;
        }
    }

}
