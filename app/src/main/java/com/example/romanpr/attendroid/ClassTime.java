package com.example.romanpr.attendroid;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by romanpr on 4/22/2017.
 */

public class ClassTime implements Comparable<ClassTime> {

    String startingTime, endingTime;
    DayOfWeek day;

    public ClassTime() {}

    public ClassTime(DayOfWeek dayOfWeek, String startingTime, String endingTime) {
        this.day = dayOfWeek;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public String getDay() {
        return day.name();
    }

    @Override
    public String toString() {
        return day + ", " + startingTime + " - " + endingTime;
    }

    @Override
    public int compareTo(@NonNull ClassTime t2) {
        int result = 0;
        if (this.day.compareTo(t2.day) != 0) {
            result = this.day.compareTo(t2.day);
        } else {
            Time startingTime1 = new Time(this.startingTime);
            Time startingTime2 = new Time(t2.startingTime);
            if (startingTime1.hours != startingTime2.hours) {
                result = startingTime1.hours < startingTime2.hours ? -1 : 1;
            } else {
                result = startingTime1.minutes < startingTime2.minutes ? -1 : 1;
            }
        }
        return result;
    }
}
