package com.katespitzer.android.weekender.api;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kate on 1/16/18.
 */

public class MapFetcher {
    private static final String TAG = "MapFetcher";
    private static final Uri ENDPOINT = Uri.parse("https://maps.googleapis.com/maps/api/staticmap?");
    private static final String SIZE = "size";
    private static final String PATH = "path";

    public Bitmap getMapImage(String polyline) {
        URL url = buildURL(polyline);
        Bitmap bitmap;

        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        // this doesn't do anything
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();

        } catch (Exception e) {
            Log.e(TAG, "getMapImage: exception: ", e);
            return null;
        }

//        Log.i(TAG, "getDirections()");
//        String data = "";
//        InputStream iStream = null;
//        HttpURLConnection urlConnection = null;
//        try {
//            URL url = buildURL(route.getDestinations());
//            Log.i(TAG, "getDirections: URL BUILT: " + url);
//
//            urlConnection = (HttpURLConnection) url.openConnection();
//
//            urlConnection.connect();
//
//            iStream = urlConnection.getInputStream();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
//
//            StringBuffer sb = new StringBuffer();
//
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//
//            data = sb.toString();
//
//            br.close();
//
//            iStream.close();
//        } catch (Exception e) {
//            Log.d("Exception", e.toString());
//        } finally {
//            urlConnection.disconnect();
//        }
//        return data;
    }

    private URL buildURL(String polyline) {
        String pathArgs = String.format("weight:%1$s|color:%2$s|enc:%3$s", "3", "blue", "" + polyline);
        String urlString = ENDPOINT.buildUpon()
                .appendQueryParameter(SIZE, "320x240")
                .appendQueryParameter(PATH, pathArgs)
                .build()
                .toString();

        try {
            Log.i(TAG, "buildURL: built URL " + urlString);
            return new URL(urlString);
        } catch (Exception e) {
            Log.e(TAG, "buildURL: exception: ", e);
            return null;
        }
    }
}
