package com.katespitzer.android.weekender.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private static final String IMAGE_RES = "640x480";

    public static Bitmap getMapImage(String polyline) {
        URL url = buildURL(polyline);
        Bitmap bitmap;

        InputStream in = null;
        HttpURLConnection connection = null;

        // this doesn't do anything
        try {
            connection = (HttpURLConnection) url.openConnection();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        url);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            byte[] outBytes = out.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(outBytes, 0, outBytes.length);
//            return out.toByteArray();
        } catch (Exception e) {
            Log.e(TAG, "getMapImage: exception: ", e);
            return null;
        } finally {
            connection.disconnect();
        }

        return bitmap;
    }

    private static URL buildURL(String polyline) {
        String pathArgs = String.format("weight:%1$s|color:%2$s|enc:%3$s", "3", "blue", "" + polyline);
        String urlString = ENDPOINT.buildUpon()
                .appendQueryParameter(SIZE, IMAGE_RES)
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
