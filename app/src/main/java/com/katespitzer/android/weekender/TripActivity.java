package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.katespitzer.android.weekender.api.PlaceFetcher;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Note;
import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.models.Trip;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TripActivity 
        extends AppCompatActivity 
        implements TripPlaceFragment.OnListFragmentInteractionListener,
        TripRouteFragment.OnFragmentInteractionListener, 
        TripNoteFragment.OnListFragmentInteractionListener,
        SearchResultFragment.OnListFragmentInteractionListener {

    private static final String TAG = "TripActivity";
    private static final String EXTRA_TRIP_UUID = "com.katespitzer.android.weekender.trip_uuid";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Context mContext;
    private Trip mTrip;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        mContext = this;

        UUID tripId = (UUID) getIntent().getSerializableExtra(EXTRA_TRIP_UUID);
        mTrip = TripManager.get(this).getTrip(tripId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    public static Intent newIntent(Context context, UUID tripId) {
        Intent intent = new Intent(context, TripActivity.class);
        intent.putExtra(EXTRA_TRIP_UUID, tripId);

        return intent;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i(TAG, "onPlaceClicked: " + uri);
    }

    @Override
    public void onPlaceClicked(Place place) {
        Log.i(TAG, "onPlaceClicked: " + place);
//        Intent intent = PlaceActivity.newIntent(this, place.getId());
//
//        startActivity(intent);

        Intent intent = PlaceTabbedActivity.newIntent(this, place.getId());

        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(Place result) {

    }

    @Override
    public void onListFragmentInteraction(Note note) {
        Log.i(TAG, "onPlaceClicked: dummy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_places, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.trip_menu_add_place) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Place Search");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "onClick: input: " + input.getText());
                    String query = input.getText().toString();

                    new FetchPlacesTask(mContext, query).execute();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment = TripPlaceFragment.newInstance(mTrip.getId());

            switch (position) {
                case 0:
                    fragment = TripRouteFragment.newInstance(mTrip.getId());
                    break;
                case 1:
                    fragment = TripPlaceFragment.newInstance(mTrip.getId());
                    break;
                case 2:
                    fragment = TripNoteFragment.newInstance(mTrip.getId());
                    break;
                default:
                    break;
            }

//            return PlaceholderFragment.newInstance(position + 1);

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    private class FetchPlacesTask extends AsyncTask<Void, Void, String> {
        String mQuery;
        List<Place> mPlaces;
        List<CharSequence> mPlaceNames;
        Place mSelection;
        Context mContext;

        GeoDataClient mGeoDataClient;

        public FetchPlacesTask(Context context, String query) {
            mContext = context;
            mQuery = query;
            mPlaces = new ArrayList<>();
            mPlaceNames = new ArrayList<>();
            mGeoDataClient = Places.getGeoDataClient(mContext, null);
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (mQuery == null) {
                Log.e(TAG, "doInBackground: No Query set");
                return null;
            } else {
                JSONObject jsonObject = new JSONObject();
//                JSONArray jsonArray = new JSONArray();
                String results = new PlaceFetcher().getPlaceData(mQuery);
                Log.i(TAG, "doInBackground: results returned: " + results);
//
                try {
                    jsonObject = new JSONObject(results);
                    String status = jsonObject.getString("status");
                    return status;
                } catch (Exception e) {
                    Log.d(TAG, "doInBackground: exception: " + e.toString());
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(String status) {
            Log.i(TAG, "onPostExecute: " + status);

            if (status.equals("OK")) {
                Log.i(TAG, "onPostExecute: status ok");
                Intent intent = PlaceSearchActivity.newIntent(mContext, mQuery, mTrip.getId());
                startActivity(intent);
            } else if (status.equals("ZERO_RESULTS")) {
                Log.i(TAG, "onPostExecute: no results");
                Toast.makeText(mContext, "No results found", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "onPostExecute: unexpected status: " + status);
            }
            super.onPostExecute(status);
        }

        private void getPhotos(String placeId) {
            final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
            photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    final PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    // Get the first photo in the list.
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            photoMetadataBuffer.release();
                            Log.i(TAG, "onComplete: result found: \n bitmap: " + bitmap + "\n photo: " + photo);
                        }
                    });

                }
            });
        }
    }

}
