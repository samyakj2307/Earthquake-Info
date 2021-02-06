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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&limit=10";
    private EarthQuakeDataAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        EarthQuakeAsync task = new EarthQuakeAsync();
        task.execute(USGS_REQUEST_URL);

        // Get a reference to the ListView, and attach the adapter to the listView.
        ListView listView = (ListView) findViewById(R.id.list);

        mAdapter = new EarthQuakeDataAdapter(this, new ArrayList<EarthQuakeData>());

        listView.setAdapter(mAdapter);

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

    private class EarthQuakeAsync extends AsyncTask<String, Void, ArrayList<EarthQuakeData>> {

        @Override
        protected ArrayList<EarthQuakeData> doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            final ArrayList<EarthQuakeData> earthquakes = QueryUtils.fetchEarthquakeData(urls[0]);
            Log.d(LOG_TAG, "completed_background_process");
            return earthquakes;
        }

        @Override
        protected void onPostExecute(ArrayList<EarthQuakeData> earthQuakeData) {
            super.onPostExecute(earthQuakeData);

            // Clear the adapter of previous earthquake data
            mAdapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (earthQuakeData != null && !earthQuakeData.isEmpty()) {
                mAdapter.addAll(earthQuakeData);
            }
        }
    }
}
