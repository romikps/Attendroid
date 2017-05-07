package com.example.romanpr.attendroid;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.firebase.database.Exclude;

import java.text.DecimalFormat;

/**
 * Created by romanpr on 5/6/2017.
 */

public class GPSLocation {

    private static final int LOCATION_ACCESS_PERMISSION_REQUEST = 1;
    double latitude, longitude;

    public GPSLocation() {}

    public GPSLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public static boolean locationAccessPermissionGranted(Activity thisActivity) {
        return ActivityCompat.checkSelfPermission(thisActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(thisActivity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermissions(Activity thisActivity) {
        // Here, thisActivity is the current activity
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                && ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            Toast.makeText(thisActivity,
                    "Your location is used only locally and is necessary to decide if you're in class when attendance is taken",
                    Toast.LENGTH_SHORT).show();
            requestCoarseFineLocation(thisActivity);
        } else {
            // No explanation needed, we can request the permission.
            requestCoarseFineLocation(thisActivity);
        }
    }

    public static void requestCoarseFineLocation(Activity thisActivity) {
        ActivityCompat.requestPermissions(thisActivity,
                new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                },
                LOCATION_ACCESS_PERMISSION_REQUEST);
        // LOCATION_ACCESS_PERMISSION_REQUEST is an
        // app-defined int constant. The callback method gets the
        // result of the request.
    }

    @Exclude
    public static GPSLocation getLocation(Activity thisActivity) {
        LocationManager locationManager = (LocationManager) thisActivity.getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(thisActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(thisActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(thisActivity, "No Location Access Permission!", Toast.LENGTH_SHORT).show();
            return null;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        DecimalFormat df = new DecimalFormat("#.##");
        Toast.makeText(thisActivity, "Latitude: " + df.format(location.getLatitude())
                    + "\nLongitude: " + df.format(location.getLongitude()), Toast.LENGTH_LONG).show();
        return new GPSLocation(location.getLatitude(), location.getLongitude());
    }

    public static double degToRad(double deg) {
        return deg * Math.PI/180;
    }

    public static double getDistance(GPSLocation loc1, GPSLocation loc2) {
        int R = 6371 * 1000; // Radius of the earth in m
        double dLat = degToRad(loc2.getLatitude() - loc1.getLatitude());
        double dLng = degToRad(loc2.getLongitude() - loc1.getLongitude());
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(degToRad(loc1.getLatitude())) * Math.cos(degToRad(loc2.getLatitude())) *
                                Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in m
        return Math.abs(d);
    }
}
