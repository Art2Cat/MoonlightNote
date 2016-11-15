package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
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
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.MoonlightDetailActivity;
import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.FirebaseImageLoader;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.art2cat.dev.moonlightnote.Model.Constants.BUS_FLAG_ALBUM;
import static com.art2cat.dev.moonlightnote.Model.Constants.BUS_FLAG_CAMERA;
import static com.art2cat.dev.moonlightnote.Model.Constants.BUS_FLAG_USERNAME;

/**
 * Created by art2cat
 * on 9/17/16.
 */
public abstract class MoonlightListFragment extends Fragment {
    private static final String TAG = "MoonlightListFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Moonlight, MoonlightsViewHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private Menu menu;
    private int index;
    private boolean deleteFlag;
    private boolean isLogin = true;


    public MoonlightListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取Bus单例，并注册
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView: ");
        View rootView = inflater.inflate(R.layout.fragment_moonlight, container, false);
        if (isTrash()) {
            setHasOptionsMenu(true);
        }

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        setAdapter();
    }

    private void setAdapter() {

        Query moonlightsQuery = getQuery(mDatabase);
        if (moonlightsQuery != null) {
            mAdapter = new FirebaseRecyclerAdapter<Moonlight, MoonlightsViewHolder>
                    (Moonlight.class, R.layout.moonlight_items, MoonlightsViewHolder.class,
                            moonlightsQuery) {
                @Override
                public DatabaseReference getRef(int position) {
                    return super.getRef(position);
                }

                @Override
                protected void populateViewHolder(MoonlightsViewHolder viewHolder,
                                                  final Moonlight model, int position) {
                    final DatabaseReference moonlightRef = getRef(position);
                    final String moonlightKey = moonlightRef.getKey();

                    if (model.getTitle() != null) {
                        viewHolder.displayTitle(model.getTitle());
                    } else {
                        viewHolder.titleAppCompatTextView.setVisibility(View.GONE);
                    }

                    if (model.getContent() != null) {
                        viewHolder.displayContent(model.getContent());
                    } else {
                        viewHolder.contentAppCompatTextView.setVisibility(View.GONE);
                    }

                    if (model.getImageName() != null) {
                        Log.i(TAG, "populateViewHolder: " + model.getImageName());
                        viewHolder.photoAppCompatImageView.setImageResource(R.drawable.ic_cloud_download_white_48dp);
                        viewHolder.photoAppCompatImageView.setTag(model.getImageName());
                        viewHolder.displayImage(getActivity(), model.getImageUrl());
                    } else {
                        viewHolder.photoAppCompatImageView.setVisibility(View.GONE);
                    }

                    if (model.getColor() != 0) {
                        viewHolder.setColor(model.getColor());
                    } else {
                        viewHolder.setColor(0);
                    }

                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isLogin) {
                                Intent intent = new Intent(getActivity(), MoonlightDetailActivity.class);
                                if (isTrash()) {
                                    Log.d(TAG, "onClick: trash");
                                    intent.putExtra("writeoredit", 2);
                                    intent.putExtra("keyid", moonlightKey);
                                    startActivity(intent);
                                } else {
                                    Log.d(TAG, "onClick: edit");
                                    intent.putExtra("writeoredit", 1);
                                    intent.putExtra("keyid", moonlightKey);
                                    startActivity(intent);
                                }
                            }

                        }
                    });
                    viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            deleteFlag = true;
                            return deleteFlag;
                        }
                    });

                }

            };

            mAdapter.notifyDataSetChanged();

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
        inflater.inflate(R.menu.trash_menu, menu);
        //this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isLogin) {
            switch (item.getItemId()) {
                case R.id.menu_empty_trash:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_empty_trash, null);
                    builder.setTitle(R.string.dialog_empty_trash_title).setView(view)
                            .setPositiveButton("Empty Trash", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    emptyTrash();
                                }
                            }).setNegativeButton("Cancel", null).create().show();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

    public abstract boolean isTrash();

    public void emptyTrash() {
        try {
            FirebaseDatabase.getInstance().getReference().child("users-moonlight")
                    .child(getUid()).child("trash").removeValue(
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Log.d(TAG, "emptyTrash onComplete: ");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void busAction(BusEvent busEvent) {
        //这里更新视图或者后台操作,从busAction获取传递参数.
        if (busEvent != null) {
            if (busEvent.getFlag() == Constants.BUS_FLAG_SIGNOUT) ;
            {
                isLogin = false;
            }
        }
    }
}
