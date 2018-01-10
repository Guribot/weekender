package com.katespitzer.android.weekender;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katespitzer.android.weekender.dummy.DummyContent;

import org.json.JSONObject;

import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripRouteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TripRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class TripRouteFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private DestinationRecyclerViewAdapter mAdapter;
    private OnListFragmentInteractionListener mDestinationListener;

    private static final String TAG = "TripRouteFragment";

    public TripRouteFragment() {
        // Required empty public constructor
    }


    public static TripRouteFragment newInstance(UUID tripId) {
        TripRouteFragment fragment = new TripRouteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_route, container, false);

        // Set the adapter
        final Context context = view.getContext();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trip_route_recycler_view);
        mAdapter = new DestinationRecyclerViewAdapter(DummyContent.ITEMS, mDestinationListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyContent.DummyItem item);
    }

    private class FetchRouteTask extends AsyncTask<Void, Void, JSONObject> {
        Route mRoute;

        public FetchRouteTask(Route route) {
            mRoute = route;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            if (mRoute == null) {
                Log.e(TAG, "doInBackground: No Route set");
                return null;
            } else {
                JSONObject jsonObject = new JSONObject();
                String results = new DirectionsFetcher().getDirections(mRoute);
                Log.i(TAG, "doInBackground: results returned: " + results);

                try {
                    jsonObject = new JSONObject(results);
                    jsonObject = jsonObject.getJSONArray("routes")
                            .getJSONObject(0);
                } catch (Exception e) {
                    Log.d(TAG, "doInBackground: exception: " + e.toString());
                }

                Log.i(TAG, "doInBackground: result is: " + results);
                return jsonObject;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.i(TAG, "onPostExecute: " + jsonObject);
            super.onPostExecute(jsonObject);
        }
    }
}
