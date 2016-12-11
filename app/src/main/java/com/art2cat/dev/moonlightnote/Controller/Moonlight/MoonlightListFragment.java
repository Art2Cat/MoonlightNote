package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
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

import com.art2cat.dev.moonlightnote.Controller.CommonDialogFragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.MoonlightDetailActivity;
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

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

import static com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils.emptyTrash;


/**
 * Created by art2cat
 * on 9/17/16.
 */
public abstract class MoonlightListFragment extends Fragment {
    private static final String TAG = "MoonlightListFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Moonlight, MoonlightViewHolder> mFirebaseRecyclerAdapter;
    private MoonlightActivity mMoonlightActivity;
    private Toolbar mToolbar;
    private Toolbar mToolbar2;
    private AppBarLayout.LayoutParams mParams;
    private RecyclerView mRecyclerView;
    public LinearLayoutCompat mTransitionItem;
    private AppCompatImageView mImageView;
    private Moonlight moonlight;
    private Menu mMenu;
    private MenuInflater mMenuInflater;
    private boolean isLogin = true;
    private boolean isToolbarScroll = false;
    private boolean isInflate = false;
    ;
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
        mMoonlightActivity = (MoonlightActivity) getActivity();

        mToolbar = mMoonlightActivity.mToolbar;
        mToolbar2 = mMoonlightActivity.mToolbar2;
        mParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();


        if (isTrash()) {
            getActivity().setTitle(R.string.fragment_trash);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mToolbar.setBackgroundColor(getResources().getColor(R.color.grey, getActivity().getTheme()));
            } else {
                mToolbar.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        } else {
            getActivity().setTitle(R.string.app_name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary, getActivity().getTheme()));
            } else {
                mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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

        SlideInRightAnimator animator = new SlideInRightAnimator();
//        animator.setInterpolator(new OvershootInterpolator());
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
                    Log.d(TAG, "onScrollChange: " + isToolbarScroll);
                    if (mMenu != null && !isTrash() && !isToolbarScroll) {
                        mMenu.clear();
                        setParams(0);
                        getActivity().setTitle(R.string.app_name);
                    }
                }
            });
        } else {
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.d(TAG, "onScrollChange: " + isToolbarScroll);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        setParams(0);
                        if (mMenu != null && !isTrash() && !isToolbarScroll) {
                            mMenu.clear();
                            getActivity().setTitle(R.string.app_name);
                        }
                    }
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
                        viewHolder.mTitle.setVisibility(View.GONE);
                    }

                    if (model.getContent() != null) {
                        viewHolder.displayContent(model.getContent());
                    } else {
                        viewHolder.mContent.setVisibility(View.GONE);
                    }

                    if (model.getImageName() != null) {
                        Log.i(TAG, "populateViewHolder: " + model.getImageName());
                        viewHolder.mImage.setImageResource(R.drawable.ic_cloud_download_white_48dp);
                        viewHolder.mImage.setTag(model.getImageName());
                        viewHolder.displayImage(getActivity(), model.getImageUrl());
                    } else {
                        viewHolder.mImage.setVisibility(View.GONE);
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
                        viewHolder.mAudio.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mAudio.setVisibility(View.GONE);
                    }
                    mTransitionItem = viewHolder.mTransitionItem;
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: " + isLogin);
                            if (isLogin) {
                                Intent intent = new Intent(getActivity(), MoonlightDetailActivity.class);
                                if (isTrash()) {
                                    Log.d(TAG, "onClick: trash");
                                    intent.putExtra("Fragment", Constants.EXTRA_TRASH_FRAGMENT);
                                    intent.putExtra("moonlight", model);
                                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                                            getActivity(),
                                            mTransitionItem,
                                            mTransitionItem.getTransitionName()).toBundle();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Bundle bundle1 = ActivityOptions.makeClipRevealAnimation(mTransitionItem,
                                                mTransitionItem.getScrollX(),
                                                mTransitionItem.getScrollY(),
                                                mTransitionItem.getWidth(),
                                                mTransitionItem.getHeight()).toBundle();
                                        startActivity(intent, bundle1);
                                    } else {
                                        startActivity(intent, bundle);
                                    }

//                                    startActivity(intent);
                                    BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null);
                                } else {
                                    Log.d(TAG, "onClick: edit");
                                    intent.putExtra("Fragment", Constants.EXTRA_EDIT_FRAGMENT);
                                    intent.putExtra("moonlight", model);
                                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                                            getActivity(),
                                            mTransitionItem,
                                            mTransitionItem.getTransitionName()).toBundle();

                                    startActivity(intent, bundle);
//                                    startActivity(intent);
                                    BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null);
                                }
                                changeToolbar(1);
                                setParams(0);
                            }

                        }
                    });
                    viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            moonlight = model;
                            if (!isTrash()) {
                                setParams(1);
                                changeToolbar(0);
                                isToolbarScroll = true;
                            } else {
                                //changeOptionsMenu(1);
                                changeToolbar(0);
                                setParams(1);
                                isToolbarScroll = true;
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
                break;
            case 1:
                mMenuInflater.inflate(R.menu.long_click_trash_menu, mMenu);
                break;
            case 2:
                mMenuInflater.inflate(R.menu.trash_menu, mMenu);
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
        isToolbarScroll = false;
        setParams(0);
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

    private void setParams(int type) {
        switch (type) {
            case 0:
                mParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                break;
            case 1:
                mParams.setScrollFlags(0);
                break;
        }
    }

    private void changeToolbar(int type) {
        switch (type) {
            case 0:
                mToolbar.setVisibility(View.GONE);
                mToolbar2.setVisibility(View.VISIBLE);
                mToolbar2.setTitle(null);


                if (isTrash() && !isInflate) {
                    mToolbar2.getMenu().clear();
                    mToolbar2.inflateMenu(R.menu.long_click_trash_menu);
                } else {
                    if (!isInflate) {
                        mToolbar2.getMenu().clear();
                        mToolbar2.inflateMenu(R.menu.long_click_moonlight_menu);
                    }
                }


                mToolbar2.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                mToolbar2.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeToolbar(1);
                        setParams(0);
                        isInflate = true;
                    }
                });
                mToolbar2.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                mDatabaseUtils.moveToTrash(moonlight);
                                getActivity().setTitle(R.string.app_name);
                                break;
                            case R.id.action_delete_forever:
                                StorageUtils.removePhoto(null, getUid(), moonlight.getImageName());
                                StorageUtils.removeAudio(null, getUid(), moonlight.getAudioName());
                                mDatabaseUtils.removeMoonlight(moonlight.getId(), Constants.EXTRA_TYPE_MOONLIGHT);
                                moonlight = null;
                                getActivity().setTitle(R.string.app_name);
                                break;
                            case R.id.action_make_a_copy:
                                mDatabaseUtils.addMoonlight(moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
                                getActivity().setTitle(R.string.app_name);
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
                                break;
                            case R.id.action_restore:
                                mDatabaseUtils.restoreToNote(moonlight);
                                getActivity().setTitle(R.string.fragment_trash);
                                break;
                            case R.id.action_trash_delete_forever:
                                StorageUtils.removePhoto(null, getUid(), moonlight.getImageName());
                                StorageUtils.removeAudio(null, getUid(), moonlight.getAudioName());
                                mDatabaseUtils.removeMoonlight(moonlight.getId(), Constants.EXTRA_TYPE_DELETE_TRASH);
                                moonlight = null;
                                getActivity().setTitle(R.string.fragment_trash);
                                break;
                        }
                        changeToolbar(1);

                        isInflate = true;
                        return false;
                    }
                });
                break;
            case 1:
                mToolbar2.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean isEmpty(Moonlight moonlight) {
        return moonlight.getImageUrl() != null || moonlight.getAudioUrl() != null || moonlight.getContent() != null
                || moonlight.getTitle() != null;
    }
}
