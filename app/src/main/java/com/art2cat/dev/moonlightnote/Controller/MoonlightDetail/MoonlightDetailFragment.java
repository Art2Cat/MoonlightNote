package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.LinearLayoutCompat;
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
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.art2cat.dev.moonlightnote.Controller.CommonActivity;
import com.art2cat.dev.moonlightnote.Controller.CommonDialogFragment.ProgressDialogFragment;
import com.art2cat.dev.moonlightnote.Controller.Moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.AudioPlayer;
import com.art2cat.dev.moonlightnote.Utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.Utils.Firebase.StorageUtils;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.art2cat.dev.moonlightnote.Utils.PermissionUtils;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.art2cat.dev.moonlightnote.Model.Constants.ALBUM_CHOOSE;
import static com.art2cat.dev.moonlightnote.Model.Constants.CAMERA_PERMS;
import static com.art2cat.dev.moonlightnote.Model.Constants.RECORD_AUDIO;
import static com.art2cat.dev.moonlightnote.Model.Constants.STORAGE_PERMS;
import static com.art2cat.dev.moonlightnote.Model.Constants.TAKE_PICTURE;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MoonlightDetailFragment extends Fragment implements
        View.OnClickListener, View.OnFocusChangeListener {
    private static final String TAG = "MoonlightDetailFragment";
    private View mView;
    private Toolbar mToolbar;
    private ContentFrameLayout mContentFrameLayout;
    private LinearLayoutCompat mBottomBarContainer;
    private LinearLayoutCompat mAudioContainer;
    private TextInputLayout mTitleTextInputLayout;
    private TextInputLayout mContentTextInputLayout;
    private TextInputEditText mTitle;
    private TextInputEditText mContent;
    private AppCompatTextView mShowDuration;
    private AppCompatTextView mDisplayTime;
    private AppCompatButton mBottomBarLeft;
    private AppCompatButton mBottomBarRight;
    private AppCompatButton mDeleteImage;
    private AppCompatButton mDeleteAudio;
    private AppCompatButton mPlayingAudio;
    private CardView mImageCardView;
    private CardView mAudioCardView;
    private AppCompatImageView mImage;
    private ProgressDialogFragment mProgressDialogFragment;
    private ProgressBar mAudioPlayerPB;
    private CoordinatorLayout mCoordinatorLayout;
    private BottomSheetBehavior mRightBottomSheetBehavior;
    private BottomSheetBehavior mLeftBottomSheetBehavior;
    private InputMethodManager mInputMethodManager;
    private ArrayAdapter<String> mArrayAdapter;
    private Moonlight moonlight;
    private boolean mCreateFlag = true;
    private boolean mEditFlag = false;
    private boolean mEditable = true;
    private boolean mStartPlaying = true;
    private boolean isLeftOrRight;
    private String mUserId;
    private String mKeyId;
    private String mLabel;
    private int mPaddingBottom;
    private Uri mFileUri = null;
    private FDatabaseUtils mFDatabaseUtils;
    private DatabaseReference mMoonlightRef;
    private ValueEventListener mMoonlightListener;
    private ValueEventListener moonlightListener;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageRef;
    private StorageReference mStorageReference;
    private String mImageFileName;
    private String mAudioFileName;
    private Uri mDownloadIUrl;
    private Uri mDownloadAUrl;
    private File mFile;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private AudioPlayer mAudioPlayer;
    private BitmapUtils mBitmapUtils;
    private InitView myRunnable = new InitView();
    private CommonActivity.FragmentOnTouchListener fragmentOnTouchListener;

    public MoonlightDetailFragment() {
        // Required empty public constructor
    }

    public abstract MoonlightDetailFragment newInstance();

    public abstract MoonlightDetailFragment newInstance(String keyid);

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置显示OptionsMenu
        setHasOptionsMenu(true);
        //获取Bus单例，并注册
        //BusProvider.getInstance().register(this);
        EventBus.getDefault().register(this);
        //获取用户id
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //获取FirebaseDatabase实例
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFDatabaseUtils = new FDatabaseUtils(getActivity(), mDatabaseReference, mUserId);

        //新建moonlight对象
        moonlight = new Moonlight();
        //获取firebaseStorage实例
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = firebaseStorage.getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE);

        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        mPaddingBottom = getResources().getDimensionPixelOffset(R.dimen.padding_bottom);

        //当能argument不为空时，从argument中获取keyId，同时调用getMoonlight方法获取moonlight信息，editFlag设为true
        if (getArguments() != null) {
            mKeyId = getArguments().getString("keyId");
            int trashTag = getArguments().getInt("trash");
            if (trashTag == 0) {
                mFDatabaseUtils.getDataFromDatabase(mKeyId, Constants.EXTRA_TYPE_MOONLIGHT);
                //getMoonlight(mKeyId, Constants.EXTRA_TYPE_MOONLIGHT);
                mEditFlag = true;
                mCreateFlag = false;
            } else {
                mFDatabaseUtils.getDataFromDatabase(mKeyId, Constants.EXTRA_TYPE_TRASH);
                //getMoonlight(mKeyId, Constants.EXTRA_TYPE_TRASH);
                mEditFlag = true;
                mCreateFlag = false;
                mEditable = false;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //视图初始化
        mView = inflater.inflate(R.layout.fragment_moonlight_detail, container, false);

        getActivity().setTitle(null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mToolbar = ((CommonActivity) getActivity()).mToolbar;
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);

        mContentFrameLayout = (ContentFrameLayout) mView.findViewById(R.id.view_parent);
        mTitle = (TextInputEditText) mView.findViewById(R.id.title_TIET);
        mContent = (TextInputEditText) mView.findViewById(R.id.content_TIET);
        mContentTextInputLayout = (TextInputLayout) mView.findViewById(R.id.content_TIL);
        mImage = (AppCompatImageView) mView.findViewById(R.id.moonlight_image);
        //mImageCardView = (CardView) mView.findViewById(R.id.image_container);
        mAudioCardView = (CardView) mView.findViewById(R.id.audio_container);
        mAudioContainer = (LinearLayoutCompat) mView.findViewById(R.id.audio_container_inner);
        //mDeleteImage = (AppCompatButton) mView.findViewById(R.id.delete_image);

        mDeleteAudio = (AppCompatButton) mView.findViewById(R.id.delete_audio);
        mPlayingAudio = (AppCompatButton) mView.findViewById(R.id.playing_audio_button);
        mShowDuration = (AppCompatTextView) mView.findViewById(R.id.moonlight_audio_duration);
        mAudioPlayerPB = (ProgressBar) mView.findViewById(R.id.moonlight_audio_progressBar);
        mDisplayTime = (AppCompatTextView) mView.findViewById(R.id.bottom_bar_display_time);
        mCoordinatorLayout = (CoordinatorLayout) mView.findViewById(R.id.bottom_sheet_container);
        mBottomBarContainer = (LinearLayoutCompat) mView.findViewById(R.id.bottom_bar_container);
        mBottomBarLeft = (AppCompatButton) mView.findViewById(R.id.bottom_bar_left);
        mBottomBarRight = (AppCompatButton) mView.findViewById(R.id.bottom_bar_right);
        mTitle.setOnFocusChangeListener(this);
        mContent.setOnFocusChangeListener(this);
        mBitmapUtils = new BitmapUtils(getActivity());
        mAudioPlayer = new AudioPlayer(mAudioPlayerPB, mShowDuration);

        mProgressDialogFragment = ProgressDialogFragment.newInstance();

        showBottomSheet();

        if (mEditable) {
            //获取系统当前时间
            long date = System.currentTimeMillis();
            moonlight.setDate(date);
            String time = Utils.timeFormat(getActivity().getApplicationContext(), new Date(date));
            if (time != null) {
                String timeFormat = "Edited: " + time;
                mDisplayTime.setText(timeFormat);
            }
            onCheckSoftKeyboardState(mView);
            //mDeleteImage.setOnClickListener(this);
            mImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Delete Image?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            StorageUtils.removePhoto(mView, mUserId, moonlight.getImageName());
                            mImage.setVisibility(View.GONE);
                            moonlight.setImageName(null);
                            moonlight.setImageUrl(null);
                        }
                    }).setNegativeButton("Cancel", null).create();
                    builder.show();
                    return false;
                }
            });
            mDeleteAudio.setOnClickListener(this);
            mPlayingAudio.setOnClickListener(this);
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

        getActivity().postponeEnterTransition();

        return mView;
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
        mToolbar.setBackgroundColor(color);
        mBottomBarContainer.setBackgroundColor(color);

    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        changeUIColor(R.color.white, getActivity().getTheme());
        //当editFlag为true时延时0.5秒更新UI（防止UI已更新，moonlight数据未载入）
        if (mEditFlag) {
            //long_click_moonlight_menu = mFDatabaseUtils.getMoonlight();
            mHandler.postDelayed(myRunnable, 500);
        }
        if (!mEditable) {
            Log.d(TAG, "onActivityCreated: SnackBar");
            final Snackbar snackbar = SnackBarUtils.longSnackBar(mView, getString(R.string.trash_restore),
                    SnackBarUtils.TYPE_WARNING).setAction(R.string.trash_restore_action,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BusEventUtils.post(Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT, null);
                            mFDatabaseUtils.restoreToNote(moonlight);
                            startActivity(new Intent(getActivity(), MoonlightActivity.class));
                        }
                    });

            fragmentOnTouchListener = new CommonActivity.FragmentOnTouchListener() {
                @Override
                public boolean onTouch(MotionEvent ev) {
                    if (!snackbar.isShown() && ev.getAction() == MotionEvent.ACTION_DOWN) {
                        snackbar.show();
                    }
                    return false;
                }
            };
            ((CommonActivity) getActivity()).registerFragmentOnTouchListener(fragmentOnTouchListener);
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
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        //当moonlight图片，标题，内容不为空空时，添加moonlight到服务器
        if (mCreateFlag && mEditable) {
            if (isEmpty(moonlight)) {
                mFDatabaseUtils.addMoonlight(moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
            }
        }
        //当editFlag为true且moonlight不为空时更新moonlight信息到服务器
        if (mEditable && mEditFlag && moonlight != null && !moonlight.isTrash()) {
            Log.d(TAG, "mKeyId" + mKeyId);
            mFDatabaseUtils.updateMoonlight(mKeyId, moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
        }
        mFDatabaseUtils.removeListener();
        //removeListener();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mAudioPlayer.releasePlayer();
        super.onDestroy();
    }

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
            inflater.inflate(R.menu.moonlight_detail_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_color_picker:
                if (mEditable) {
                    MyColorPickerDialog dialog = new MyColorPickerDialog(getActivity());
                    dialog.setTitle("Color Picker");
                    dialog.setColorListener(new ColorListener() {
                        @Override
                        public void OnColorClick(View v, int color) {
                            //do whatever you want to with the values
                            moonlight.setColor(color);
                            mContentFrameLayout.setBackgroundColor(color);
                            mAudioContainer.setBackgroundColor(color);
                            changeUIColor(color);
                            mEditable = true;
                        }
                    });
                    //customize the dialog however you want
                    dialog.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEmpty(Moonlight moonlight) {
        return moonlight.getImageUrl() != null || moonlight.getAudioUrl() != null || moonlight.getContent() != null
                || moonlight.getTitle() != null;
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
                        Uri mAudioUri = FileProvider.getUriForFile(getActivity(), Constants.FILE_PROVIDER, file);
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
            case R.id.moonlight_image:
                //网页浏览图片。。。
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(moonlight.getImageUrl()));
                startActivity(intent);
                break;
            case R.id.playing_audio_button:
                if (moonlight.getAudioName() != null && mStartPlaying) {
                    if (!mAudioPlayer.isPrepared) {
                        mAudioPlayer.prepare(moonlight.getAudioName());
                    }
                    mAudioPlayer.startPlaying();
                    mPlayingAudio.setBackgroundResource(R.drawable.ic_pause_circle_filled_lime_a700_48dp);
                    mAudioPlayer.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            mp.reset();
                            mAudioPlayer.mProgressBar.setProgress(0);
                            mPlayingAudio.setBackgroundResource(R.drawable.ic_play_circle_outline_cyan_400_48dp);
                            mAudioPlayer.isPrepared = false;
                            mStartPlaying = !mStartPlaying;
                        }
                    });
                } else {
                    mAudioPlayer.stopPlaying();
                    mPlayingAudio.setBackgroundResource(R.drawable.ic_play_circle_outline_cyan_400_48dp);
                }
                mStartPlaying = !mStartPlaying;
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
                if (isEmpty(moonlight)) {
                    mFDatabaseUtils.moveToTrash(moonlight);
                } else {
                    BusEventUtils.post(Constants.BUS_FLAG_NULL, null);
                }
                startActivity(new Intent(getActivity(), MoonlightActivity.class));
                mEditable = false;
                break;
            case R.id.bottom_sheet_item_permanent_delete:
                if (isEmpty(moonlight)) {
                    StorageUtils.removePhoto(mView, mUserId, moonlight.getImageName());
                    StorageUtils.removeAudio(mView, mUserId, moonlight.getAudioName());
                    if (mKeyId != null) {
                        mFDatabaseUtils.removeMoonlight(mKeyId, Constants.EXTRA_TYPE_MOONLIGHT);
                    }
                    moonlight = null;
                } else {
                    BusEventUtils.post(Constants.BUS_FLAG_NULL, null);
                }
                startActivity(new Intent(getActivity(), MoonlightActivity.class));
                break;
            case R.id.bottom_sheet_item_make_a_copy:
                if (isEmpty(moonlight)) {
                    mFDatabaseUtils.addMoonlight(moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
                } else {
                    SnackBarUtils.shortSnackBar(mCoordinatorLayout,
                            getString(R.string.note_binned), SnackBarUtils.TYPE_INFO).show();
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
        ContentResolver contentResolver = getActivity().getContentResolver();
        File dir = new File(Environment.getExternalStorageDirectory() + "/MoonlightNote/.audio");
        File file = new File(dir, UUID.randomUUID().toString() + ".amr");
        FileOutputStream fos = null;
        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
            fos = new FileOutputStream(file);
            try {

                byte[] buffer = new byte[4 * 1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fos.close();
                assert inputStream != null;
                inputStream.close();
            }
            return FileProvider.getUriForFile(getActivity(), Constants.FILE_PROVIDER, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @AfterPermissionGranted(CAMERA_PERMS)
    private void onCameraClick() {
        // Check that we have permission to read images from external storage.
        String perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String perm1 = Manifest.permission.CAMERA;
        if (!EasyPermissions.hasPermissions(getActivity(), perm) &&
                !EasyPermissions.hasPermissions(getActivity(), perm1)) {
            PermissionUtils.requestStorage(getActivity(), perm);
            PermissionUtils.requestCamera(getActivity(), perm1);
            return;
        }
        if (!EasyPermissions.hasPermissions(getActivity(), perm)) {
            PermissionUtils.requestStorage(getActivity(), perm);
            return;
        }
        if (!EasyPermissions.hasPermissions(getActivity(), perm1)) {
            PermissionUtils.requestCamera(getActivity(), perm1);
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

        // Create content:// URI for file, required since Android N
        // See: https://developer.android.com/reference/android/support/v4/content/FileProvider.html

        mFileUri = FileProvider.getUriForFile(getActivity(), Constants.FILE_PROVIDER, mFile);
        Log.i(TAG, "file: " + mFileUri);

        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        if (takePicIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePicIntent, TAKE_PICTURE);
            mEditable = false;
            Log.d(TAG, "onCameraClick: ");
        } else {
            SnackBarUtils.longSnackBar(mView, "No Camera!", SnackBarUtils.TYPE_WARNING).show();
        }
    }

    @AfterPermissionGranted(STORAGE_PERMS)
    private void onAlbumClick() {
        // Check that we have permission to read images from external storage.
        String perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!EasyPermissions.hasPermissions(getActivity(), perm)) {
            PermissionUtils.requestStorage(getActivity(), perm);
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

        Uri fileUri = FileProvider.getUriForFile(getActivity(), Constants.FILE_PROVIDER, mFile);
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setType("image/*");
        albumIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        if (albumIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(albumIntent, ALBUM_CHOOSE);
            mEditable = false;
        } else {
            SnackBarUtils.longSnackBar(mView, "No Album!", SnackBarUtils.TYPE_WARNING).show();
        }
    }

    @AfterPermissionGranted(RECORD_AUDIO)
    private void onAudioClick() {
        // Check that we have permission to read images from external storage.
        String perm = Manifest.permission.RECORD_AUDIO;
        String perm1 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!EasyPermissions.hasPermissions(getActivity(), perm) &&
                !EasyPermissions.hasPermissions(getActivity(), perm1)) {
            PermissionUtils.requestStorage(getActivity(), perm1);
            PermissionUtils.requestRecAudio(getActivity(), perm);
            return;
        }
        if (!EasyPermissions.hasPermissions(getActivity(), perm1)) {
            PermissionUtils.requestStorage(getActivity(), perm1);
            return;
        }

        if (!EasyPermissions.hasPermissions(getActivity(), perm)) {
            PermissionUtils.requestRecAudio(getActivity(), perm);
            return;
        }
        // Choose file storage location, must be listed in res/xml/file_paths.xml
        File dir = new File(Environment.getExternalStorageDirectory() + "/MoonlightNote/.audio");

        if (!dir.exists()) {
            dir.mkdirs();
            Log.d(TAG, "onAudioClick: " + dir.mkdirs());
        }
        displaySpeechRecognizer();
    }

    public void uploadFromUri(final Uri fileUri, String userId, int type) {
        if (mProgressDialogFragment != null) {
            mProgressDialogFragment.show(getFragmentManager(), "progress");
        } else {
            mProgressDialogFragment = ProgressDialogFragment.newInstance();
            mProgressDialogFragment.show(getFragmentManager(), "progress");
        }

        StorageTask<UploadTask.TaskSnapshot> uploadTask = null;
        if (type == 0) {

            // Get a reference to store file at photos/<FILENAME>.jpg
            StorageReference photoRef = mStorageReference.child(userId).child("photos")
                    .child(fileUri.getLastPathSegment());
            Log.d(TAG, "uploadFromUri: " + fileUri.getLastPathSegment());
            // Upload file to Firebase Storage
            uploadTask = photoRef.putFile(fileUri);
            uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDownloadIUrl = taskSnapshot.getDownloadUrl();
                    mImageFileName = taskSnapshot.getMetadata().getName();

                    Log.d(TAG, "onSuccess: downloadUrl:  " + mDownloadIUrl.toString());
                    moonlight.setImageName(mImageFileName);
                    moonlight.setImageUrl(mDownloadIUrl.toString());
                    mProgressDialogFragment.dismiss();
                    showImage(fileUri);
                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.toString());
                    mImageFileName = null;
                    mDownloadIUrl = null;
                    mProgressDialogFragment.dismiss();
                    showImage(fileUri);
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onPaused: ");
                    mProgressDialogFragment.dismiss();
                    SnackBarUtils.shortSnackBar(mView, "upload paused", SnackBarUtils.TYPE_INFO).show();
                }
            });
        } else if (type == 3) {
            StorageReference storageReference = mStorageReference.child(userId).child("audios")
                    .child(fileUri.getLastPathSegment());
            uploadTask = storageReference.putFile(fileUri);
            uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDownloadAUrl = taskSnapshot.getDownloadUrl();
                    mAudioFileName = taskSnapshot.getMetadata().getName();

                    Log.d(TAG, "onSuccess: downloadUrl:  " + mAudioFileName.toString());
                    moonlight.setAudioName(mAudioFileName);
                    moonlight.setAudioUrl(mDownloadAUrl.toString());
                    showAudio(mAudioFileName);
                    mProgressDialogFragment.dismiss();
                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.toString());
                    mAudioFileName = null;
                    mDownloadAUrl = null;
                    mProgressDialogFragment.dismiss();
                    showAudio(fileUri.toString());
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onPaused: ");
                    mProgressDialogFragment.dismiss();
                    SnackBarUtils.shortSnackBar(mView, "upload paused", SnackBarUtils.TYPE_INFO).show();
                }
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


    /**
     * 从firebase的database获取moonlight
     *
     * @param keyId 当前读取moonlight的keyId
     */
    private void getMoonlight(String keyId, int type) {
        if (type == 201) {
            mMoonlightRef = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("note").child(keyId);
        } else if (type == 202) {
            mMoonlightRef = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("trash").child(keyId);
        }

        moonlightListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        moonlight = dataSnapshot.getValue(Moonlight.class);
                    }

                    Log.d(TAG, "long_click_moonlight_menu.getId: " + moonlight.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadMoonlight:onCancelled", databaseError.toException());
                SnackBarUtils.shortSnackBar(mView, "Failed to load long_click_moonlight_menu.", SnackBarUtils.TYPE_WARNING).show();

            }
        };
        mMoonlightRef.addValueEventListener(moonlightListener);
    }


    private void removeListener() {
        // Remove mMoonlight value event listener
        if (mMoonlightListener != null) {
            mMoonlightRef.removeEventListener(mMoonlightListener);
        }
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
        LinearLayoutCompat takePhoto = (LinearLayoutCompat) mView.findViewById(R.id.bottom_sheet_item_take_photo);
        LinearLayoutCompat chooseImage = (LinearLayoutCompat) mView.findViewById(R.id.bottom_sheet_item_choose_image);
        LinearLayoutCompat recording = (LinearLayoutCompat) mView.findViewById(R.id.bottom_sheet_item_recording);
        LinearLayoutCompat moveToTrash = (LinearLayoutCompat) mView.findViewById(R.id.bottom_sheet_item_move_to_trash);
        LinearLayoutCompat permanentDelete = (LinearLayoutCompat) mView.findViewById(R.id.bottom_sheet_item_permanent_delete);
        LinearLayoutCompat makeACopy = (LinearLayoutCompat) mView.findViewById(R.id.bottom_sheet_item_make_a_copy);
        LinearLayoutCompat send = (LinearLayoutCompat) mView.findViewById(R.id.bottom_sheet_item_send);
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
                mInputMethodManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                mHandler.postDelayed(new BottomSheet(), 100);
            }
        }
    }

    private void showSoftKeyboard() {
        if (mInputMethodManager != null) {
            mInputMethodManager.showSoftInput(getActivity().getWindow().getDecorView(), 0);
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
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                //比较主视图布局与当前布局的大小
                int heightDiff = mView.getRootView().getHeight() - view.getHeight();
                if (heightDiff > 100) {
                    //大小超过100时，一般为显示虚拟键盘事件
                    if (mEditable) {
                        if (mLeftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            mLeftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        if (mRightBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            mRightBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    } else {
                        //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
                        Log.d(TAG, "onGlobalLayout: ");
                    }
                }
            }
        });
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
            try {
                //.Options options = new BitmapFactory.Options();
                //options.inJustDecodeBounds = true;
                //options.inSampleSize = 2;//宽高压缩为原来的1/2
                //options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                Bitmap bitmap = BitmapFactory.decodeStream(
                        getActivity().getContentResolver().openInputStream(mFileUri), null, null);
                if (bitmap != null) {
                    mImage.setImageBitmap(bitmap);
//                    LocalCacheUtils localCacheUtils = new LocalCacheUtils(getActivity(), new MemoryCacheUtils());
//                    localCacheUtils.setBitmapToLocal(mDownloadIUrl.toString(), bitmap);
                    Picasso.with(getActivity()).load(mFileUri).memoryPolicy(NO_CACHE, NO_STORE).into(mImage);
                } else {
//                    mBitmapUtils.display(mImage, mDownloadIUrl.toString());
                    Picasso.with(getActivity()).load(mDownloadIUrl).memoryPolicy(NO_CACHE, NO_STORE).into(mImage);
                }
                //mImageCardView.setVisibility(View.VISIBLE);
                mImage.setVisibility(View.VISIBLE);
                mContentTextInputLayout.setPadding(0, 0, 0, mPaddingBottom);

            } catch (FileNotFoundException e) {
                Log.d(TAG, "load local file failed" + e.toString());
            }
        } else {
            mImageFileName = null;
            //mImageCardView.setVisibility(View.GONE);
        }
    }

    private void showAudio(String audioFileName) {
        if (moonlight.getAudioDuration() == 0) {
            mAudioPlayer.prepare(audioFileName);
            moonlight.setAudioDuration((long) mAudioPlayer.mDuration);
            mAudioCardView.setVisibility(View.VISIBLE);
            mAudioContainer.setBackgroundColor(moonlight.getColor());
            mContentTextInputLayout.setPadding(0, 0, 0, 0);
        } else {
            mShowDuration.setText(Utils.convert(moonlight.getAudioDuration()));
        }
    }

    private class InitView implements Runnable {

        @Override
        public void run() {
            initView(mEditable);
        }

        private void initView(boolean editable) {

            moonlight = mFDatabaseUtils.getMoonlight();
            if (editable) {
                if (moonlight.getTitle() != null) {
                    mTitle.setText(moonlight.getTitle());
                }
            } else {
                //loseFocus();
                //mTitle.setShowSoftInputOnFocus(false);
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
                Picasso.with(getActivity()).load(Uri.parse(moonlight.getImageUrl())).memoryPolicy(NO_CACHE, NO_STORE).into(mImage);
//                mBitmapUtils.display(mImage, moonlight.getImageUrl());
                //mImageCardView.setVisibility(View.VISIBLE);
                mImage.setVisibility(View.VISIBLE);
                mContentTextInputLayout.setPadding(0, 0, 0, mPaddingBottom);
                if (!editable) {
                    //mDeleteImage.setClickable(false);
                }
            }
            if (moonlight.getAudioUrl() != null) {
                showAudio(moonlight.getAudioName());
                //String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MoonlightNote/.audio/";
                //mAudioPlayer.prepare(dirPath + long_click_moonlight_menu.getAudioName());
                mAudioCardView.setVisibility(View.VISIBLE);
                mContentTextInputLayout.setPadding(0, 0, 0, 0);
                if (!editable) {
                    mDeleteAudio.setClickable(false);
                    mBottomBarLeft.setClickable(false);
                    mBottomBarRight.setClickable(false);
                }
            }

            if (moonlight.getColor() != 0) {
                mContentFrameLayout.setBackgroundColor(moonlight.getColor());
                changeUIColor(moonlight.getColor());
                mAudioContainer.setBackgroundColor(moonlight.getColor());
            }
        }
    }

    private class BottomSheet implements Runnable {

        @Override
        public void run() {
            changeBottomSheetState();
        }
    }
}
