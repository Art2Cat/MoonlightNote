package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.art2cat.dev.moonlightnote.Controller.CommonDialogFragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.CustomView.BaseFragment;
import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.Utils.Firebase.StorageUtils;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.Utils.MoonlightEncryptUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

import static android.R.attr.tag;


/**
 * Created by art2cat
 * on 9/17/16.
 */
public abstract class MoonlightListFragment extends BaseFragment {
    private static final String TAG = "MoonlightListFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Moonlight, MoonlightViewHolder> mFirebaseRecyclerAdapter;
    private Toolbar mToolbar;
    private Toolbar mToolbar2;
    private AppBarLayout.LayoutParams mParams;
    private RecyclerView mRecyclerView;
    private RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private Moonlight mMoonlight;
    private Menu mMenu;
    private MenuInflater mMenuInflater;
    private boolean isLogin = true;
    private boolean isInflate = false;

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

        MoonlightActivity moonlightActivity = (MoonlightActivity) mActivity;
        mToolbar = moonlightActivity.mToolbar;
        mToolbar2 = moonlightActivity.mToolbar2;
        mParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();


        if (isTrash()) {
            mToolbar.setTitle(R.string.fragment_trash);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mToolbar.setBackgroundColor(getResources().getColor(R.color.grey,
                        mActivity.getTheme()));
//                mActivity.getWindow().setStatusBarColor(getResources().getColor(R.color.grey_dark,
//                        mActivity.getTheme()));
            } else {
                mToolbar.setBackgroundColor(getResources().getColor(R.color.grey));
//                mActivity.getWindow().setStatusBarColor(getResources().getColor(R.color.grey_dark));
            }
        } else {
            mToolbar.setTitle(R.string.app_name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary, mActivity.getTheme()));
                mActivity.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark,
                        mActivity.getTheme()));
            } else {
                mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mActivity.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setHasOptionsMenu(true);

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
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        SlideInRightAnimator animator = new SlideInRightAnimator();
        animator.setInterpolator(new OvershootInterpolator());
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);
        setAdapter();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(mActivity).resumeTag(tag);

                } else {
                    Picasso.with(mActivity).pauseTag(tag);
                }
            }

        });

        mRecyclerView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                return false;
            }
        });

        setOverflowButtonColor(mActivity, 0xFFFFFFFF);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        isInflate = false;
        mFirebaseRecyclerAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
    }

    private void setAdapter() {

        Query moonlightsQuery = getQuery(mDatabase);
        if (moonlightsQuery != null) {
            mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Moonlight, MoonlightViewHolder>(Moonlight.class, R.layout.moonlight_item, MoonlightViewHolder.class,
                    moonlightsQuery) {

                @Override
                protected void populateViewHolder(final MoonlightViewHolder viewHolder, Moonlight model, final int position) {
                    DatabaseReference moonlightRef = getRef(position);
                    final String moonlightKey = moonlightRef.getKey();

                    final Moonlight moonlightD = MoonlightEncryptUtils.newInstance().decryptMoonlight(model);

                    if (moonlightD == null) return;

                    if (moonlightD.getTitle() != null) {
                        viewHolder.displayTitle(moonlightD.getTitle());
                    } else {
                        viewHolder.mTitle.setVisibility(View.GONE);
                    }

                    if (moonlightD.getContent() != null) {
                        viewHolder.displayContent(moonlightD.getContent());
                    } else {
                        viewHolder.mContent.setVisibility(View.GONE);
                    }

                    if (moonlightD.getImageName() != null) {
                        Log.i(TAG, "populateViewHolder: " + moonlightD.getImageName());
                        viewHolder.mImage.setImageResource(R.drawable.ic_cloud_download_black_24dp);
//                        viewHolder.mImage.setTag(moonlightD.getImageName());
                        viewHolder.displayImage(mActivity, moonlightD.getImageUrl());
                    } else {
                        viewHolder.mImage.setVisibility(View.GONE);
                    }

                    if (moonlightD.getColor() != 0) {
                        viewHolder.setColor(moonlightD.getColor());
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            viewHolder.setColor(mActivity.getResources().getColor(R.color.white, null));
                        } else {
                            viewHolder.setColor(mActivity.getResources().getColor(R.color.white));
                        }
                    }

                    if (moonlightD.getAudioName() != null) {
                        String audioName = moonlightD.getAudioName();
                        if (!isAudioFileExists(audioName)) {
                            StorageUtils.downloadAudio(
                                    FirebaseStorage.getInstance().getReference(),
                                    getUid(), moonlightD.getAudioName());
                        }
                        viewHolder.mAudio.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mAudio.setVisibility(View.GONE);
                    }

                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isLogin) {
                                moonlightD.setId(moonlightKey);
                                if (isTrash()) {
                                    Log.d(TAG, "onClick: trash");
                                    TrashDetailFragment trashDetailFragment = new TrashDetailFragment();
                                    trashDetailFragment.setArgs(moonlightD, 12);
//                                    getFragmentManager().beginTransaction()
//                                            .replace(R.id.main_fragment_container, trashDetailFragment)
//                                            .addToBackStack("trash")
//                                            .addSharedElement(viewHolder.itemView, viewHolder.itemView.getTransitionName())
//                                            .commit();
                                    FragmentUtils.replaceFragment(getFragmentManager(),
                                            R.id.main_fragment_container,
                                            trashDetailFragment,
                                            FragmentUtils.REPLACE_BACK_STACK);
                                    BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null);
                                } else {
                                    Log.d(TAG, "onClick: edit");
                                    EditMoonlightFragment editMoonlightFragment = new EditMoonlightFragment();
                                    editMoonlightFragment.setArgs(moonlightD, 0);
//                                    getFragmentManager().beginTransaction()
//                                            .replace(R.id.main_fragment_container, editMoonlightFragment)
//                                            .addToBackStack("moonlight")
//                                            .addSharedElement(viewHolder.itemView, viewHolder.itemView.getTransitionName())
//                                            .commit();
                                    FragmentUtils.replaceFragment(getFragmentManager(),
                                            R.id.main_fragment_container,
                                            editMoonlightFragment,
                                            FragmentUtils.REPLACE_BACK_STACK);
                                    BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null);
                                }
                                changeToolbar(null, 1);
                                setParams(0);
                            }
                        }
                    });

                    viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            moonlightD.setId(moonlightKey);
                            if (moonlightKey.equals(moonlightD.getId())) {
                                if (!isTrash()) {
                                    setParams(1);
                                    changeToolbar(moonlightD, 0);
                                } else {
                                    changeToolbar(moonlightD, 0);
                                    setParams(1);
                                }
                            }
                            return false;
                        }
                    });
                }
            };

            mRecyclerView.setAdapter(mFirebaseRecyclerAdapter);
            Log.d(TAG, "setAdapter");

            mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    super.onItemRangeChanged(positionStart, itemCount);
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    mRecyclerView.smoothScrollToPosition(mFirebaseRecyclerAdapter.getItemCount());
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                    mRecyclerView.smoothScrollToPosition(toPosition);
                }
            };

            mFirebaseRecyclerAdapter.registerAdapterDataObserver(mAdapterDataObserver);

        }
    }

    private boolean isAudioFileExists(String audioName) {
        File dir = new File(mActivity.getCacheDir(), "/audio");
        if (audioName.contains(".amr")) {
            File file = new File(dir, audioName);
            return file.exists();
        } else {
            File file = new File(dir, audioName + ".amr");
            return file.exists();
        }
    }


    private void notifyChange() {
        Log.d(TAG, "notifyChange: ");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFirebaseRecyclerAdapter.notifyDataSetChanged();
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFirebaseRecyclerAdapter != null) {
            Log.d(TAG, "cleanup");
            mFirebaseRecyclerAdapter.cleanup();
        }

        RefWatcher refWatcher = MoonlightApplication.getRefWatcher(mActivity);
        refWatcher.watch(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        mMenuInflater = inflater;
        if (!isTrash()) {
            changeOptionsMenu(3);
        } else {
            changeOptionsMenu(2);
        }
    }

    private void changeOptionsMenu(int type) {
        mMenu.clear();
        switch (type) {
            case 0:
                mMenuInflater.inflate(R.menu.menu_long_click_moonlight, mMenu);
                break;
            case 1:
                mMenuInflater.inflate(R.menu.menu_long_click_trash, mMenu);
                break;
            case 2:
                mMenuInflater.inflate(R.menu.menu_trash, mMenu);
                break;
            case 3:
                mMenuInflater.inflate(R.menu.menu_moonlight, mMenu);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_data:
                ConfirmationDialogFragment emptyNote = ConfirmationDialogFragment
                        .newInstance(getUid(), getString(R.string.dialog_empty_note_title),
                                getString(R.string.dialog_empty_note_content),
                                Constants.EXTRA_TYPE_CDF_EMPTY_NOTE);
                emptyNote.show(getFragmentManager(), "Empty Note");
                break;
            case R.id.menu_empty_trash:
                ConfirmationDialogFragment emptyTrash = ConfirmationDialogFragment
                        .newInstance(getUid(), getString(R.string.dialog_empty_trash_title),
                                getString(R.string.dialog_empty_trash_content),
                                Constants.EXTRA_TYPE_CDF_EMPTY_TRASH);
                emptyTrash.show(getFragmentManager(), "Empty Trash");
                mActivity.setTitle(R.string.fragment_trash);
                break;
            case R.id.action_restore:
                FDatabaseUtils.restoreToNote(getUid(), mMoonlight);
                mActivity.setTitle(R.string.fragment_trash);
                changeOptionsMenu(3);
                break;
            case R.id.action_trash_delete_forever:
                StorageUtils.removePhoto(null, getUid(), mMoonlight.getImageName());
                StorageUtils.removeAudio(null, getUid(), mMoonlight.getAudioName());
                FDatabaseUtils.removeMoonlight(getUid(), mMoonlight.getId(), Constants.EXTRA_TYPE_DELETE_TRASH);
                mMoonlight = null;
                mActivity.setTitle(R.string.fragment_trash);
                changeOptionsMenu(3);
                break;
        }
        setParams(0);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @SuppressWarnings("ConstantConditions")
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

    private void changeToolbar(final Moonlight moonlight, int type) {
        switch (type) {
            case 0:
                mToolbar.setVisibility(View.GONE);
                mToolbar2.setVisibility(View.VISIBLE);
                mToolbar2.setTitle(null);

                if (isTrash() && !isInflate) {
                    mToolbar2.getMenu().clear();
                    mToolbar2.inflateMenu(R.menu.menu_long_click_trash);
                } else {
                    if (!isInflate) {
                        mToolbar2.getMenu().clear();
                        mToolbar2.inflateMenu(R.menu.menu_long_click_moonlight);
                    }
                }


                mToolbar2.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                mToolbar2.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setParams(0);
                        changeToolbar(null, 1);
                        isInflate = true;
                    }
                });
                mToolbar2.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                FDatabaseUtils.moveToTrash(getUid(), moonlight);
                                mActivity.setTitle(R.string.app_name);
                                break;
                            case R.id.action_delete_forever:
                                if (moonlight.getImageUrl() != null) {
                                    StorageUtils.removePhoto(null, getUid(), moonlight.getImageName());
                                }
                                if (moonlight.getAudioUrl() != null) {
                                    StorageUtils.removeAudio(null, getUid(), moonlight.getAudioName());
                                }
                                FDatabaseUtils.removeMoonlight(getUid(),
                                        moonlight.getId(),
                                        Constants.EXTRA_TYPE_MOONLIGHT);
                                mActivity.setTitle(R.string.app_name);
                                break;
                            case R.id.action_make_a_copy:
                                FDatabaseUtils.addMoonlight(getUid(),
                                        moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
                                mActivity.setTitle(R.string.app_name);
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
                                mActivity.setTitle(R.string.app_name);
                                break;
                            case R.id.action_restore:
                                FDatabaseUtils.restoreToNote(getUid(), moonlight);
                                mActivity.setTitle(R.string.fragment_trash);
                                break;
                            case R.id.action_trash_delete_forever:
                                if (moonlight.getImageUrl() != null) {
                                    StorageUtils.removePhoto(null, getUid(), moonlight.getImageName());
                                }
                                if (moonlight.getAudioUrl() != null) {
                                    StorageUtils.removeAudio(null, getUid(), moonlight.getAudioName());
                                }
                                FDatabaseUtils.removeMoonlight(getUid(),
                                        moonlight.getId(),
                                        Constants.EXTRA_TYPE_DELETE_TRASH);
                                mActivity.setTitle(R.string.fragment_trash);
                                break;
                        }
                        setParams(0);
                        changeToolbar(null, 1);
                        changeOptionsMenu(3);
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
}
