package com.art2cat.dev.moonlightnote.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.model.Constants;
import com.art2cat.dev.moonlightnote.model.Moonlight;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.art2cat.dev.moonlightnote.model.Constants.ALBUM_CHOOSE;
import static com.art2cat.dev.moonlightnote.model.Constants.CAMERA_PERMS;
import static com.art2cat.dev.moonlightnote.model.Constants.STORAGE_PERMS;
import static com.art2cat.dev.moonlightnote.model.Constants.TAKE_PICTURE;
import static org.greenrobot.eventbus.EventBus.TAG;

/**
 * Created by Rorschach
 * on 2017/1/8 14:32.
 */

public abstract class BaseFragment extends Fragment {
    private static final String KEY_INDEX = "index";
    protected Activity mActivity;
    private int mCurrentIndex = 0;

    /**
     * 更改toolbar三个点颜色
     *
     * @param activity Activity
     * @param color    颜色
     */
    public static void setOverflowButtonColor(Activity activity, final int color) {
        @SuppressLint("PrivateResource") final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<>();
                decorView.findViewsWithText(outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }
                AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
                overflow.setColorFilter(color);
                removeOnGlobalLayoutListener(decorView, this);
            }
        });
    }

    /**
     * 移除布局监听器
     *
     * @param v        view
     * @param listener 监听器
     */
    public static void removeOnGlobalLayoutListener(View v,
                                                    ViewTreeObserver.OnGlobalLayoutListener listener
    ) {
        v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    public void showShortSnackBar(View view, String content, int type) {
        SnackBarUtils.shortSnackBar(view, content, type).show();
    }

    public void showLongSnackBar(View view, String content, int type) {
        SnackBarUtils.longSnackBar(view, content, type).show();
    }

    public void showShortToast(String content) {
        Toast.makeText(MoonlightApplication.getContext(), content, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String content) {
        Toast.makeText(MoonlightApplication.getContext(), content, Toast.LENGTH_LONG).show();
    }

    public void setArgs(Moonlight moonlight, int flag) {
        Bundle args = new Bundle();
        args.putParcelable("moonlight", moonlight);
        args.putInt("flag", flag);
        this.setArguments(args);
    }

    private void grantCameraPermission() {
        if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.CAMERA)) {
            EasyPermissions.requestPermissions(mActivity, "If you want to do this continue, " +
                            "you should give App camera permission ",
                    CAMERA_PERMS, Manifest.permission.CAMERA);
        }
    }

    protected void grantStoragePermission() {
        if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(mActivity,
                    "If you want to do this continue, " +
                            "you should give App storage permission ",
                    STORAGE_PERMS, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @AfterPermissionGranted(STORAGE_PERMS)
    protected void onAlbumClick(View mView, boolean mEditable) {
        // Check that we have permission to read images from external storage.
        grantStoragePermission();

        // Choose file storage location, must be listed in res/xml/file_paths.xml
        File dir = new File(Environment.getExternalStorageDirectory()
                + "/MoonlightNote/.image");
        File file = new File(dir, UUID.randomUUID().toString() + ".jpg");
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdirs();
            }
            boolean created = file.createNewFile();
            if (BuildConfig.DEBUG)
                Log.d(TAG, "file.createNewFile:" + file.getAbsolutePath() + ":" + created);
        } catch (IOException e) {
            Log.e(TAG, "file.createNewFile" + file.getAbsolutePath() + ":FAILED", e);
        }

        Uri fileUri = FileProvider.getUriForFile(mActivity, Constants.FILE_PROVIDER, file);
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

    @AfterPermissionGranted(CAMERA_PERMS)
    protected void onCameraClick(View mView, Uri mFileUri, boolean mEditable) {
        // Check that we have permission to read images from external storage.
        grantStoragePermission();
        grantCameraPermission();
        // Choose file storage location, must be listed in res/xml/file_paths.xml
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/MoonlightNote/.image");
        File file = new File(dir, UUID.randomUUID().toString() + ".jpg");
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdirs();
            }
            boolean created = file.createNewFile();
            if (BuildConfig.DEBUG)
                Log.d(TAG, "created:" + created);
        } catch (IOException e) {
            Log.e(TAG, "file.createNewFile" + file.getAbsolutePath() + ":FAILED", e);
        }

        // Create content:// URI for file, required since Android N
        // See: https://developer.android.com/reference/android/support/v4/content/FileProvider.html

        mFileUri = FileProvider.getUriForFile(mActivity, Constants.FILE_PROVIDER, file);
        if (BuildConfig.DEBUG)
            Log.i(TAG, "file: " + mFileUri);

        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        if (takePicIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            startActivityForResult(takePicIntent, TAKE_PICTURE);
            mEditable = false;
            if (BuildConfig.DEBUG)
                Log.d(TAG, "onCameraClick: ");
        } else {
            showLongSnackBar(mView, "No Camera!", SnackBarUtils.TYPE_WARNING);
        }
    }

    public interface DrawerLocker {
        void setDrawerEnabled(boolean enabled);
    }
}
