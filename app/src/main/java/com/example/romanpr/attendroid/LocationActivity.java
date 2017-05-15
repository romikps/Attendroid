package com.example.romanpr.attendroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    private static final int LOCATION_ACCESS_PERMISSION_REQUEST = 1;
    EditText currentActivity;
    Button provideLocation;
    ListView userLocations;
    String userId;
    DatabaseReference database;
    List<LocationInfo> locations;
    LocationListAdapter adapter;
    ChildEventListener locationListener;
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        userId = getIntent().getStringExtra("USER_ID");
        locations = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference("locations/" + userId);
        currentActivity = (EditText) findViewById(R.id.current_activity_edit_text);
        provideLocation = (Button) findViewById(R.id.provide_location_button);
        userLocations = (ListView) findViewById(R.id.user_locations_list_view);
        adapter = new LocationListAdapter(this, locations);
        userLocations.setAdapter(adapter);

        /*database.child("locations/" + userId).addValueEventListener(new ValueEventListener() {
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
        });*/

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String provider = mLocationManager.getBestProvider(new Criteria(), false);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LocationInfo locationInfo = new LocationInfo(
                        new GPSLocation(location.getLatitude(), location.getLongitude()),
                        "Automatically acquired location");
                storeUserLocationInfo(locationInfo);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            GPSLocation.requestCoarseFineLocation(this);
            return;
        }
        mLocationManager.requestLocationUpdates(provider, 5 * 60 * 1000, 0, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_ACCESS_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    Toast.makeText(this, "Location permissions successfully granted", Toast.LENGTH_SHORT).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
        database.push().setValue(location);
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

    @Override
    protected void onStart() {
        super.onStart();
        locations.clear();
        locationListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                locations.add(0, dataSnapshot.getValue(LocationInfo.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        database.addChildEventListener(locationListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationListener != null) {
            database.removeEventListener(locationListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_map_menu_item:
                Intent intent = new Intent(this, MapLocationActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
