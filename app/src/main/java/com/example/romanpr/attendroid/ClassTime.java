package com.example.romanpr.attendroid;

/**
 * Created by romanpr on 4/22/2017.
 */

public class ClassTime {

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
}
