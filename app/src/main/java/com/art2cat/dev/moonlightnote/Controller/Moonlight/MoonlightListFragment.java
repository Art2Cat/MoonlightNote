package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
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
import com.art2cat.dev.moonlightnote.Utils.MoonlightEncryptUtils;
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

import static com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils.emptyNote;
import static com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils.emptyTrash;


/**
 * Created by art2cat
 * on 9/17/16.
 */
public abstract class MoonlightListFragment extends Fragment {
    private static final String TAG = "MoonlightListFragment";
    public LinearLayoutCompat mTransitionItem;
    private DatabaseReference mDatabase;
    private FDatabaseUtils mFDatabaseUtils;
    private FirebaseRecyclerAdapter<Moonlight, MoonlightViewHolder> mFirebaseRecyclerAdapter;
    private Toolbar mToolbar;
    private Toolbar mToolbar2;
    private FloatingActionButton mFAB;
    private AppBarLayout.LayoutParams mParams;
    private RecyclerView mRecyclerView;
    private RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private Moonlight mMoonlight;
    private Menu mMenu;
    private MenuInflater mMenuInflater;
    private boolean isLogin = true;
    private boolean isToolbarScroll = false;
    private boolean isInflate = false;
    private boolean isNotify;

    public MoonlightListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取Bus单例，并注册
        EventBus.getDefault().register(this);
        mFDatabaseUtils = FDatabaseUtils.newInstance(getActivity(), getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView: ");
        View rootView = inflater.inflate(R.layout.fragment_moonlight, container, false);

        MoonlightActivity moonlightActivity = (MoonlightActivity) getActivity();
        mToolbar = moonlightActivity.mToolbar;
        mToolbar2 = moonlightActivity.mToolbar2;
        mFAB = moonlightActivity.mFAB;
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
        isNotify = true;
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

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (!isTrash()) {
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        isScroll= false;
//                        Log.d(TAG, "onScrollStateChanged: " + mFAB.getVisibility());
//                        Log.d(TAG, "onScrollStateChanged: S 0");
//                            showFAB(mFAB);
//                    }
//                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//                        hideFAB(mFAB);
//                    }
//                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
//                        hideFAB(mFAB);
//                    }
//                }

            }
        });
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
        mFDatabaseUtils.removeListener();
        mFirebaseRecyclerAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
    }

    private void setAdapter() {

        Query moonlightsQuery = getQuery(mDatabase);
        if (moonlightsQuery != null) {
            mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Moonlight, MoonlightViewHolder>(Moonlight.class, R.layout.moonlight_item, MoonlightViewHolder.class,
                    moonlightsQuery) {

                @Override
                protected void populateViewHolder(MoonlightViewHolder viewHolder, Moonlight model, final int position) {
                    DatabaseReference moonlightRef = getRef(position);
                    String moonlightKey = moonlightRef.getKey();

                    Moonlight moonlightD = MoonlightEncryptUtils.decryptMoonlight(model);

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
                        viewHolder.mImage.setImageResource(R.drawable.ic_cloud_download_white_48dp);
//                        viewHolder.mImage.setTag(moonlightD.getImageName());
                        viewHolder.displayImage(getActivity(), moonlightD.getImageUrl());
                    } else {
                        viewHolder.mImage.setVisibility(View.GONE);
                    }

                    if (moonlightD.getColor() != 0) {
                        viewHolder.setColor(moonlightD.getColor());
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            viewHolder.setColor(getActivity().getResources().getColor(R.color.white, null));
                        } else {
                            viewHolder.setColor(getActivity().getResources().getColor(R.color.white));
                        }
                    }

                    if (moonlightD.getAudioName() != null) {
                        StorageUtils.downloadAudio(
                                FirebaseStorage.getInstance().getReference(),
                                getUid(), moonlightD.getAudioName());
                        viewHolder.mAudio.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mAudio.setVisibility(View.GONE);
                    }

                    mTransitionItem = viewHolder.mTransitionItem;
                    viewHolder.itemView.setOnClickListener(v -> {
                        Log.d(TAG, "onClick: " + isLogin);
                        if (isLogin) {
                            Intent intent = new Intent(getActivity(), MoonlightDetailActivity.class);
                            if (isTrash()) {
                                Log.d(TAG, "onClick: trash");
                                intent.putExtra("Fragment", Constants.EXTRA_TRASH_FRAGMENT);
                                intent.putExtra("moonlight", moonlightD);
                                startActivity(intent);
                                BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null);
                            } else {
                                Log.d(TAG, "onClick: edit");
                                intent.putExtra("Fragment", Constants.EXTRA_EDIT_FRAGMENT);
                                intent.putExtra("moonlight", moonlightD);
                                startActivity(intent);
                                BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null);
                            }
                            isNotify = false;
                            changeToolbar(null, 1);
                            setParams(0);
                        }

                    });

                    viewHolder.itemView.setOnLongClickListener(v -> {
                        Log.d(TAG, "onLongClick: " + moonlightKey);
                        Log.d(TAG, "onLongClick1: " + moonlightD.getId());
                        Log.d(TAG, "onLongClick2: " + position);
                        if (moonlightKey.equals(moonlightD.getId())) {
                            if (!isTrash()) {
                                setParams(1);
                                changeToolbar(moonlightD, 0);
                                isToolbarScroll = true;
                            } else {
                                changeToolbar(moonlightD, 0);
                                setParams(1);
                                isToolbarScroll = true;
                            }
                        }
                        return true;

                    });
                }
            };


//            mFirebaseRecyclerAdapter.notifyDataSetChanged();
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
                    Log.d(TAG, "onItemRangeInserted: " + itemCount);
                    mRecyclerView.smoothScrollToPosition(mFirebaseRecyclerAdapter.getItemCount());
                    if (isNotify) {
                        notifyChange();
                    }
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    if (isNotify) {
                        notifyChange();
                    }
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                }
            };

            mFirebaseRecyclerAdapter.registerAdapterDataObserver(mAdapterDataObserver);

        }
    }

    private void notifyChange() {
        new Handler().postDelayed(() -> mFirebaseRecyclerAdapter.notifyDataSetChanged(), 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFirebaseRecyclerAdapter != null) {
            Log.d(TAG, "cleanup");
            mFirebaseRecyclerAdapter.cleanup();
        }

        mFDatabaseUtils.removeListener();
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
            case R.id.action_clear_data:
                ConfirmationDialogFragment emptyNote = ConfirmationDialogFragment
                        .newInstance(getString(R.string.dialog_empty_note_title),
                                getString(R.string.dialog_empty_note_content),
                                Constants.EXTRA_TYPE_CDF_EMPTY_NOTE);
                emptyNote.show(getFragmentManager(), "Empty Note");
                break;
            case R.id.action_export_data:
                mFDatabaseUtils.exportNote();
                break;
            case R.id.menu_empty_trash:
                ConfirmationDialogFragment emptyTrash = ConfirmationDialogFragment
                        .newInstance(getString(R.string.dialog_empty_trash_title),
                                getString(R.string.dialog_empty_trash_content),
                                Constants.EXTRA_TYPE_CDF_EMPTY_TRASH);
                emptyTrash.show(getFragmentManager(), "Empty Trash");
                getActivity().setTitle(R.string.fragment_trash);
                break;
            case R.id.action_restore:
                FDatabaseUtils.restoreToNote(getUid(), mMoonlight);
                getActivity().setTitle(R.string.fragment_trash);
                changeOptionsMenu(3);
                break;
            case R.id.action_trash_delete_forever:
                StorageUtils.removePhoto(null, getUid(), mMoonlight.getImageName());
                StorageUtils.removeAudio(null, getUid(), mMoonlight.getAudioName());
                FDatabaseUtils.removeMoonlight(getUid(), mMoonlight.getId(), Constants.EXTRA_TYPE_DELETE_TRASH);
                mMoonlight = null;
                getActivity().setTitle(R.string.fragment_trash);
                changeOptionsMenu(3);
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
                case Constants.BUS_FLAG_EMPTY_NOTE:
                    emptyNote(getUid());
                    break;
                case Constants.BUS_FLAG_MAKE_COPY_DONE:
                    isNotify = false;
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
                                getActivity().setTitle(R.string.app_name);
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
                                getActivity().setTitle(R.string.app_name);
                                break;
                            case R.id.action_make_a_copy:
                                FDatabaseUtils.addMoonlight(getUid(),
                                        moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
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
                                FDatabaseUtils.restoreToNote(getUid(), moonlight);
                                getActivity().setTitle(R.string.fragment_trash);
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
                                getActivity().setTitle(R.string.fragment_trash);
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
