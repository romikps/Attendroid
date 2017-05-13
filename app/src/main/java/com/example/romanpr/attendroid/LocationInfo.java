package com.example.romanpr.attendroid;

import java.util.Date;

/**
 * Created by romanpr on 5/13/2017.
 */

public class LocationInfo {

    GPSLocation location;
    Date timestamp;
    String activity;

    public LocationInfo() {
    }

    public LocationInfo(GPSLocation location, String activity) {
        this.location = location;
        this.timestamp = new Date();
        this.activity = activity;
    }

    public GPSLocation getLocation() {
        return location;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getActivity() {
        return activity;
    }
}
