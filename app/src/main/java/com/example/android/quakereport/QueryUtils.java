package com.example.android.quakereport;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class QueryUtils {


    private static final String LOG_TAG = "EarthQuakeDataExtracter";

    public static ArrayList<EarthQuakeData> fetchEarthquakeData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonresponse = "";
        try {
            jsonresponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        ArrayList<EarthQuakeData> earthquake = extractEarthquakes(jsonresponse);

        return earthquake;

    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    @SuppressLint("SimpleDateFormat")
    public static ArrayList<EarthQuakeData> extractEarthquakes(String JsonResponse) {

        ArrayList<EarthQuakeData> earthquakes = new ArrayList<>();

        try {
            JSONObject earthquakejson = new JSONObject(JsonResponse);
            JSONArray array = earthquakejson.getJSONArray("features");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                JSONObject properties = object.getJSONObject("properties");

                String url = properties.getString("url");

                Double magnitudetemp = properties.getDouble("mag");
                DecimalFormat magnitudeFormat = new DecimalFormat("0.00");
                Double magnitude = Double.parseDouble(magnitudeFormat.format(magnitudetemp));


                String[] fulllocation = properties.getString("place").split("(?<=of)");
                String nearestlocation, location;
                if (fulllocation.length < 2) {
                    nearestlocation = "Near the";
                    location = fulllocation[0].trim();
                } else {
                    nearestlocation = fulllocation[0].trim();
                    location = fulllocation[1].trim();
                }

                long date = properties.getLong("time");

                Date dateObject = new Date(date);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
                String dateToDisplay = dateFormatter.format(dateObject);

                SimpleDateFormat timeFormatter;
                timeFormatter = new SimpleDateFormat("h:mm a");
                String timeToDisplay;
                timeToDisplay = timeFormatter.format(dateObject);

                earthquakes.add(new EarthQuakeData(magnitude, nearestlocation, location, dateToDisplay, timeToDisplay, url));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }
}
