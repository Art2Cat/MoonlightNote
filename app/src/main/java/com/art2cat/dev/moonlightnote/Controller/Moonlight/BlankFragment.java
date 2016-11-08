package com.art2cat.dev.moonlightnote.Controller.Moonlight;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
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
    private List<Moonlight> mData;
    private String mUserId;
    private DatabaseReference moonlightReference;
    private ValueEventListener mMoonlightListener;
    private static final String TAG = "BlankFragment";


    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance(String userId) {
        BlankFragment fragment = new BlankFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        fragment.setArguments(bundle);
        Log.i(TAG, "newInstance: ");
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString("userId");
            Log.i(TAG, "onCreate: " + mUserId);
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
            getMoonlights();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        if (mMoonlightListener != null) {
            moonlightReference.removeEventListener(mMoonlightListener);
        }
    }

    public void getMoonlights() {
        Log.d(TAG, "getMoonlights: ");
        try {
            moonlightReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("note");
            ValueEventListener moonlightListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //清空list数据
                    mData.clear();
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "getChildrenCount: " + dataSnapshot.getChildrenCount());
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Moonlight moonlight = child.getValue(Moonlight.class);
                            String key = child.getKey();
                            moonlight.setId(key);
                            if (moonlight != null) {
                                mData.add(moonlight);
                            }
                        }
                        mRecyclerView.setAdapter(new MoonlightAdapter(getActivity(), mUserId, mData));
                        Log.d(TAG, "onDataChange: " + mData.size());
                    } else {
                        SnackBarUtils.shortSnackBar(view, "absolutely none data here!",
                                SnackBarUtils.TYPE_INFO).show();
                    }

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
