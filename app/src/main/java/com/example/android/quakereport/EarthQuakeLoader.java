package com.example.android.quakereport;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;


public class EarthQuakeLoader extends AsyncTaskLoader<List<EarthQuakeData>> {

    private String mUrl;

    public EarthQuakeLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public List<EarthQuakeData> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        final ArrayList<EarthQuakeData> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }
}
