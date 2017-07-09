package com.art2cat.dev.moonlightnote.controller.moonlight;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
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
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.art2cat.dev.moonlightnote.controller.BaseFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.CircleProgressDialogFragment;
import com.art2cat.dev.moonlightnote.model.BusEvent;
import com.art2cat.dev.moonlightnote.model.Constants;
import com.art2cat.dev.moonlightnote.model.Moonlight;
import com.art2cat.dev.moonlightnote.utils.AudioPlayer;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.utils.FragmentBackHandler;
import com.art2cat.dev.moonlightnote.utils.LogUtils;
import com.art2cat.dev.moonlightnote.utils.PermissionUtils;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.utils.Utils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.utils.firebase.StorageUtils;
import com.art2cat.dev.moonlightnote.utils.material_animation.CircularRevealUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.art2cat.dev.moonlightnote.model.Constants.ALBUM_CHOOSE;
import static com.art2cat.dev.moonlightnote.model.Constants.BLUE_DARK;
import static com.art2cat.dev.moonlightnote.model.Constants.CAMERA_PERMS;
import static com.art2cat.dev.moonlightnote.model.Constants.CYAN_DARK;
import static com.art2cat.dev.moonlightnote.model.Constants.GREY_DARK;
import static com.art2cat.dev.moonlightnote.model.Constants.RECORD_AUDIO;
import static com.art2cat.dev.moonlightnote.model.Constants.STORAGE_PERMS;
import static com.art2cat.dev.moonlightnote.model.Constants.TAKE_PICTURE;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MoonlightDetailFragment extends BaseFragment implements
        View.OnClickListener, View.OnFocusChangeListener, PopupMenu.OnMenuItemClickListener,
        FragmentBackHandler {
    private static final String TAG = "MoonlightDetailFragment";
    private View mView;
    private Toolbar mToolbar;
    private ContentFrameLayout mViewParent;
    private LinearLayoutCompat mBottomBarContainer;
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
    private AppCompatImageView mImage;
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
    private Uri mFileUri = null;
    private StorageReference mStorageReference;
    private String mImageFileName;
    private String mAudioFileName;
    private Uri mDownloadIUrl;
    private Uri mDownloadAUrl;
    private File mFile;
    private Handler mHandler = new Handler();
    private AudioPlayer mAudioPlayer;
    private Map<Integer, Integer> mColorMaps;

    public MoonlightDetailFragment() {
        // Required empty public constructor
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        LogUtils.getInstance(TAG).setMessage("onCreate").debug();
        //设置显示OptionsMenu
        setHasOptionsMenu(true);
        //获取Bus单例，并注册
        //BusProvider.getInstance().register(this);
        EventBus.getDefault().register(this);
        //获取用户id
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //获取firebaseStorage实例
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = firebaseStorage.getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE);

        mInputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        mPaddingBottom = getResources().getDimensionPixelOffset(R.dimen.padding_bottom);

        if (getArguments() != null) {
            moonlight = getArguments().getParcelable("moonlight");
            if (moonlight != null) {
                mKeyId = moonlight.getId();
                if (BuildConfig.DEBUG) Log.d(TAG, "keyId: " + mKeyId);
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

        ((DrawerLocker) mActivity).setDrawerEnabled(false);

        initColor();

    }

    @SuppressLint("UseSparseArrays")
    private void initColor() {
        mColorMaps = new HashMap<>();
        mColorMaps.put(Constants.AMBER, Constants.AMBER_DARK);
        mColorMaps.put(Constants.BLUE, BLUE_DARK);
        mColorMaps.put(Constants.BLUE_GRAY, Constants.BLUE_GRAY_DARK);
        mColorMaps.put(Constants.BROWN, Constants.BROWN_DARK);
        mColorMaps.put(Constants.CYAN, Constants.CYAN_DARK);
        mColorMaps.put(Constants.DEEP_ORANGE, Constants.DEEP_ORANGE_DARK);
        mColorMaps.put(Constants.DEEP_PURPLE, Constants.DEEP_PURPLE_DARK);
        mColorMaps.put(Constants.GREEN, Constants.GREEN_DARK);
        mColorMaps.put(Constants.GREY, GREY_DARK);
        mColorMaps.put(Constants.INDIGO, Constants.INDIGO_DARK);
        mColorMaps.put(Constants.LIGHT_BLUE, Constants.LIGHT_BLUE_DARK);
        mColorMaps.put(Constants.LIGHT_GREEN, Constants.LIGHT_GREEN_DARK);
        mColorMaps.put(Constants.LIME, Constants.LIME_DARK);
        mColorMaps.put(Constants.ORANGE, Constants.ORANGE_DARK);
        mColorMaps.put(Constants.PINK, Constants.PINK_DARK);
        mColorMaps.put(Constants.PURPLE, Constants.PURPLE_DARK);
        mColorMaps.put(Constants.RED, Constants.RED_DARK);
        mColorMaps.put(Constants.TEAL, Constants.TEAL_DARK);
        mColorMaps.put(Constants.YELLOW, Constants.YELLOW_DARK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.getInstance(TAG).setMessage("onCreate").debug();
        //视图初始化
        mView = inflater.inflate(R.layout.fragment_moonlight_detail, container, false);

        mToolbar = ((MoonlightActivity) mActivity).mToolbar2;
        mToolbar.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.white, null));
        } else {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        }
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) ((MoonlightActivity) mActivity).mToolbar.getLayoutParams();
        params.setScrollFlags(0);
        ((MoonlightActivity) mActivity).mToolbar.setLayoutParams(params);
        mToolbar.setLayoutParams(params);
        ((MoonlightActivity) mActivity).setSupportActionBar(mToolbar);
        ((MoonlightActivity) mActivity).mToolbar.setVisibility(View.GONE);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey_700_24dp);
        mToolbar.setNavigationOnClickListener(view -> mActivity.onBackPressed());

        mViewParent = mView.findViewById(R.id.view_parent);
        mTitle = mView.findViewById(R.id.title_TIET);
        mContent = mView.findViewById(R.id.content_TIET);
        mContentTextInputLayout = mView.findViewById(R.id.content_TIL);
        mImage = mView.findViewById(R.id.moonlight_image);
        mAudioCardView = mView.findViewById(R.id.audio_container);
        mAudioContainer = mView.findViewById(R.id.audio_container_inner);

        mDeleteAudio = mView.findViewById(R.id.delete_audio);
        mPlayingAudio = mView.findViewById(R.id.playing_audio_button);
        mShowDuration = mView.findViewById(R.id.moonlight_audio_duration);
        ProgressBar audioPlayerPB = mView.findViewById(R.id.moonlight_audio_progressBar);
        AppCompatTextView displayTime = mView.findViewById(R.id.bottom_bar_display_time);
        mCoordinatorLayout = mView.findViewById(R.id.bottom_sheet_container);
        mBottomBarContainer = mView.findViewById(R.id.bottom_bar_container);
        mBottomBarLeft = mView.findViewById(R.id.bottom_bar_left);
        mBottomBarRight = mView.findViewById(R.id.bottom_bar_right);
        mTitle.setOnFocusChangeListener(this);
        mContent.setOnFocusChangeListener(this);
        mAudioPlayer = AudioPlayer.getInstance(audioPlayerPB, mShowDuration);

        mCircleProgressDialogFragment = CircleProgressDialogFragment.newInstance(getString(R.string.prograssBar_uploading));

        if (mEditable) {
            //获取系统当前时间
            long date = System.currentTimeMillis();
            moonlight.setDate(date);
            String time = Utils.timeFormat(mActivity.getApplicationContext(), new Date(date));
            if (time != null) {
                String timeFormat = "Edited: " + time;
                displayTime.setText(timeFormat);
            }
            onCheckSoftKeyboardState(mView);
            mImage.setOnClickListener(this);
            mDeleteAudio.setOnClickListener(this);
            mPlayingAudio.setOnClickListener(this);
//            if (!Utils.isXLargeTablet(mActivity)) {
            showBottomSheet();
//            }

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
        return mView;
    }

    private void initView(boolean editable) {
        if (editable) {
            if (moonlight.getTitle() != null) {
                mTitle.setText(moonlight.getTitle());
            }
        } else {
            mTitle.setEnabled(false);
            if (moonlight.getTitle() != null) {
                mTitle.setText(moonlight.getTitle());
            }
        }
        if (moonlight.getContent() != null) {
            mContent.setText(moonlight.getContent());
            if (!editable) {
                mContent.setEnabled(false);
            }
        }
        if (moonlight.getImageUrl() != null) {
            String url = moonlight.getImageUrl();
            Picasso.with(mActivity)
                    .load(Uri.parse(url))
                    .memoryPolicy(NO_CACHE, NO_STORE)
                    .placeholder(R.drawable.ic_cloud_download_black_24dp)
                    .into(mImage);
            mImage.post(new Runnable() {
                @Override
                public void run() {
                    CircularRevealUtils.show(mImage);
                }
            });

            mContentTextInputLayout.setPadding(0, 0, 0, mPaddingBottom);
        }
        if (moonlight.getAudioUrl() != null) {
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

    private void changeUIColor(@ColorRes int color, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mToolbar.setBackgroundColor(getResources().getColor(color, theme));
            mBottomBarContainer.setBackgroundColor(getResources().getColor(color, theme));
        } else {
            mToolbar.setBackgroundColor(getResources().getColor(color));
            mBottomBarContainer.setBackgroundColor(getResources().getColor(color));
        }
    }

    private void changeUIColor(@ColorInt int color) {
        mViewParent.setBackgroundColor(color);
        mToolbar.setBackgroundColor(color);
        mAudioContainer.setBackgroundColor(moonlight.getColor());
        mBottomBarContainer.setBackgroundColor(color);
        changeStatusBarColor(color);
    }

    private void changeStatusBarColor(int color) {
        for (Integer integer : mColorMaps.keySet()) {
            if (integer == color) {
                mActivity.getWindow().setStatusBarColor(mColorMaps.get(color));
                break;
            }
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changeUIColor(R.color.white, mActivity.getTheme());
        mToolbar.setTitle(null);
        ((MoonlightActivity) mActivity).mToolbar.setTitle(null);
        ((MoonlightActivity) mActivity).hideFAB();
        initView(mEditable);
        setOverflowButtonColor(mActivity, GREY_DARK);

//        if (Utils.isXLargeTablet(mActivity)) {
//            LinearLayout.LayoutParams lp =
//                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                            mToolbar.getHeight());
//            mBottomBarContainer.setLayoutParams(lp);
//        }

        if (!mEditable) {
            //禁用软键盘
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            final Snackbar snackbar = SnackBarUtils.longSnackBar(mView, getString(R.string.trash_restore),
                    SnackBarUtils.TYPE_WARNING).setAction(R.string.trash_restore_action,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            BusEventUtils.post(Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT, null);
                            FDatabaseUtils.restoreToNote(mUserId, moonlight);
                            getFragmentManager().popBackStack();
                        }
                    });

            mFragmentOnTouchListener = ev -> {
                if (!snackbar.isShown() && ev.getAction() == MotionEvent.ACTION_DOWN) {
                    snackbar.show();
                }

                return false;
            };
            ((MoonlightActivity) mActivity).registerFragmentOnTouchListener(mFragmentOnTouchListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mInputMethodManager != null) {
            mInputMethodManager.hideSoftInputFromWindow(
                    mActivity.getWindow().getDecorView().getWindowToken(), 0);

        }

        revertUI();

        mAudioPlayer.releasePlayer();
        //移除FragmentOnTouchListener
        if (mFragmentOnTouchListener != null) {
            ((MoonlightActivity) mActivity).unregisterFragmentOnTouchListener(mFragmentOnTouchListener);
        }

        RefWatcher refWatcher = MoonlightApplication.getRefWatcher(mActivity);
        refWatcher.watch(this);
        release();
        super.onDestroy();
    }

    private void release() {
//        mView = null;
        mToolbar = null;
        mViewParent = null;
        mBottomBarContainer = null;
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
    }

    @Override
    public boolean onBackPressed() {
        commitMoonlight();
        android.app.FragmentManager fragmentManager = mActivity.getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
        return false;
    }

    private void commitMoonlight() {
        //当moonlight图片，标题，内容不为空空时，添加moonlight到服务器
        if (mCreateFlag && mEditable) {
            if (!isEmpty(moonlight)) {
                FDatabaseUtils.addMoonlight(mUserId, moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
            }
        }
        //当editFlag为true且moonlight不为空时更新moonlight信息到服务器
        if (mEditable && mEditFlag && moonlight != null && !moonlight.isTrash()) {
            FDatabaseUtils.updateMoonlight(mUserId, mKeyId, moonlight,
                    Constants.EXTRA_TYPE_MOONLIGHT);
        }
    }

    /**
     * 恢复原来的UI界面
     */
    private void revertUI() {

        mToolbar.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.light_green, null));
        } else {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.light_green));
        }
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams)
                ((MoonlightActivity) mActivity).mToolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        ((MoonlightActivity) mActivity).mToolbar.setLayoutParams(params);
        ((MoonlightActivity) mActivity).mToolbar.setVisibility(View.VISIBLE);
        ((MoonlightActivity) mActivity).mToolbar.setTitle(getString(R.string.app_name));

        ((DrawerLocker) mActivity).setDrawerEnabled(true);

        if (mEditable) {
            mActivity.getWindow().setStatusBarColor(CYAN_DARK);
            mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);

            if (((MoonlightActivity) mActivity).mFAB != null) {
                ((MoonlightActivity) mActivity).mFAB.show();
            }

        }

    }

    @SuppressLint("LogConditional")
    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.title_TIET:
                Log.d(TAG, "onFocusChange: title " + b);
                break;
            case R.id.content_TIET:
                Log.d(TAG, "onFocusChange: content " + b);
                break;
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
                    MyColorPickerDialog dialog = new MyColorPickerDialog(mActivity);
                    dialog.setTitle("Color Picker");
                    dialog.setColorListener((v, color) -> {
                        moonlight.setColor(color);
                        changeUIColor(color);
                        mEditable = true;
                        mEditable = true;
                    });
                    //customize the dialog however you want
                    dialog.show();
                }
                break;
            case R.id.action_remove_image:
                if (BuildConfig.DEBUG) showShortToast("delete image");
                StorageUtils.removePhoto(mView, mUserId, moonlight.getImageName());
                CircularRevealUtils.hide(mImage);
                moonlight.setImageName(null);
                moonlight.setImageUrl(null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEmpty(Moonlight moonlight) {
        return !(moonlight.getImageUrl() != null || moonlight.getAudioUrl() != null || moonlight.getContent() != null
                || moonlight.getTitle() != null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMessage(BusEvent busEvent) {
        if (busEvent != null) {
            switch (busEvent.getFlag()) {
                case Constants.BUS_FLAG_AUDIO_URL:
                    if (busEvent.getMessage() != null) {
                        Log.d(TAG, "handleMessage: " + busEvent.getMessage());
                        File file = new File(new File(Environment.getExternalStorageDirectory()
                                + "/MoonlightNote/.audio"), busEvent.getMessage());
                        Uri mAudioUri = FileProvider.getUriForFile(MoonlightApplication.getContext(), Constants.FILE_PROVIDER, file);
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
//                if (Utils.isXLargeTablet(mActivity)) {
//                    MenuUtils.showPopupMenu(mActivity, mView, R.menu.menu_detail_left, this);
//                } else {
                isLeftOrRight = true;
                hideSoftKeyboard();
//                }
                break;
            case R.id.bottom_bar_right:
//                if (Utils.isXLargeTablet(mActivity)) {
//                    MenuUtils.showPopupMenu(mActivity, mView, R.menu.menu_detail_right, this);
//                } else {
                isLeftOrRight = false;
                hideSoftKeyboard();
//                }
                break;
            case R.id.moonlight_image:
                //网页浏览图片。。。
                ScaleFragment scaleFragment = ScaleFragment.newInstance(moonlight.getImageUrl());
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, scaleFragment)
                        .addSharedElement(mImage, mImage.getTransitionName())
                        .addToBackStack("scale")
                        .commit();
                break;
            case R.id.playing_audio_button:
                if (moonlight.getAudioName() != null && mStartPlaying) {
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
                //删除录音
                StorageUtils.removeAudio(mView, mUserId, moonlight.getAudioName());
                mAudioCardView.setVisibility(View.GONE);
                moonlight.setAudioName(null);
                moonlight.setAudioUrl(null);
                break;
            case R.id.bottom_sheet_item_take_photo:
                onCameraClick();
                break;
            case R.id.bottom_sheet_item_choose_image:
                onAlbumClick();
                break;
            case R.id.bottom_sheet_item_recording:
                onAudioClick();
                break;
            case R.id.bottom_sheet_item_move_to_trash:
                if (!isEmpty(moonlight)) {
                    FDatabaseUtils.moveToTrash(mUserId, moonlight);
//                    BusEventUtils.post(moonlight, Constants.BUS_FLAG_DELETE);
                } else {
                    BusEventUtils.post(Constants.BUS_FLAG_NULL, null);
                }
                mActivity.onBackPressed();
                mEditable = false;
                break;
            case R.id.bottom_sheet_item_permanent_delete:
                if (!isEmpty(moonlight)) {
                    StorageUtils.removePhoto(mView, mUserId, moonlight.getImageName());
                    StorageUtils.removeAudio(mView, mUserId, moonlight.getAudioName());
                    if (mKeyId != null) {
                        FDatabaseUtils.removeMoonlight(mUserId, mKeyId, Constants.EXTRA_TYPE_MOONLIGHT);
                    }
//                    BusEventUtils.post(moonlight, Constants.BUS_FLAG_PERMENAT_DELETE);
                    moonlight = null;
                } else {
                    BusEventUtils.post(Constants.BUS_FLAG_NULL, null);
                }
                mActivity.onBackPressed();
                break;
            case R.id.bottom_sheet_item_make_a_copy:
                if (!isEmpty(moonlight)) {
                    FDatabaseUtils.addMoonlight(mUserId, moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
//                    BusEventUtils.post(moonlight, Constants.BUS_FLAG_MAKE_A_COPY);
                    showShortSnackBar(mViewParent,
                            "Note Copy complete.", SnackBarUtils.TYPE_INFO);
                    changeBottomSheetState();
                } else {
                    showShortSnackBar(mViewParent,
                            getString(R.string.note_binned), SnackBarUtils.TYPE_INFO);
                    changeBottomSheetState();
                }
                break;
            case R.id.bottom_sheet_item_send:
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
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "take Picture" + mFileUri.toString());
                    uploadFromUri(mFileUri, mUserId, 0);
                }
                mEditable = true;
                break;
            case ALBUM_CHOOSE:
                Log.d(TAG, "album choose");
                if (resultCode == RESULT_OK && data.getData() != null) {
                    Uri fileUri = data.getData();
                    uploadFromUri(fileUri, mUserId, 0);
                }
                mEditable = true;
                break;
            case RECORD_AUDIO:
                if (resultCode == RESULT_OK) {
                    List<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    String spokenText = results.get(0);
                    // Do something with spokenText
                    mContent.setText(spokenText);
                    Log.d(TAG, "onActivityResult: " + spokenText);
                    // the recording url is in getData:
                    if (data.getData() != null) {
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
        ContentResolver contentResolver = mActivity.getContentResolver();
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/MoonlightNote/.audio/");
        if (!dir.exists()) {
            boolean isDirCreate = dir.mkdirs();
            Log.d(TAG, "dir.mkdirs():" + isDirCreate);
        }
        File file = new File(dir, UUID.randomUUID().toString() + ".amr");
        FileOutputStream fos;
        InputStream inputStream;
        try {
            inputStream = contentResolver.openInputStream(uri);
            fos = new FileOutputStream(file);
            try {

                byte[] buffer = new byte[4 * 1024];
                int length;
                while ((length = inputStream != null ? inputStream.read(buffer) : 0) != -1) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //noinspection ThrowFromFinallyBlock
                fos.close();
                assert inputStream != null;
                //noinspection ThrowFromFinallyBlock
                inputStream.close();
            }
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LogConditional")
    @AfterPermissionGranted(CAMERA_PERMS)
    private void onCameraClick() {
        // Check that we have permission to read images from external storage.
        String perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String perm1 = Manifest.permission.CAMERA;
        if (!EasyPermissions.hasPermissions(mActivity, perm) &&
                !EasyPermissions.hasPermissions(mActivity, perm1)) {
            PermissionUtils.requestStorage(mActivity, perm);
            PermissionUtils.requestCamera(mActivity, perm1);
            return;
        }
        if (!EasyPermissions.hasPermissions(mActivity, perm)) {
            PermissionUtils.requestStorage(mActivity, perm);
            return;
        }
        if (!EasyPermissions.hasPermissions(mActivity, perm1)) {
            PermissionUtils.requestCamera(mActivity, perm1);
            return;
        }
        // Choose file storage location, must be listed in res/xml/file_paths.xml
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/MoonlightNote/.image");
        mFile = new File(dir, UUID.randomUUID().toString() + ".jpg");
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdirs();
            }
            boolean created = mFile.createNewFile();
            Log.d(TAG, "created:" + created);
        } catch (IOException e) {
            Log.e(TAG, "file.createNewFile" + mFile.getAbsolutePath() + ":FAILED", e);
        }

        // Create content:// URI for file, required since Android N
        // See: https://developer.android.com/reference/android/support/v4/content/FileProvider.html

        mFileUri = FileProvider.getUriForFile(mActivity, Constants.FILE_PROVIDER, mFile);
        Log.i(TAG, "file: " + mFileUri);

        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        if (takePicIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            startActivityForResult(takePicIntent, TAKE_PICTURE);
            mEditable = false;
            Log.d(TAG, "onCameraClick: ");
        } else {
            showLongSnackBar(mView, "No Camera!", SnackBarUtils.TYPE_WARNING);
        }
    }

    @SuppressLint("LogConditional")
    @AfterPermissionGranted(STORAGE_PERMS)
    private void onAlbumClick() {
        // Check that we have permission to read images from external storage.
        String perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!EasyPermissions.hasPermissions(mActivity, perm)) {
            PermissionUtils.requestStorage(mActivity, perm);
            return;
        }

        // Choose file storage location, must be listed in res/xml/file_paths.xml
        File dir = new File(Environment.getExternalStorageDirectory() + "/MoonlightNote/.image");
        mFile = new File(dir, UUID.randomUUID().toString() + ".jpg");
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdirs();
            }
            boolean created = mFile.createNewFile();
            Log.d(TAG, "file.createNewFile:" + mFile.getAbsolutePath() + ":" + created);
        } catch (IOException e) {
            Log.e(TAG, "file.createNewFile" + mFile.getAbsolutePath() + ":FAILED", e);
        }

        Uri fileUri = FileProvider.getUriForFile(mActivity, Constants.FILE_PROVIDER, mFile);
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setType("image/*");
        albumIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        if (albumIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            startActivityForResult(albumIntent, ALBUM_CHOOSE);
            mEditable = false;
        } else {
            showLongSnackBar(mView, "No Album!", SnackBarUtils.TYPE_WARNING);
        }
    }

    @AfterPermissionGranted(RECORD_AUDIO)
    private void onAudioClick() {
        // Check that we have permission to read images from external storage.
        String perm = Manifest.permission.RECORD_AUDIO;
        String perm1 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!EasyPermissions.hasPermissions(mActivity, perm) &&
                !EasyPermissions.hasPermissions(mActivity, perm1)) {
            PermissionUtils.requestStorage(mActivity, perm1);
            PermissionUtils.requestRecAudio(mActivity, perm);
            return;
        }
        if (!EasyPermissions.hasPermissions(mActivity, perm1)) {
            PermissionUtils.requestStorage(mActivity, perm1);
            return;
        }

        if (!EasyPermissions.hasPermissions(mActivity, perm)) {
            PermissionUtils.requestRecAudio(mActivity, perm);
            return;
        }
        // Choose file storage location, must be listed in res/xml/file_paths.xml
        File dir = new File(Environment.getExternalStorageDirectory() + "/MoonlightNote/.audio");

        if (!dir.exists()) {
            boolean isDirCreate = dir.mkdirs();
            Log.d(TAG, "onAudioClick: " + isDirCreate);
        }
        displaySpeechRecognizer();
    }

    public void uploadFromUri(final Uri fileUri, String userId, int type) {
        if (mCircleProgressDialogFragment != null) {
            mCircleProgressDialogFragment.show(mActivity.getFragmentManager(), "progress");
        } else {
            mCircleProgressDialogFragment = CircleProgressDialogFragment.newInstance();
            mCircleProgressDialogFragment.show(mActivity.getFragmentManager(), "progress");
        }

        StorageTask<UploadTask.TaskSnapshot> uploadTask;
        if (type == 0) {

            // Get a reference to store file at photos/<FILENAME>.jpg
            StorageReference photoRef = mStorageReference.child(userId).child("photos")
                    .child(fileUri.getLastPathSegment());
            Log.d(TAG, "uploadFromUri: " + fileUri.getLastPathSegment());
            // Upload file to Firebase Storage
            uploadTask = photoRef.putFile(fileUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                mDownloadIUrl = taskSnapshot.getDownloadUrl();
                mImageFileName = taskSnapshot.getMetadata().getName();

                Log.d(TAG, "onSuccess: downloadUrl:  " + mDownloadIUrl.toString());
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
                Log.d(TAG, "onPaused: ");
                mCircleProgressDialogFragment.dismiss();
                showShortSnackBar(mView, "upload paused", SnackBarUtils.TYPE_INFO);
            });
        } else if (type == 3) {
            StorageReference storageReference = mStorageReference.child(userId).child("audios")
                    .child(fileUri.getLastPathSegment());
            uploadTask = storageReference.putFile(fileUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                mDownloadAUrl = taskSnapshot.getDownloadUrl();
                mAudioFileName = taskSnapshot.getMetadata().getName();

                Log.d(TAG, "onSuccess: downloadUrl:  " + mAudioFileName);
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
                Log.d(TAG, "onPaused: ");
                mCircleProgressDialogFragment.dismiss();
                showShortSnackBar(mView, "upload paused", SnackBarUtils.TYPE_INFO);
            });
        }
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Log.d(TAG, "displaySpeechRecognizer: ");
        loseFocus();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
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
                    Log.d(TAG, "left onStateChanged: " + newState);
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        mLeftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }
                if (bottomSheet == bottomSheetRight) {
                    Log.d(TAG, "right onStateChanged: " + newState);
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
        LinearLayoutCompat takePhoto = mView.findViewById(R.id.bottom_sheet_item_take_photo);
        LinearLayoutCompat chooseImage = mView.findViewById(R.id.bottom_sheet_item_choose_image);
        LinearLayoutCompat recording = mView.findViewById(R.id.bottom_sheet_item_recording);
        LinearLayoutCompat moveToTrash = mView.findViewById(R.id.bottom_sheet_item_move_to_trash);
        LinearLayoutCompat permanentDelete = mView.findViewById(R.id.bottom_sheet_item_permanent_delete);
        LinearLayoutCompat makeACopy = mView.findViewById(R.id.bottom_sheet_item_make_a_copy);
        LinearLayoutCompat send = mView.findViewById(R.id.bottom_sheet_item_send);
        takePhoto.setOnClickListener(this);
        chooseImage.setOnClickListener(this);
        recording.setOnClickListener(this);
        moveToTrash.setOnClickListener(this);
        permanentDelete.setOnClickListener(this);
        makeACopy.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    private void hideSoftKeyboard() {
        if (mInputMethodManager != null) {
            if (mEditable) {
                mInputMethodManager.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), 0);
                mHandler.postDelayed(new BottomSheet(), 100);
            }
        }
    }

    private void showSoftKeyboard() {
        if (mInputMethodManager != null) {
            mInputMethodManager.showSoftInput(mActivity.getWindow().getDecorView(), 0);
        }
    }

    private void changeBottomSheetState() {
        // isLeftOrRight值为真是左，假则是右
        if (isLeftOrRight) {
            // 首先检查RightBottomSheet是否启用，如果是则隐藏
            if (mRightBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED
                    || mRightBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
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
            if (mLeftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED
                    || mLeftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
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
        Log.d(TAG, "loseFocus: ");
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
        view.getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        () -> {
                            //比较主视图布局与当前布局的大小
                            int heightDiff = mView.getRootView().getHeight() - view.getHeight();
                            if (heightDiff > 100) {
                                //大小超过100时，一般为显示虚拟键盘事件
                                if (mEditable && !Utils.isXLargeTablet(mActivity)) {
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
                        }
                );
    }

    /**
     * 更新显示图片信息
     *
     * @param mFileUri 图片地址
     */
    private void showImage(Uri mFileUri) {
        //当图片地址不为空时，首先从本地读取bitmap设置图片，bitmap为空，则从网络加载
        //图片地址为空则不加载图片
        if (mFileUri != null) {
            Picasso.with(mActivity)
                    .load(mFileUri)
                    .memoryPolicy(NO_CACHE, NO_STORE)
                    .placeholder(R.drawable.ic_cloud_download_black_24dp)
                    .config(Bitmap.Config.RGB_565)
                    .into(mImage);
            mImage.post(new Runnable() {
                @Override
                public void run() {
                    CircularRevealUtils.show(mImage);
                }
            });

            mContentTextInputLayout.setPadding(0, 0, 0, mPaddingBottom);
        } else {
            mImageFileName = null;
            //mImageCardView.setVisibility(View.GONE);
        }
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    private class BottomSheet implements Runnable {

        @Override
        public void run() {
            changeBottomSheetState();
        }
    }
}