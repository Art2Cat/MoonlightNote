package com.art2cat.dev.moonlightnote.Controller.MoonlightActivity;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by art2cat
 * on 9/17/16.
 */
public abstract class MoonlightListFragment extends Fragment {
    private static final String TAG = "MoonlightListFragment";
    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Moonlight, MoonlightsViewHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private Menu menu;
    private int index;
    private int[] drawableArray = new int[]{
            R.drawable.ic_view_stream_white_24dp,
            R.drawable.ic_view_quilt_white_24dp
    };

    public MoonlightListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_moonlight, container, false);
        setHasOptionsMenu(true);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        Query moonlightsQuery = getQuery(mDatabase);
        if (moonlightsQuery != null) {
            mAdapter = new FirebaseRecyclerAdapter<Moonlight, MoonlightsViewHolder>
                    (Moonlight.class, R.layout.moonlight_item, MoonlightsViewHolder.class,
                            moonlightsQuery) {
                @Override
                public DatabaseReference getRef(int position) {
                    Log.i(TAG, "getRef");
                    return super.getRef(position);
                }

                @Override
                protected void populateViewHolder(MoonlightsViewHolder viewHolder,
                                                  Moonlight model, int position) {
                    final DatabaseReference moonlightRef = getRef(position);

                    final String moonlightKey = moonlightRef.getKey();


                    Log.i(TAG, "key id: " + moonlightKey);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });

                    viewHolder.onBindMoonlight(model);
                }
            };
            mRecyclerView.setAdapter(mAdapter);
            Log.d(TAG, "setAdapter");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            Log.d(TAG, "cleanup");
            mAdapter.cleanup();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        this.menu = menu;
        setLayoutManager();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_layout:
                index = (index + 1) % 2;
                setLayoutManager();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setLayoutManager() {
        menu.findItem(R.id.menu_layout).setIcon(drawableArray[index]);
        switch (index) {
            case 0:
                mLinearLayoutManager = new LinearLayoutManager(getActivity());
                mLinearLayoutManager.setReverseLayout(true);
                mLinearLayoutManager.setStackFromEnd(true);
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
                break;
            case 1:
                int orientation = this.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
                    mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
                    mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                }
                mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
                break;
        }
    }

    public String getUid() {
        String uid = SPUtils.getString(getActivity(), "User", "Id", null);
        if (uid == null) {
            return null;
        } else {
            return uid;
        }
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}
