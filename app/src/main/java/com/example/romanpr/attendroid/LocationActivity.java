package com.example.romanpr.attendroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    EditText currentActivity;
    Button provideLocation;
    ListView userLocations;
    String userId;
    DatabaseReference database;
    List<LocationInfo> locations;
    LocationListAdapter adapter;
    ChildEventListener locationListener;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        locations = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference("locations/" + userId);
        currentActivity = (EditText) findViewById(R.id.current_activity_edit_text);
        provideLocation = (Button) findViewById(R.id.provide_location_button);
        userLocations = (ListView) findViewById(R.id.user_locations_list_view);
        adapter = new LocationListAdapter(this, locations);
        userLocations.setAdapter(adapter);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10 * 60 * 1000);
        mLocationRequest.setFastestInterval(5 * 60 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location updates access permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void onGiveMyLocationClicked(View view) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location access permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            GPSLocation gpsLocation = new GPSLocation(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude());
            LocationInfo locationInfo = new LocationInfo(gpsLocation,
                    currentActivity.getText().toString());
            currentActivity.setText("");
            storeUserLocationInfo(locationInfo);
        } else {
            Toast.makeText(this, "The location is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void storeUserLocationInfo(LocationInfo location) {
        database.push().setValue(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        GPSLocation gpsLocation = new GPSLocation(mLastLocation.getLatitude(),
                mLastLocation.getLongitude());
        LocationInfo locationInfo = new LocationInfo(gpsLocation,
                "Automatic location");
        storeUserLocationInfo(locationInfo);
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
        mGoogleApiClient.connect();
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
        // mGoogleApiClient.disconnect();
        super.onStop();
        if (locationListener != null) {
            database.removeEventListener(locationListener);
        }
        // stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
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
            case R.id.log_out_menu_item:
                Attendata.get(this).signOut();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
