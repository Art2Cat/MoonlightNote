package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.art2cat.dev.moonlightnote.Controller.CommonActivity;
import com.art2cat.dev.moonlightnote.Controller.CommonDialogFragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.Utils.Firebase.StorageUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

import static com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils.emptyTrash;


/**
 * Created by art2cat
 * on 9/17/16.
 */
public abstract class MoonlightListFragment extends Fragment {
    private static final String TAG = "MoonlightListFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Moonlight, MoonlightViewHolder> mFirebaseRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private Moonlight moonlight;
    private Menu mMenu;
    private MenuInflater mMenuInflater;
    private boolean isLogin = true;
    private FDatabaseUtils mDatabaseUtils;


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
        Toolbar toolbar = ((MoonlightActivity)getActivity()).mToolbar;
        if (isTrash()) {
            getActivity().setTitle(R.string.fragment_trash);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.grey, getActivity().getTheme()));
            } else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        } else {
            getActivity().setTitle(R.string.app_name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary, getActivity().getTheme()));
            } else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        }
        setHasOptionsMenu(true);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabaseUtils = FDatabaseUtils.newInstance(getActivity(), mDatabase, getUid());
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

        FadeInDownAnimator animator = new FadeInDownAnimator();
        animator.setInterpolator(new OvershootInterpolator());
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);
        setAdapter();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                    if (mMenu != null && !isTrash()) {
//                        mMenu.clear();
//                        getActivity().setTitle(R.string.app_name);
//                    }
                }
            });
        } else {
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        if (mMenu != null && !isTrash()) {
//                            mMenu.clear();
//                            getActivity().setTitle(R.string.app_name);
//                        }
//                    }
                }
            });
        }

    }

    @Override
    public void onPause() {
        if (mMenu != null && !isTrash()) {
            mMenu.clear();
            getActivity().setTitle(R.string.app_name);
        }
        super.onPause();
    }

    private void setAdapter() {

        Query moonlightsQuery = getQuery(mDatabase);
        if (moonlightsQuery != null) {
            mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Moonlight, MoonlightViewHolder>(Moonlight.class, R.layout.moonlight_item, MoonlightViewHolder.class,
                    moonlightsQuery) {

                @Override
                protected void populateViewHolder(MoonlightViewHolder viewHolder, final Moonlight model, int position) {
                    DatabaseReference moonlightRef = getRef(position);
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            viewHolder.setColor(getActivity().getResources().getColor(R.color.white, null));
                        } else {
                            viewHolder.setColor(getActivity().getResources().getColor(R.color.white));
                        }
                    }

                    if (model.getAudioName() != null) {
                        StorageUtils.downloadAudio(FirebaseStorage.getInstance().getReference(), getUid(), model.getAudioName());
                        viewHolder.audioAppCompatImageView.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.audioAppCompatImageView.setVisibility(View.GONE);
                    }

                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: " + isLogin);
                            if (isLogin) {
                                Intent intent = new Intent(getActivity(), CommonActivity.class);
                                if (isTrash()) {
                                    Log.d(TAG, "onClick: trash");
                                    intent.putExtra("Fragment", Constants.EXTRA_TRASH_FRAGMENT);
                                    intent.putExtra("keyid", moonlightKey);
                                    startActivity(intent);
                                    BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null);
                                } else {
                                    Log.d(TAG, "onClick: edit");
                                    intent.putExtra("Fragment", Constants.EXTRA_EDIT_FRAGMENT);
                                    intent.putExtra("keyid", moonlightKey);
                                    startActivity(intent);
                                    BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null);
                                }
                            }

                        }
                    });
                    viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            moonlight = model;
                            if (!isTrash()) {
                                changeOptionsMenu(0);
                            } else {
                                changeOptionsMenu(1);
                            }
                            return true;
                        }
                    });
                }
            };


            //  mAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mFirebaseRecyclerAdapter);
            Log.d(TAG, "setAdapter");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFirebaseRecyclerAdapter != null) {
            Log.d(TAG, "cleanup");
            mFirebaseRecyclerAdapter.cleanup();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        mMenuInflater = inflater;
        if (isTrash()) {
            changeOptionsMenu(2);
        } else {
//            changeOptionsMenu(3);
        }
        //this.menu = menu;
    }

    private void changeOptionsMenu(int type) {
        mMenu.clear();
        switch (type) {
            case 0:
                mMenuInflater.inflate(R.menu.long_click_moonlight_menu, mMenu);
//                getActivity().setTitle(null);
                break;
            case 1:
                mMenuInflater.inflate(R.menu.long_click_trash_menu, mMenu);
//                getActivity().setTitle(R.string.fragment_trash);
                break;
            case 2:
                mMenuInflater.inflate(R.menu.trash_menu, mMenu);
//                getActivity().setTitle(R.string.fragment_trash);
                break;
            case 3:
                mMenuInflater.inflate(R.menu.moonlight_menu, mMenu);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_empty_trash:
                ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment
                        .newInstance(getString(R.string.dialog_empty_trash_title), getString(R.string.dialog_empty_trash_content), Constants.EXTRA_TYPE_CDF_EMPTY_TRASH);
                confirmationDialogFragment.show(getFragmentManager(), "Empty Trash");
                getActivity().setTitle(R.string.fragment_trash);
                break;
            case R.id.action_delete:
                mDatabaseUtils.moveToTrash(moonlight);
                getActivity().setTitle(R.string.app_name);
//                changeOptionsMenu(3);
                break;
            case R.id.action_delete_forever:
                StorageUtils.removePhoto(null, getUid(), moonlight.getImageName());
                StorageUtils.removeAudio(null, getUid(), moonlight.getAudioName());
                mDatabaseUtils.removeMoonlight(moonlight.getId(), Constants.EXTRA_TYPE_MOONLIGHT);
                moonlight = null;
                getActivity().setTitle(R.string.app_name);
//                changeOptionsMenu(3);
                break;
            case R.id.action_make_a_copy:
                mDatabaseUtils.addMoonlight(moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
                getActivity().setTitle(R.string.app_name);
//                changeOptionsMenu(3);
                break;
            case R.id.action_send:
                //启动Intent分享
                Intent in = new Intent(Intent.ACTION_SEND);
                in.setType("text/plain");
                if (moonlight.getTitle() != null) {
                    in.putExtra(Intent.EXTRA_TITLE, moonlight.getTitle());
                }

                if (moonlight.getContent() != null) {
                    in.putExtra(Intent.EXTRA_TEXT, moonlight.getContent());
                }

                if (moonlight.getImageUrl() != null) {
                    in.putExtra(Intent.EXTRA_TEXT, moonlight.getImageUrl());
                }
                //设置分享选择器
                in = Intent.createChooser(in, "Send to");
                startActivity(in);
                getActivity().setTitle(R.string.app_name);
//                changeOptionsMenu(3);
                break;
            case R.id.action_restore:
                mDatabaseUtils.restoreToNote(moonlight);
                getActivity().setTitle(R.string.fragment_trash);
                changeOptionsMenu(2);
                break;
            case R.id.action_trash_delete_forever:
                StorageUtils.removePhoto(null, getUid(), moonlight.getImageName());
                StorageUtils.removeAudio(null, getUid(), moonlight.getAudioName());
                mDatabaseUtils.removeMoonlight(moonlight.getId(), Constants.EXTRA_TYPE_DELETE_TRASH);
                moonlight = null;
                getActivity().setTitle(R.string.fragment_trash);
                changeOptionsMenu(2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

    public abstract boolean isTrash();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void busAction(BusEvent busEvent) {
        //这里更新视图或者后台操作,从busAction获取传递参数.
        if (busEvent != null) {
            switch (busEvent.getFlag()) {
                case Constants.BUS_FLAG_SIGN_OUT:
                    isLogin = false;
                    break;
                case Constants.BUS_FLAG_EMPTY_TRASH:
                    emptyTrash(getUid());
                    break;
            }
        }
    }

    private boolean isEmpty(Moonlight moonlight) {
        return moonlight.getImageUrl() != null || moonlight.getAudioUrl() != null || moonlight.getContent() != null
                || moonlight.getTitle() != null;
    }
}
