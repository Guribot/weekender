package com.katespitzer.android.weekender;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kate on 1/10/18.
 */

public class PlaceFetcher {

    private static final String PLACES_API_KEY = "AIzaSyDuZyVgRjLmr8phtuJZvNXCfaZjpE_k0jo";
    private static final Uri ENDPOINT = Uri.parse("https://maps.googleapis.com/maps/api/place/textsearch/json?");
    private static final String KEY = "key";
    private static final String QUERY = "query";

    private static final String TAG = "PlaceFetcher";


    public String getPlaceData(String query) {
        Log.i(TAG, "getPlaceData()");
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = buildURL(query);
            Log.i(TAG, "getPlaceData: URL BUILT: " + url);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

            iStream.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    public URL buildURL(String query) {
        String newURL = ENDPOINT.buildUpon()
                .appendQueryParameter(KEY, PLACES_API_KEY)
                .appendQueryParameter(QUERY, query)
                .build()
                .toString();
        try {
            return new URL(newURL);
        } catch (Exception e) {
            Log.e(TAG, "buildURL: Exception Occurred: ", e);
            return null;
        }
    }
}
