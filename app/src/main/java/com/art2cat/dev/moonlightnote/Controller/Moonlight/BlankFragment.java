package com.art2cat.dev.moonlightnote.Controller.Moonlight;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Firebase.DbTools;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {
    private View view;
    private RecyclerView mRecyclerView;
    private List<Moonlight> mData, mDatas;
    private DbTools mDbTools;
    private String mUserId;
    private int flag;
    private DatabaseReference moonlightReference;
    private ValueEventListener mMoonlightListener;
    private static final String TAG = "BlankFragment";


    public BlankFragment() {
        // Required empty public constructor
    }

    public BlankFragment newInstance(String userId, int state) {
        BlankFragment fragment = new BlankFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putInt("state", state);
        fragment.setArguments(bundle);
        Log.i(TAG, "newInstance: ");
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString("userId");
            flag = getArguments().getInt("state");
            Log.i(TAG, "onCreate: " + mUserId);
            getMoonlights();
            mData = new ArrayList<Moonlight>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_moonlight, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: ");
            if (mData != null) {

                mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                MoonlightAdapter ad = new MoonlightAdapter(getActivity(), mData);
                mRecyclerView.setAdapter(ad);
                Log.d(TAG, "onPostExecute: " + ad.getItemCount());

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
       // getMoonlights();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
      //  if (mMoonlightListener != null) {
      //      moonlightReference.removeEventListener(mMoonlightListener);
      //  }
    }

    private class LoadData extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mData != null) {

                mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                MoonlightAdapter ad = new MoonlightAdapter(getActivity(), mData);
                mRecyclerView.setAdapter(ad);
                Log.d(TAG, "onPostExecute: " + ad.getItemCount());

            }
        }
    }


    public void getMoonlights() {
        Log.d(TAG, "getMoonlights: ");
        try {
            moonlightReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("day_book");
            ValueEventListener moonlightListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "getChildrenCount: " + dataSnapshot.getChildrenCount());
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Moonlight moonlightTest = child.getValue(Moonlight.class);
                            String key = child.getKey();
                            if (moonlightTest != null) {
                                mData.add(moonlightTest);
                            }
                        }
                        Log.d(TAG, "onDataChange: " + mData.size());
                    } else {
                        Utils.displaySnackBar(view, "absolutely none data here!");
                    }

                    MoonlightAdapter ad = new MoonlightAdapter(getActivity(), mData);
                    mRecyclerView.setAdapter(ad);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadMoonlight:onCancelled", databaseError.toException());
                    Toast.makeText(getActivity(), "Failed to load moonlight.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            moonlightReference.addValueEventListener(moonlightListener);
            mMoonlightListener = moonlightListener;

        } catch (Exception e) {
            Log.d(TAG, "getMoonlights: " + e.toString());
        }
    }
}
