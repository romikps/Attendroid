package com.example.romanpr.attendroid;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MapLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapLocationActivity";
    
    private GoogleMap mMap;
    MapFragment mapFragment;
    DatabaseReference database;
    String userId;
    Map<String, String> mUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        userId = getIntent().getStringExtra("USER_ID");
        database = FirebaseDatabase.getInstance().getReference();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mUsers = new HashMap<>();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                for (DataSnapshot student : dataSnapshot.child("students").getChildren()) {
                    Log.d(TAG, student.toString());
                    user = student.getValue(Student.class);
                    Log.d(TAG, user.toString());
                    mUsers.put(student.getKey(), user.getFirstName() + " " + user.getLastName());
                }

                for (DataSnapshot prof : dataSnapshot.child("professors").getChildren()) {
                    user = prof.getValue(Professor.class);
                    Log.d(TAG, user.toString());
                    mUsers.put(prof.getKey(), user.getFirstName() + " " + user.getLastName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(MapLocationActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MapLocationActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MapLocationActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException se) {

        }

        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        database.child("locations/" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationInfo locInfo;
                LatLng placeLocation;
                LatLngBounds.Builder bld = new LatLngBounds.Builder();
                for (DataSnapshot loc : dataSnapshot.getChildren()) {
                    locInfo = loc.getValue(LocationInfo.class);
                    placeLocation = new LatLng(locInfo.getLocation().getLatitude(),
                            locInfo.getLocation().getLongitude());
                    bld.include(placeLocation);
                    Marker placeMarker = mMap.addMarker(new MarkerOptions().position(placeLocation)
                            .title(locInfo.getActivity()).snippet(locInfo.getTimestamp().toString()));
                    // finalGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
                    // finalGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);
                }
                LatLngBounds bounds = bld.build();
                // finalGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                // finalGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 16.0f));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.peek_all:
                mMap.clear();
                database.child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LocationInfo locInfo;
                        LatLng placeLocation;
                        LatLngBounds.Builder bld = new LatLngBounds.Builder();
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            for (DataSnapshot loc : user.getChildren()) {
                                locInfo = loc.getValue(LocationInfo.class);
                                placeLocation = new LatLng(locInfo.getLocation().getLatitude(),
                                        locInfo.getLocation().getLongitude());
                                bld.include(placeLocation);
                                Marker placeMarker = mMap.addMarker(
                                        new MarkerOptions()
                                                .position(placeLocation)
                                                .title(locInfo.getActivity())
                                        .snippet(mUsers.get(user.getKey()) + "\n" + locInfo.getTimestamp().toString())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                );
                                // finalGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
                                // finalGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);
                            }
                        }
                        LatLngBounds bounds = bld.build();
                        // finalGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                        // finalGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 16.0f));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
