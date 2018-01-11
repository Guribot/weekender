package com.katespitzer.android.weekender;

import android.net.Uri;
import android.util.Log;

import com.katespitzer.android.weekender.models.Destination;
import com.katespitzer.android.weekender.models.Route;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by kate on 1/9/18.
 */

public class DirectionsFetcher {

    private static final String DIRECTIONS_API_KEY = "AIzaSyCIHqtLThVikm-SgmvE8FnHf6GcFaVNq4g";
    private static final Uri ENDPOINT = Uri.parse("https://maps.googleapis.com/maps/api/directions/json?");
    private static final String KEY = "key";
    private static final String ORIGIN = "origin";
    private static final String ENDDEST = "destination";
    private static final String WAYPOINTS = "waypoints";

    private static final String TAG = "DirectionsFetcher";

    public String getDirections(Route route) {
        Log.i(TAG, "getDirections()");
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = buildURL(route.getDestinations());
            Log.i(TAG, "getDirections: URL BUILT: " + url);

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

    private URL buildURL(List<Destination> destinations) {
        Destination origin = destinations.get(0);
        int lastIndex = destinations.size() - 1;
        Destination endDest = destinations.get(lastIndex);
        List<Destination> waypoints = destinations.subList(1, lastIndex);
        String waypointVals = new String();

        Uri builtURL = ENDPOINT.buildUpon()
                .appendQueryParameter(KEY, DIRECTIONS_API_KEY)
                .appendQueryParameter(ORIGIN, origin.getGooglePlaceId())
                .appendQueryParameter(ENDDEST, endDest.getGooglePlaceId())
                .build();

        if (waypoints.size() > 0) {
            for (int i = 0; i < waypoints.size(); i ++) {
                waypointVals = waypointVals.concat(waypoints.get(i).getGooglePlaceId());
                if ( (i + 1) != waypoints.size() ) {
                    waypointVals = waypointVals.concat("|");
                }
            }

            builtURL.buildUpon()
                    .appendQueryParameter(WAYPOINTS, waypointVals)
                    .build();
        }

        try {
            URL url = new URL(builtURL.toString());
            return url;
        } catch (MalformedURLException mue) {
            Log.e(TAG, "MalformedURLException", mue);
            return null;
        }
    }
}
