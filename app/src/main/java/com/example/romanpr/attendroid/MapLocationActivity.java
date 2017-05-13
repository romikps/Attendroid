package com.example.romanpr.attendroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MapLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapLocationActivity";
    GoogleMap googleMap;
    MapFragment mapFragment;
    DatabaseReference database;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        userId = getIntent().getStringExtra("USER_ID");
        database = FirebaseDatabase.getInstance().getReference("locations/" + userId);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException se) {

        }

        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        final GoogleMap finalGoogleMap = googleMap;
        database.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Marker placeMarker = finalGoogleMap.addMarker(new MarkerOptions().position(placeLocation)
                            .title(locInfo.getActivity()));
                    // finalGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
                    // finalGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);
                }
                LatLngBounds bounds = bld.build();
                // finalGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
                finalGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                // finalGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 16.0f));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
