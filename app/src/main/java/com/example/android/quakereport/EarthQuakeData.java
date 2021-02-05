package com.example.android.quakereport;

public class EarthQuakeData {

    private final Double mEarthQuakeMagnitude;
    private final String mEarthQuakeLocation;
    private final String mEarthQuakeNearestLocation;
    private final String mEarthQuakeDate;
    private final String mEarthQuakeTime;
    private final String mEarthQuakeUrl;

    public EarthQuakeData(Double Magnitude, String NearestLocation, String Location, String Date, String Time, String Url) {
        mEarthQuakeMagnitude = Magnitude;
        mEarthQuakeLocation = Location;
        mEarthQuakeNearestLocation = NearestLocation;
        mEarthQuakeDate = Date;
        mEarthQuakeTime = Time;
        mEarthQuakeUrl = Url;
    }

    public String getmEarthQuakeLocation() {
        return mEarthQuakeLocation;
    }

    public Double getmEarthQuakeMagnitude() {
        return mEarthQuakeMagnitude;
    }

    public String getmEarthQuakeDate() {
        return mEarthQuakeDate;
    }

    public String getmEarthQuakeTime() {
        return mEarthQuakeTime;
    }

    public String getmEarthQuakeNearestLocation() {
        return mEarthQuakeNearestLocation;
    }

    public String getmEarthQuakeUrl() {
        return mEarthQuakeUrl;
    }
}
