package com.example.romanpr.attendroid;

/**
 * Created by romanpr on 4/22/2017.
 */

public class ClassTime {

    Time startingTime, endingTime;
    DayOfWeek day;

    public ClassTime(DayOfWeek dayOfWeek, String startingTime, String endingTime) {
        this.day = dayOfWeek;
        this.startingTime = new Time(startingTime);
        this.endingTime = new Time(endingTime);
    }

    public String getStartingTime() {
        return startingTime.toString();
    }

    public String getEndingTime() {
        return endingTime.toString();
    }

    public int getDurationInHours() {
        return endingTime.hours - startingTime.hours;
    }

    public String getDay() {
        return day.name();
    }
}
