package com.art2cat.dev.moonlightnote.controller.moonlight;


import static android.app.Activity.RESULT_OK;
import static com.art2cat.dev.moonlightnote.constants.Constants.ALBUM_CHOOSE;
import static com.art2cat.dev.moonlightnote.constants.Constants.RECORD_AUDIO;
import static com.art2cat.dev.moonlightnote.constants.Constants.TAKE_PICTURE;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.constants.ColorConstants;
import com.art2cat.dev.moonlightnote.constants.Constants;
import com.art2cat.dev.moonlightnote.controller.BaseFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.CircleProgressDialogFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.ColorPickerDialogFragment;
import com.art2cat.dev.moonlightnote.model.BusEvent;
import com.art2cat.dev.moonlightnote.model.Moonlight;
import com.art2cat.dev.moonlightnote.utils.AudioPlayer;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.utils.FragmentBackHandler;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.utils.Utils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.utils.firebase.StorageUtils;
import com.art2cat.dev.moonlightnote.utils.material_animation.CircularRevealUtils;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.leakcanary.RefWatcher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MoonlightDetailFragment extends BaseFragment
    implements View.OnClickListener, FragmentBackHandler {

  private static final String TAG = MoonlightDetailFragment.class.getName();
  private final Handler handler = new Handler();
  private View view;
  private Toolbar toolbar;
  private ContentFrameLayout viewParent;
  private LinearLayoutCompat bottomBarContainer;
  private LinearLayoutCompat mAudioContainer;
  private TextInputLayout mContentTextInputLayout;
  private TextInputEditText mTitle;
  private TextInputEditText mContent;
  private AppCompatTextView mShowDuration;
  private AppCompatButton mBottomBarLeft;
  private AppCompatButton mBottomBarRight;
  private AppCompatButton mDeleteAudio;
  private AppCompatButton mPlayingAudio;
  private CardView mAudioCardView;
  private PhotoView mImage;
  private CircleProgressDialogFragment mCircleProgressDialogFragment;
  private CoordinatorLayout mCoordinatorLayout;
  private BottomSheetBehavior mRightBottomSheetBehavior;
  private BottomSheetBehavior mLeftBottomSheetBehavior;
  private InputMethodManager mInputMethodManager;
  private Moonlight moonlight;
  private MoonlightActivity.FragmentOnTouchListener mFragmentOnTouchListener;
  private boolean mCreateFlag = true;
  private boolean mEditFlag = false;
  private boolean mEditable = true;
  private boolean mStartPlaying = true;
  private boolean isLeftOrRight;
  private String mUserId;
  private String mKeyId;
  private int mPaddingBottom;
  private int mBottomBarHeight;
  //    private Uri fileUri = null;
  private StorageReference mStorageReference;
  private String mImageFileName;
  private String mAudioFileName;
  private Uri mDownloadIUrl;
  private Uri mDownloadAUrl;
  private AudioPlayer mAudioPlayer;
  private SparseIntArray mColorMaps;

  public MoonlightDetailFragment() {
    // Required empty public constructor
  }

  @TargetApi(Build.VERSION_CODES.N)
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    setHasOptionsMenu(true);
    EventBus.getDefault().register(this);
    mUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    mStorageReference = firebaseStorage.getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE);

    mInputMethodManager = (InputMethodManager) activity
        .getSystemService(Context.INPUT_METHOD_SERVICE);

    mPaddingBottom = getResources().getDimensionPixelOffset(R.dimen.padding_bottom);

    if (Objects.nonNull(getArguments())) {
      moonlight = getArguments().getParcelable("moonlight");
      if (Objects.nonNull(moonlight)) {
        mKeyId = moonlight.getId();
        if (BuildConfig.DEBUG) {
          Log.d(TAG, "keyId: " + mKeyId);
        }
      }
      int trashTag = getArguments().getInt("flag");
      if (trashTag == 0) {
        mEditFlag = true;
        mCreateFlag = false;
      } else {
        mEditFlag = true;
        mCreateFlag = false;
        mEditable = false;
      }
    } else {
      moonlight = new Moonlight();
    }

    ((DrawerLocker) activity).setDrawerEnabled(false);

    initColor();

  }

  private void initColor() {
    mColorMaps = new SparseIntArray();
    mColorMaps.put(ColorConstants.AMBER, ColorConstants.AMBER_DARK);
    mColorMaps.put(ColorConstants.BLUE, ColorConstants.BLUE_DARK);
    mColorMaps.put(ColorConstants.BLUE_GRAY, ColorConstants.BLUE_GRAY_DARK);
    mColorMaps.put(ColorConstants.BROWN, ColorConstants.BROWN_DARK);
    mColorMaps.put(ColorConstants.CYAN, ColorConstants.CYAN_DARK);
    mColorMaps.put(ColorConstants.DEEP_ORANGE, ColorConstants.DEEP_ORANGE_DARK);
    mColorMaps.put(ColorConstants.DEEP_PURPLE, ColorConstants.DEEP_PURPLE_DARK);
    mColorMaps.put(ColorConstants.GREEN, ColorConstants.GREEN_DARK);
    mColorMaps.put(ColorConstants.GREY, ColorConstants.GREY_DARK);
    mColorMaps.put(ColorConstants.INDIGO, ColorConstants.INDIGO_DARK);
    mColorMaps.put(ColorConstants.LIGHT_BLUE, ColorConstants.LIGHT_BLUE_DARK);
    mColorMaps.put(ColorConstants.LIGHT_GREEN, ColorConstants.LIGHT_GREEN_DARK);
    mColorMaps.put(ColorConstants.LIME, ColorConstants.LIME_DARK);
    mColorMaps.put(ColorConstants.ORANGE, ColorConstants.ORANGE_DARK);
    mColorMaps.put(ColorConstants.PINK, ColorConstants.PINK_DARK);
    mColorMaps.put(ColorConstants.PURPLE, ColorConstants.PURPLE_DARK);
    mColorMaps.put(ColorConstants.RED, ColorConstants.RED_DARK);
    mColorMaps.put(ColorConstants.TEAL, ColorConstants.TEAL_DARK);
    mColorMaps.put(ColorConstants.YELLOW, ColorConstants.YELLOW_DARK);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_moonlight_detail, container, false);

    toolbar = ((MoonlightActivity) activity).mToolbar2;
    toolbar.setVisibility(View.VISIBLE);
    toolbar.setBackgroundColor(getResources().getColor(R.color.white, null));
    AppBarLayout.LayoutParams params =
        (AppBarLayout.LayoutParams) ((MoonlightActivity) activity).mToolbar.getLayoutParams();
    params.setScrollFlags(0);
    ((MoonlightActivity) activity).mToolbar.setLayoutParams(params);
    toolbar.setLayoutParams(params);
    ((MoonlightActivity) activity).setSupportActionBar(toolbar);
    ((MoonlightActivity) activity).mToolbar.setVisibility(View.GONE);
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey_700_24dp);
    toolbar.setNavigationOnClickListener(view -> activity.onBackPressed());

    viewParent = view.findViewById(R.id.view_parent);
    mTitle = view.findViewById(R.id.title_TIET);
    mContent = view.findViewById(R.id.content_TIET);
    mContentTextInputLayout = view.findViewById(R.id.content_TIL);
    mImage = view.findViewById(R.id.moonlight_image);
    mAudioCardView = view.findViewById(R.id.audio_container);
    mAudioContainer = view.findViewById(R.id.audio_container_inner);

    mDeleteAudio = view.findViewById(R.id.delete_audio);
    mPlayingAudio = view.findViewById(R.id.playing_audio_button);
    mShowDuration = view.findViewById(R.id.moonlight_audio_duration);
    ProgressBar audioPlayerPB = view.findViewById(R.id.moonlight_audio_progressBar);
    AppCompatTextView displayTime = view.findViewById(R.id.bottom_bar_display_time);
    mCoordinatorLayout = view.findViewById(R.id.bottom_sheet_container);
    bottomBarContainer = view.findViewById(R.id.bottom_bar_container);
    mBottomBarLeft = view.findViewById(R.id.bottom_bar_left);
    mBottomBarRight = view.findViewById(R.id.bottom_bar_right);
    mAudioPlayer = AudioPlayer.getInstance(audioPlayerPB, mShowDuration);

    mCircleProgressDialogFragment =
        CircleProgressDialogFragment.newInstance(getString(R.string.prograssBar_uploading));

    if (mEditable) {
      long date = System.currentTimeMillis();
      moonlight.setDate(date);
      String time = Utils.timeFormat(activity.getApplicationContext(), new Date(date));
      if (Objects.nonNull(time)) {
        String timeFormat = "Edited: " + time;
        displayTime.setText(timeFormat);
      }
      onCheckSoftKeyboardState(view);
      mImage.setOnClickListener(this);
      mDeleteAudio.setOnClickListener(this);
      mPlayingAudio.setOnClickListener(this);
      showBottomSheet();

      mBottomBarLeft.setOnClickListener(this);
      mBottomBarRight.setOnClickListener(this);
    }

    mTitle.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String title = s.toString();
        moonlight.setTitle(title);
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    mContent.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String content = s.toString();
        moonlight.setContent(content);
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    return view;
  }

  private void initView(boolean editable) {
    if (editable) {
      if (Objects.nonNull(moonlight.getTitle())) {
        mTitle.setText(moonlight.getTitle());
      }
    } else {
      mTitle.setEnabled(false);
      if (Objects.nonNull(moonlight.getTitle())) {
        mTitle.setText(moonlight.getTitle());
      }
    }
    if (Objects.nonNull(moonlight.getContent())) {
      mContent.setText(moonlight.getContent());
      if (!editable) {
        mContent.setEnabled(false);
      }
    }
    if (Objects.nonNull(moonlight.getImageUrl())) {
      String url = moonlight.getImageUrl();
      Utils.displayImage(url, mImage);
      mImage.post(() -> CircularRevealUtils.show(mImage));

      mContentTextInputLayout.setPadding(0, 0, 0, mPaddingBottom);
    }
    if (Objects.nonNull(moonlight.getAudioUrl())) {
      showAudio(moonlight.getAudioName());
      mAudioCardView.setVisibility(View.VISIBLE);
      mContentTextInputLayout.setPadding(0, 0, 0, 0);
      if (!editable) {
        mDeleteAudio.setClickable(false);
        mBottomBarLeft.setClickable(false);
        mBottomBarRight.setClickable(false);
      }
    }

    if (moonlight.getColor() != 0) {
      changeUIColor(moonlight.getColor());
    }
  }

  private void changeUIColor(@ColorInt int color) {
    viewParent.setBackgroundColor(color);
    toolbar.setBackgroundColor(color);
    mAudioContainer.setBackgroundColor(moonlight.getColor());
    bottomBarContainer.setBackgroundColor(color);
    changeStatusBarColor(color);
  }

  private void changeStatusBarColor(int color) {
    activity.getWindow().setStatusBarColor(mColorMaps.get(color));
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    toolbar.setBackgroundColor(getResources().getColor(R.color.white, activity.getTheme()));
    bottomBarContainer
        .setBackgroundColor(getResources().getColor(R.color.white, activity.getTheme()));
    toolbar.setTitle(null);
    ((MoonlightActivity) activity).mToolbar.setTitle(null);
    ((MoonlightActivity) activity).hideFAB();
    initView(mEditable);
    setOverflowButtonColor(activity, ColorConstants.GREY_DARK);

    if (!mEditable) {
      // disable softInput keyboard
      activity.getWindow()
          .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
      final Snackbar snackbar =
          SnackBarUtils
              .longSnackBar(view, getString(R.string.trash_restore), SnackBarUtils.TYPE_WARNING)
              .setAction(R.string.trash_restore_action, view -> {

                BusEventUtils.post(Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT, null);
                FDatabaseUtils.restoreToNote(mUserId, moonlight);
                activity.getFragmentManager().popBackStack();
              });

      mFragmentOnTouchListener = ev -> {
        if (!snackbar.isShown() && ev.getAction() == MotionEvent.ACTION_DOWN) {
          snackbar.show();
        }

        return false;
      };
      ((MoonlightActivity) activity).registerFragmentOnTouchListener(mFragmentOnTouchListener);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {
    if (Objects.nonNull(mInputMethodManager)) {
      mInputMethodManager
          .hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);

    }

    revertUI();

    mAudioPlayer.releasePlayer();
    // remove FragmentOnTouchListener
    if (Objects.nonNull(mFragmentOnTouchListener)) {
      ((MoonlightActivity) activity).unregisterFragmentOnTouchListener(mFragmentOnTouchListener);
    }

    RefWatcher refWatcher = MoonlightApplication.getRefWatcher(activity);
    refWatcher.watch(this);
    release();
    super.onDestroy();
  }

  private void release() {
    toolbar = null;
    viewParent = null;
    bottomBarContainer = null;
    mAudioContainer = null;
    mContentTextInputLayout = null;
    mTitle = null;
    mContent = null;
    mShowDuration = null;
    mBottomBarLeft = null;
    mBottomBarRight = null;
    mDeleteAudio = null;
    mPlayingAudio = null;
    mAudioCardView = null;
    mImage = null;
    mCoordinatorLayout = null;
    moonlight = null;
    mColorMaps.clear();
    mColorMaps = null;
    mCircleProgressDialogFragment = null;
    mFragmentOnTouchListener = null;
  }

  @Override
  public boolean onBackPressed() {
    commitMoonlight();
    android.app.FragmentManager fragmentManager = activity.getFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      fragmentManager.popBackStack();
    }
    return false;
  }

  private void commitMoonlight() {
    //当moonlight图片，标题，内容不为空空时，添加moonlight到服务器
    if (mCreateFlag && mEditable) {
      if (isEmpty(moonlight)) {
        FDatabaseUtils.addMoonlight(mUserId, moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
      }
    }
    //当editFlag为true且moonlight不为空时更新moonlight信息到服务器
    if (mEditable && mEditFlag && Objects.nonNull(moonlight) && !moonlight.isTrash()) {
      FDatabaseUtils.updateMoonlight(mUserId, mKeyId, moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
    }
  }

  private void revertUI() {

    toolbar.setVisibility(View.GONE);
    toolbar.setBackgroundColor(getResources().getColor(R.color.light_green, null));
    AppBarLayout.LayoutParams params =
        (AppBarLayout.LayoutParams) ((MoonlightActivity) activity).mToolbar.getLayoutParams();
    params.setScrollFlags(
        AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
            | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
    ((MoonlightActivity) activity).mToolbar.setLayoutParams(params);
    ((MoonlightActivity) activity).mToolbar.setVisibility(View.VISIBLE);
    ((MoonlightActivity) activity).mToolbar.setTitle(getString(R.string.app_name));

    ((DrawerLocker) activity).setDrawerEnabled(true);

    if (mEditable) {
      activity.getWindow().setStatusBarColor(ColorConstants.CYAN_DARK);
      activity.getWindow().setStatusBarColor(Color.TRANSPARENT);

      if (((MoonlightActivity) activity) Objects.nonNull(.mFAB)){
        ((MoonlightActivity) activity).mFAB.show();
      }

    }

  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    //如mEditFlag为true，加载edit_moonlight_menu，反之则加载create_moonlight_menu
    if (mCreateFlag || mEditFlag) {
      inflater.inflate(R.menu.menu_moonlight_detail, menu);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_color_picker:
        if (mEditable) {
          ColorPickerDialogFragment dialog = new ColorPickerDialogFragment();
          dialog.setColorListener((v, color) -> {
            moonlight.setColor(color);
            changeUIColor(color);
            mEditable = true;
          });
          //customize the dialog however you want

          dialog.show(activity.getFragmentManager(), "color picker");
        }
        break;
      case R.id.action_remove_image:
        if (BuildConfig.DEBUG) {
          showShortToast("delete image");
        }
        StorageUtils.removePhoto(view, mUserId, moonlight.getImageName());
        CircularRevealUtils.hide(mImage);
        moonlight.setImageName(null);
        moonlight.setImageUrl(null);
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private boolean isEmpty(Moonlight moonlight) {
    return (Objects.isNull(moonlight.getImageUrl()) && Objects.isNull(moonlight.getAudioUrl())
        && Objects.isNull(moonlight.getContent()) &&
        Objects.isNull(moonlight.getTitle()));
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void handleMessage(BusEvent busEvent) {
    if (Objects.nonNull(busEvent)) {
      switch (busEvent.getFlag()) {
        case Constants.BUS_FLAG_AUDIO_URL:
          if (Objects.nonNull(busEvent.getMessage())) {
            Log.d(TAG, "handleMessage: " + busEvent.getMessage());
            File file =
                new File(
                    new File(Environment.getExternalStorageDirectory() + "/MoonlightNote/.audio"),
                    busEvent.getMessage());
            Uri mAudioUri = FileProvider
                .getUriForFile(MoonlightApplication.getContext(), Constants.FILE_PROVIDER, file);
            uploadFromUri(mAudioUri, mUserId, 3);
          }
          break;
      }
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bottom_bar_left:
        isLeftOrRight = true;
        hideSoftKeyboard();
        break;
      case R.id.bottom_bar_right:
        isLeftOrRight = false;
        hideSoftKeyboard();
        break;
      case R.id.playing_audio_button:
        if (Objects.nonNull(moonlight.getAudioName()) && mStartPlaying) {
          mStartPlaying = false;
          if (!mAudioPlayer.isPrepared) {
            mAudioPlayer.prepare(moonlight.getAudioName());
          }
          mAudioPlayer.startPlaying();
          mPlayingAudio.setBackgroundResource(R.drawable.ic_pause_circle_outline_lime_a700_24dp);
          mAudioPlayer.mPlayer.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.reset();
            mAudioPlayer.mProgressBar.setProgress(0);
            mPlayingAudio.setBackgroundResource(R.drawable.ic_play_circle_outline_cyan_400_48dp);
            mAudioPlayer.isPrepared = false;
            mStartPlaying = true;
          });
        } else {
          mAudioPlayer.stopPlaying();
          mPlayingAudio.setBackgroundResource(R.drawable.ic_play_circle_outline_cyan_400_48dp);
          mStartPlaying = true;
          mAudioPlayer.mPlayer.reset();
          mAudioPlayer.isPrepared = false;
        }
        break;
      case R.id.delete_audio:
        StorageUtils.removeAudio(view, mUserId, moonlight.getAudioName());
        mAudioCardView.setVisibility(View.GONE);
        moonlight.setAudioName(null);
        moonlight.setAudioUrl(null);
        break;
      case R.id.bottom_sheet_item_take_photo:
        mEditable = false;
        onCameraClick(view);
        break;
      case R.id.bottom_sheet_item_choose_image:
        mEditable = onAlbumClick(view);
        break;
      case R.id.bottom_sheet_item_recording:
        onAudioClick();
        break;
      case R.id.bottom_sheet_item_move_to_trash:
        if (isEmpty(moonlight)) {
          FDatabaseUtils.moveToTrash(mUserId, moonlight);
        } else {
          BusEventUtils.post(Constants.BUS_FLAG_NULL, null);
        }
        activity.onBackPressed();
        mEditable = false;
        break;
      case R.id.bottom_sheet_item_permanent_delete:
        if (isEmpty(moonlight)) {
          StorageUtils.removePhoto(view, mUserId, moonlight.getImageName());
          StorageUtils.removeAudio(view, mUserId, moonlight.getAudioName());
          if (Objects.nonNull(mKeyId)) {
            FDatabaseUtils.removeMoonlight(mUserId, mKeyId, Constants.EXTRA_TYPE_MOONLIGHT);
          }
          moonlight = null;
        } else {
          BusEventUtils.post(Constants.BUS_FLAG_NULL, null);
        }
        activity.onBackPressed();
        break;
      case R.id.bottom_sheet_item_make_a_copy:
        if (isEmpty(moonlight)) {
          FDatabaseUtils.addMoonlight(mUserId, moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
          showShortSnackBar(viewParent, "Note Copy complete.", SnackBarUtils.TYPE_INFO);
          changeBottomSheetState();
        } else {
          showShortSnackBar(viewParent, getString(R.string.note_binned), SnackBarUtils.TYPE_INFO);
          changeBottomSheetState();
        }
        break;
      case R.id.bottom_sheet_item_send:
        Intent in = new Intent(Intent.ACTION_SEND);
        in.setType("text/plain");
        if (Objects.nonNull(moonlight.getTitle())) {
          in.putExtra(Intent.EXTRA_TITLE, moonlight.getTitle());
        }

        if (Objects.nonNull(moonlight.getContent())) {
          in.putExtra(Intent.EXTRA_TEXT, moonlight.getContent());
        }

        if (Objects.nonNull(moonlight.getImageUrl())) {
          in.putExtra(Intent.EXTRA_TEXT, moonlight.getImageUrl());
        }
        //设置分享选择器
        in = Intent.createChooser(in, "Send to");
        startActivity(in);
        break;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case TAKE_PICTURE:
        if (resultCode == RESULT_OK && Objects.nonNull(fileUri)) {
          Log.d(TAG, "take Picture" + fileUri.toString());
          uploadFromUri(fileUri, mUserId, 0);
        }
        mEditable = true;
        break;
      case ALBUM_CHOOSE:
        Log.d(TAG, "album choose");
        if (resultCode == RESULT_OK && Objects.nonNull(data.getData())) {
          Uri fileUri = data.getData();
          uploadFromUri(fileUri, mUserId, 0);
        }
        mEditable = true;
        break;
      case RECORD_AUDIO:
        if (resultCode == RESULT_OK) {
          List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          String spokenText = results.get(0);
          // Do something with spokenText
          mContent.setText(spokenText);
          Log.d(TAG, "onActivityResult: " + spokenText);
          // the recording url is in getData:
          if (Objects.nonNull(data.getData())) {
            Uri audioUri = data.getData();
            Log.d(TAG, "onActivityResult: " + audioUri.toString());
            if (copyAudioFile(audioUri) != null) {
              uploadFromUri(copyAudioFile(audioUri), mUserId, 3);
            }
          }
          mEditable = true;
        }
      default:
        break;
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  private Uri copyAudioFile(Uri uri) {
    ContentResolver contentResolver = activity.getContentResolver();
    File dir = new File(
        Environment.getExternalStorageDirectory().getAbsolutePath() + "/MoonlightNote/.audio/");
    if (!dir.exists()) {
      boolean isDirCreate = dir.mkdirs();
      Log.d(TAG, "dir.mkdirs():" + isDirCreate);
    }
    File file = new File(dir, UUID.randomUUID().toString() + ".amr");

    try (FileOutputStream fos = new FileOutputStream(file)) {
      try (InputStream inputStream = contentResolver.openInputStream(uri)) {

        byte[] buffer = new byte[4 * 1024];
        int length;
        while ((length = Objects.nonNull(inputStream) ? inputStream.read(buffer) : 0) != -1) {
          fos.write(buffer, 0, length);
        }
        fos.flush();
      } catch (IOException e) {
        Log.e(TAG, "copyAudioFile: ", e);
      }
      return Uri.fromFile(file);
    } catch (IOException e) {
      Log.e(TAG, "copyAudioFile: ", e);
    }
    return null;
  }

  @AfterPermissionGranted(RECORD_AUDIO)
  private void onAudioClick() {
    // Check that we have permission to read images from external storage.
    grantStoragePermission();

    if (!EasyPermissions.hasPermissions(activity, Manifest.permission.RECORD_AUDIO)) {
      EasyPermissions.requestPermissions(activity, "If you want to do this continue, " +
              "you should give App record audio permission ", RECORD_AUDIO,
          Manifest.permission.RECORD_AUDIO);
    }

    // Choose file storage location, must be listed in res/xml/file_paths.xml
    File dir = new File(Environment.getExternalStorageDirectory() + "/MoonlightNote/.audio");

    if (!dir.exists()) {
      boolean isDirCreate = dir.mkdirs();
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "onAudioClick: " + isDirCreate);
      }
    }
    displaySpeechRecognizer();
  }

  private void uploadFromUri(Uri fileUri, String userId, int type) {
    if (Objects.nonNull(mCircleProgressDialogFragment)) {
      mCircleProgressDialogFragment.show(activity.getFragmentManager(), "progress");
    } else {
      mCircleProgressDialogFragment = CircleProgressDialogFragment.newInstance();
      mCircleProgressDialogFragment.show(activity.getFragmentManager(), "progress");
    }

    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    if (type == 0) {

      // Get a reference to store file at photos/<FILENAME>.jpg
      StorageReference photoRef =
          mStorageReference.child(userId).child("photos")
              .child(Objects.requireNonNull(fileUri.getLastPathSegment()));
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "uploadFromUri: " + fileUri.getLastPathSegment());
      }
      // Upload file to Firebase Storage
      uploadTask = photoRef.putFile(fileUri);
      uploadTask.addOnSuccessListener(taskSnapshot -> {

        mDownloadIUrl = Uri.parse(Objects.requireNonNull(taskSnapshot.getMetadata()).getPath());
        mImageFileName = taskSnapshot.getMetadata().getName();
        if (BuildConfig.DEBUG) {
          Log.d(TAG, "onSuccess: downloadUrl:  " + mDownloadIUrl.toString());
        }
        moonlight.setImageName(mImageFileName);
        moonlight.setImageUrl(mDownloadIUrl.toString());
        mCircleProgressDialogFragment.dismiss();
        showImage(mDownloadIUrl);
      }).addOnFailureListener(e -> {
        Log.e(TAG, "onFailure: " + e.toString());
        mImageFileName = null;
        mDownloadIUrl = null;
        mCircleProgressDialogFragment.dismiss();
        showImage(fileUri);
      }).addOnPausedListener(taskSnapshot -> {
        if (BuildConfig.DEBUG) {
          Log.d(TAG, "onPaused: ");
        }
        mCircleProgressDialogFragment.dismiss();
        showShortSnackBar(view, "upload paused", SnackBarUtils.TYPE_INFO);
      });
    } else if (type == 3) {
      StorageReference storageReference =
          mStorageReference.child(userId).child("audios")
              .child(Objects.requireNonNull(fileUri.getLastPathSegment()));
      uploadTask = storageReference.putFile(fileUri);
      uploadTask.addOnSuccessListener(taskSnapshot -> {
        mDownloadAUrl = Uri.parse(Objects.requireNonNull(taskSnapshot.getMetadata()).getPath());
        mAudioFileName = taskSnapshot.getMetadata().getName();
        if (BuildConfig.DEBUG) {
          Log.d(TAG, "onSuccess: downloadUrl:  " + mAudioFileName);
        }
        moonlight.setAudioName(mAudioFileName);
        moonlight.setAudioUrl(mDownloadAUrl.toString());
        showAudio(mAudioFileName);
        mCircleProgressDialogFragment.dismiss();
      }).addOnFailureListener(e -> {
        Log.e(TAG, "onFailure: " + e.toString());
        mAudioFileName = null;
        mDownloadAUrl = null;
        mCircleProgressDialogFragment.dismiss();
        showAudio(fileUri.toString());
      }).addOnPausedListener(taskSnapshot -> {
        if (BuildConfig.DEBUG) {
          Log.d(TAG, "onPaused: ");
        }
        mCircleProgressDialogFragment.dismiss();
        showShortSnackBar(view, "upload paused", SnackBarUtils.TYPE_INFO);
      });
    }
  }

  // Create an intent that can start the Speech Recognizer activity
  private void displaySpeechRecognizer() {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "displaySpeechRecognizer: ");
    }
    loseFocus();
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    // secret parameters that when added provide audio url in the result
    intent.putExtra(Constants.GET_AUDIO_FORMAT, "audio/AMR");
    intent.putExtra(Constants.GET_AUDIO, true);
    // Start the activity, the intent will be populated with the speech text
    startActivityForResult(intent, RECORD_AUDIO);
    mEditable = false;
  }

  public void showBottomSheet() {
    initBottomSheetItem();
    // The View with the BottomSheetBehavior

    final View bottomSheetLeft = mCoordinatorLayout.findViewById(R.id.bottom_sheet_left);
    final View bottomSheetRight = mCoordinatorLayout.findViewById(R.id.bottom_sheet_right);
    mLeftBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLeft);
    mRightBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetRight);
    mLeftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    mRightBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    BottomSheetCallback bottomSheetCallback = new BottomSheetCallback() {
      @Override
      public void onStateChanged(@NonNull View bottomSheet, int newState) {
        //这里是bottomSheet 状态的改变，根据slideOffset可以做一些动画
        if (bottomSheet == bottomSheetLeft) {
          if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            mLeftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
          }
        }
        if (bottomSheet == bottomSheetRight) {
          if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            mRightBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
          }
        }

        //                ViewCompat.setScaleX(bottomSheet,1);
        //                ViewCompat.setScaleY(bottomSheet,1);
      }

      @Override
      public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        //这里是拖拽中的回调，根据slideOffset可以做一些动画
        //                ViewCompat.setScaleX(bottomSheet,slideOffset);
        //                ViewCompat.setScaleY(bottomSheet,slideOffset);
      }
    };

    mLeftBottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
    mRightBottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
  }

  private void initBottomSheetItem() {
    LinearLayoutCompat takePhoto = view.findViewById(R.id.bottom_sheet_item_take_photo);
    LinearLayoutCompat chooseImage = view.findViewById(R.id.bottom_sheet_item_choose_image);
    LinearLayoutCompat recording = view.findViewById(R.id.bottom_sheet_item_recording);
    LinearLayoutCompat moveToTrash = view.findViewById(R.id.bottom_sheet_item_move_to_trash);
    LinearLayoutCompat permanentDelete = view.findViewById(R.id.bottom_sheet_item_permanent_delete);
    LinearLayoutCompat makeACopy = view.findViewById(R.id.bottom_sheet_item_make_a_copy);
    LinearLayoutCompat send = view.findViewById(R.id.bottom_sheet_item_send);
    takePhoto.setOnClickListener(this);
    chooseImage.setOnClickListener(this);
    recording.setOnClickListener(this);
    moveToTrash.setOnClickListener(this);
    permanentDelete.setOnClickListener(this);
    makeACopy.setOnClickListener(this);
    send.setOnClickListener(this);
  }

  private void hideSoftKeyboard() {
    if (Objects.nonNull(mInputMethodManager)) {
      if (mEditable) {
        mInputMethodManager
            .hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        handler.postDelayed(this::changeBottomSheetState, 100);
      }
    }
  }

  private void showSoftKeyboard() {
    if (Objects.nonNull(mInputMethodManager)) {
      mInputMethodManager.showSoftInput(activity.getWindow().getDecorView(), 0);
    }
  }

  private void changeBottomSheetState() {
    // isLeftOrRight值为真是左，假则是右
    if (isLeftOrRight) {
      // 首先检查RightBottomSheet是否启用，如果是则隐藏
      if (mRightBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
          mRightBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
        mRightBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      }
      //检查LeftBottomSheet是否为隐藏，如果是则直接展开，否则进入下一步判断
      if (mLeftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
        mLeftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      } else {
        //检查LeftBottomSheet是否展开或者收缩，进行相应操作
        if (mLeftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
          mLeftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
          loseFocus();
        } else {
          mLeftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
      }
    } else {
      // 首先检查LeftBottomSheet是否启用，如果是则隐藏
      if (mLeftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
          mLeftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
        mLeftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      }
      //检查RightBottomSheet是否为隐藏，如果是则直接展开，否则进入下一步判断
      if (mRightBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
        mRightBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      } else {
        //检查RightBottomSheet是否展开或者收缩，进行相应操作
        if (mRightBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
          mRightBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
          loseFocus();
        } else {
          mRightBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
      }
    }
  }

  private void loseFocus() {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "loseFocus: ");
    }
    mTitle.clearFocus();
    mContent.clearFocus();
  }

  /**
   * 用监听软键盘是否弹出
   *
   * @param view 主视图布局
   */
  private void onCheckSoftKeyboardState(final View view) {
    //先主视图布局设置监听，监听其布局发生变化事件
    view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
      //比较主视图布局与当前布局的大小
      int heightDiff = this.view.getRootView().getHeight() - view.getHeight();
      if (heightDiff > 100) {
        //大小超过100时，一般为显示虚拟键盘事件
        if (mEditable && !Utils.isXLargeTablet(activity)) {
          if (mLeftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mLeftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
          }
          if (mRightBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mRightBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
          }
        } else {
          //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
          Log.d(TAG, "onGlobalLayout: ");
        }
      }
    });
  }

  /**
   * 更新显示图片信息
   *
   * @param mFileUri 图片地址
   */
  private void showImage(@NonNull Uri mFileUri) {
    Utils.displayImage(mFileUri.toString(), mImage);
    mImage.post(() -> CircularRevealUtils.show(mImage));

    mContentTextInputLayout.setPadding(0, 0, 0, mPaddingBottom);
  }

  private void showAudio(String audioFileName) {
    if (!moonlight.getAudioName().isEmpty()) {
      mAudioPlayer.prepare(audioFileName);
      if (mAudioPlayer.isPrepared) {
        moonlight.setAudioDuration((long) mAudioPlayer.mDuration);
        mAudioCardView.setVisibility(View.VISIBLE);
        mAudioContainer.setBackgroundColor(moonlight.getColor());
        mContentTextInputLayout.setPadding(0, 0, 0, 0);
      }
    } else {
      mShowDuration.setText(Utils.convert(moonlight.getAudioDuration()));
    }
  }
}
