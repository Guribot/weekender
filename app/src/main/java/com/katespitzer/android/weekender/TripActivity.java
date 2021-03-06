package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Note;
import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.models.Trip;

import java.util.UUID;

public class TripActivity 
        extends AppCompatActivity 
        implements TripPlaceFragment.OnPlaceInteractionListener,
        TripNoteFragment.OnNoteInteractionListener {

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

        String tripTitle = mTrip.getTitle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(tripTitle);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
                    Intent intent = new Intent(TripMapFragment.RESPONSE_REFRESH);
                    lbm.sendBroadcast(intent);
                }
                super.onTabSelected(tab);
            }
        });
    }

    public static Intent newIntent(Context context, UUID tripId) {
        Intent intent = new Intent(context, TripActivity.class);
        intent.putExtra(EXTRA_TRIP_UUID, tripId);

        return intent;
    }

    @Override
    public void onPlaceClicked(Place place) {
        Intent intent = PlaceTabbedActivity.newIntent(this, place.getId());

        startActivity(intent);
    }

    @Override
    public void onNoteClicked(Note note) {
        Intent intent = NoteActivity.newIntent(this, note.getId());

        startActivity(intent);
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
                    fragment = TripMapFragment.newInstance(mTrip.getId());
                    break;
                case 1:
                    fragment = TripRouteFragment.newInstance(mTrip.getId());
                    break;
                case 2:
                    fragment = TripPlaceFragment.newInstance(mTrip.getId());
                    break;
                case 3:
                    fragment = TripNoteFragment.newInstance(mTrip.getId());
                    break;
                default:
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }


}
