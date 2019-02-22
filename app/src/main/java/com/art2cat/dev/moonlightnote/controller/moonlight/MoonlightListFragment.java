package com.art2cat.dev.moonlightnote.controller.moonlight;

import static android.R.attr.tag;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.controller.BaseFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.model.BusEvent;
import com.art2cat.dev.moonlightnote.constants.Constants;
import com.art2cat.dev.moonlightnote.model.Moonlight;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.utils.MoonlightEncryptUtils;
import com.art2cat.dev.moonlightnote.utils.Utils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.utils.firebase.StorageUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.Objects;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by art2cat on 9/17/16.
 */
public abstract class MoonlightListFragment extends BaseFragment {

  private static final String TAG = MoonlightListFragment.class.getName();
  private DatabaseReference databaseReference;
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

    EventBus.getDefault().register(this);

    setRetainInstance(true);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View rootView = inflater.inflate(R.layout.fragment_moonlight, container, false);

    MoonlightActivity moonlightActivity = (MoonlightActivity) activity;
    mToolbar = moonlightActivity.mToolbar;
    mToolbar2 = moonlightActivity.mToolbar2;
    mParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();

    if (isTrash()) {
      mToolbar.setTitle(R.string.fragment_trash);
      mToolbar.setBackgroundColor(getResources().getColor(R.color.grey, activity.getTheme()));
    } else {
      mToolbar.setTitle(R.string.app_name);
      mToolbar
          .setBackgroundColor(getResources().getColor(R.color.colorPrimary, activity.getTheme()));
      activity.getWindow()
          .setStatusBarColor(
              getResources().getColor(R.color.colorPrimaryDark, activity.getTheme()));
      activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
    setHasOptionsMenu(true);

    // [START create_database_reference]
    databaseReference = FirebaseDatabase.getInstance().getReference();

    // [END create_database_reference]
    mRecyclerView = rootView.findViewById(R.id.recyclerView);
    mRecyclerView.setHasFixedSize(true);

    return rootView;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (isTrash()) {
      ((MoonlightActivity) activity).hideFAB();
      MoonlightActivity.isHome = false;
    }

    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(activity);
    mLinearLayoutManager.setReverseLayout(true);
    mLinearLayoutManager.setStackFromEnd(true);
    mRecyclerView.setLayoutManager(mLinearLayoutManager);

    SlideInRightAnimator animator = new SlideInRightAnimator();
    animator.setInterpolator(new OvershootInterpolator());
    mRecyclerView.setItemAnimator(animator);
    Objects.requireNonNull(mRecyclerView.getItemAnimator()).setAddDuration(500);
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
          Picasso.with(activity).resumeTag(tag);

        } else {
          Picasso.with(activity).pauseTag(tag);
        }
      }

    });

    mRecyclerView.setOnDragListener((view, dragEvent) -> false);

    setOverflowButtonColor(activity, 0xFFFFFFFF);

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    isInflate = false;
    mFirebaseRecyclerAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
  }

  private void setAdapter() {
    Query moonlightsQuery = getQuery(databaseReference);
    if (moonlightsQuery != null) {
      mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Moonlight, MoonlightViewHolder>(
          Moonlight.class,
          R.layout.moonlight_item,
          MoonlightViewHolder.class,
          moonlightsQuery) {

        @Override
        protected void populateViewHolder(final MoonlightViewHolder viewHolder, Moonlight model,
            final int position) {
          DatabaseReference moonlightRef = getRef(position);
          final String moonlightKey = moonlightRef.getKey();

          final Moonlight moonlightD = MoonlightEncryptUtils.newInstance().decryptMoonlight(model);

          if (moonlightD == null) {
            return;
          }

          if (Utils.isStringNotEmpty(moonlightD.getTitle())) {
            viewHolder.displayTitle(moonlightD.getTitle());
          } else {
            viewHolder.mTitle.setVisibility(View.GONE);
          }

          if (Utils.isStringNotEmpty(moonlightD.getContent())) {
            viewHolder.displayContent(moonlightD.getContent());
          } else {
            viewHolder.mContent.setVisibility(View.GONE);
          }

          if (Utils.isStringNotEmpty(moonlightD.getImageName())) {
            Log.i(TAG, "populateViewHolder: " + moonlightD.getImageName());
            viewHolder.mImage.setVisibility(View.VISIBLE);
            viewHolder.displayImage(activity, moonlightD.getImageUrl());
          } else {
            viewHolder.mImage.setVisibility(View.GONE);
          }

          if (moonlightD.getColor() != 0) {
            viewHolder.setColor(moonlightD.getColor());
          } else {
            viewHolder.setColor(activity.getResources().getColor(R.color.white, null));
          }

          if (Utils.isStringNotEmpty(moonlightD.getAudioName())) {
            String audioName = moonlightD.getAudioName();
            if (!isAudioFileExists(audioName)) {
              StorageUtils.downloadAudio(FirebaseStorage.getInstance().getReference(), getUid(),
                  moonlightD.getAudioName());
            }
            viewHolder.mAudio.setVisibility(View.VISIBLE);
          } else {
            viewHolder.mAudio.setVisibility(View.GONE);
          }

          viewHolder.itemView.setOnClickListener(view -> {
            if (isLogin) {
              moonlightD.setId(moonlightKey);
              if (isTrash()) {
                Log.d(TAG, "onClick: trash");
                TrashDetailFragment trashDetailFragment =
                    TrashDetailFragment.newInstance(moonlightD, 12);
                FragmentUtils.replaceFragment(getFragmentManager(), R.id.main_fragment_container,
                    trashDetailFragment, FragmentUtils.REPLACE_BACK_STACK);
                BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null);
              } else {
                Log.d(TAG, "onClick: edit");
                EditMoonlightFragment editMoonlightFragment =
                    EditMoonlightFragment.newInstance(moonlightD, 0);
                FragmentUtils.replaceFragment(getFragmentManager(), R.id.main_fragment_container,
                    editMoonlightFragment, FragmentUtils.REPLACE_BACK_STACK);
                BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null);
              }
              changeToolbar(null, 1);
              setParams(0);
            }
          });

          viewHolder.itemView.setOnLongClickListener(view -> {
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
    File dir = new File(activity.getCacheDir(), "/.audio");
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
    new Handler().postDelayed(() -> mFirebaseRecyclerAdapter.notifyDataSetChanged(), 1000);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mFirebaseRecyclerAdapter != null) {
      Log.d(TAG, "cleanup");
      mFirebaseRecyclerAdapter.cleanup();
    }

    RefWatcher refWatcher = MoonlightApplication.getRefWatcher(activity);
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
        emptyNote.show(activity.getFragmentManager(), "Empty Note");
        break;
      case R.id.menu_empty_trash:
        ConfirmationDialogFragment emptyTrash = ConfirmationDialogFragment
            .newInstance(getUid(), getString(R.string.dialog_empty_trash_title),
                getString(R.string.dialog_empty_trash_content),
                Constants.EXTRA_TYPE_CDF_EMPTY_TRASH);
        emptyTrash.show(activity.getFragmentManager(), "Empty Trash");
        activity.setTitle(R.string.fragment_trash);
        break;
      case R.id.action_restore:
        FDatabaseUtils.restoreToNote(getUid(), mMoonlight);
        activity.setTitle(R.string.fragment_trash);
        changeOptionsMenu(3);
        break;
      case R.id.action_trash_delete_forever:
        StorageUtils.removePhoto(null, getUid(), mMoonlight.getImageName());
        StorageUtils.removeAudio(null, getUid(), mMoonlight.getAudioName());
        FDatabaseUtils
            .removeMoonlight(getUid(), mMoonlight.getId(), Constants.EXTRA_TYPE_DELETE_TRASH);
        mMoonlight = null;
        activity.setTitle(R.string.fragment_trash);
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

  public String getUid() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }

  public abstract Query getQuery(DatabaseReference databaseReference);

  public abstract boolean isTrash();

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void busAction(BusEvent busEvent) {
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
        mParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
            AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        break;
      case 1:
        mParams.setScrollFlags(0);
        break;
    }
  }

  private void changeToolbar(Moonlight moonlight, int type) {
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
        mToolbar2.setNavigationOnClickListener(view -> {
          setParams(0);
          changeToolbar(null, 1);
          isInflate = true;
        });
        mToolbar2.setOnMenuItemClickListener(item -> {
          switch (item.getItemId()) {
            case R.id.action_delete:
              FDatabaseUtils.moveToTrash(getUid(), moonlight);
              activity.setTitle(R.string.app_name);
              break;
            case R.id.action_delete_forever:
              if (moonlight.getImageUrl() != null) {
                StorageUtils.removePhoto(null, getUid(), moonlight.getImageName());
              }
              if (moonlight.getAudioUrl() != null) {
                StorageUtils.removeAudio(null, getUid(), moonlight.getAudioName());
              }
              FDatabaseUtils
                  .removeMoonlight(getUid(), moonlight.getId(), Constants.EXTRA_TYPE_MOONLIGHT);
              activity.setTitle(R.string.app_name);
              break;
            case R.id.action_make_a_copy:
              FDatabaseUtils.addMoonlight(getUid(), moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
              activity.setTitle(R.string.app_name);
              break;
            case R.id.action_send:
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
              in = Intent.createChooser(in, "Send to");
              startActivity(in);
              activity.setTitle(R.string.app_name);
              break;
            case R.id.action_restore:
              FDatabaseUtils.restoreToNote(getUid(), moonlight);
              activity.setTitle(R.string.fragment_trash);
              break;
            case R.id.action_trash_delete_forever:
              if (moonlight.getImageUrl() != null) {
                StorageUtils.removePhoto(null, getUid(), moonlight.getImageName());
              }
              if (moonlight.getAudioUrl() != null) {
                StorageUtils.removeAudio(null, getUid(), moonlight.getAudioName());
              }
              FDatabaseUtils
                  .removeMoonlight(getUid(), moonlight.getId(), Constants.EXTRA_TYPE_DELETE_TRASH);
              activity.setTitle(R.string.fragment_trash);
              break;
          }
          setParams(0);
          changeToolbar(null, 1);
          changeOptionsMenu(3);
          isInflate = true;
          return false;
        });
        break;
      case 1:
        mToolbar2.setVisibility(View.GONE);
        mToolbar.setVisibility(View.VISIBLE);
        break;
    }
  }
}
