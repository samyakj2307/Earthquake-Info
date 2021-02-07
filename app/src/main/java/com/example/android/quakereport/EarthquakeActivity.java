/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthQuakeData>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&limit=20";
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private EarthQuakeDataAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Get a reference to the ListView, and attach the adapter to the listView.
        ListView listView = (ListView) findViewById(R.id.list);

        mAdapter = new EarthQuakeDataAdapter(this, new ArrayList<EarthQuakeData>());

        listView.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);


        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {

            LoaderManager loaderManager = LoaderManager.getInstance(this);
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EarthQuakeData currentEarthquake = mAdapter.getItem(i);
                Uri earthquakeUri = Uri.parse(currentEarthquake.getmEarthQuakeUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                startActivity(websiteIntent);

            }
        });
    }

    @NonNull
    @Override
    public Loader<List<EarthQuakeData>> onCreateLoader(int id, @Nullable Bundle args) {

        return new EarthQuakeLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<EarthQuakeData>> loader, List<EarthQuakeData> earthquakes) {

        // Clear the adapter of previous earthquake data
        mAdapter.clear();


        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }

        mEmptyStateTextView.setText(R.string.no_earthquakes);
        mProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<EarthQuakeData>> loader) {

        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();

    }
}
