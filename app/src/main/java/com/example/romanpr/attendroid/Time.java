package com.example.romanpr.attendroid;

/**
 * Created by romanpr on 4/22/2017.
 */

public class Time {

    int hours, minutes;

    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public Time(String time) {
        String[] timeSplit = time.split(":");
        this.hours = Integer.parseInt(timeSplit[0]);
        this.minutes = Integer.parseInt(timeSplit[1]);
    }

    @Override
    public String toString() {
        return hours + ":" + minutes;
    }
}
