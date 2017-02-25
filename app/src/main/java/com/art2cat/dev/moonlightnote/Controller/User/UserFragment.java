package com.art2cat.dev.moonlightnote.controller.user;


import android.Manifest;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.CircleProgressDialogFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.InputDialogFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.PickPicDialogFragment;
import com.art2cat.dev.moonlightnote.controller.login.LoginActivity;
import com.art2cat.dev.moonlightnote.model.BusEvent;
import com.art2cat.dev.moonlightnote.model.Constants;
import com.art2cat.dev.moonlightnote.model.User;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.utils.LogUtils;
import com.art2cat.dev.moonlightnote.utils.PermissionUtils;
import com.art2cat.dev.moonlightnote.utils.SPUtils;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.utils.UserUtils;
import com.art2cat.dev.moonlightnote.utils.firebase.AuthUtils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.utils.image_loader.BitmapUtils;
import com.art2cat.dev.moonlightnote.utils.material_animation.CircularRevealUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.art2cat.dev.moonlightnote.model.Constants.ALBUM_CHOOSE;
import static com.art2cat.dev.moonlightnote.model.Constants.BUS_FLAG_ALBUM;
import static com.art2cat.dev.moonlightnote.model.Constants.BUS_FLAG_CAMERA;
import static com.art2cat.dev.moonlightnote.model.Constants.BUS_FLAG_DELETE_ACCOUNT;
import static com.art2cat.dev.moonlightnote.model.Constants.BUS_FLAG_EMAIL;
import static com.art2cat.dev.moonlightnote.model.Constants.BUS_FLAG_USERNAME;
import static com.art2cat.dev.moonlightnote.model.Constants.CAMERA_PERMS;
import static com.art2cat.dev.moonlightnote.model.Constants.FB_STORAGE_REFERENCE;
import static com.art2cat.dev.moonlightnote.model.Constants.FILE_PROVIDER;
import static com.art2cat.dev.moonlightnote.model.Constants.STORAGE_PERMS;
import static com.art2cat.dev.moonlightnote.model.Constants.TAKE_PICTURE;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "UserFragment";
    private View mView;
    private CircleImageView mCircleImageView;
    private AppCompatTextView mNickname;
    private AppCompatTextView mEmail;
    private AdView mAdView;
    private CircleProgressDialogFragment mCircleProgressDialogFragment;
    private FirebaseUser user;
    private User mUser;
    private BitmapUtils mBitmapUtils;
    private Uri mFileUri = null;
    private StorageReference mStorageReference;
    private String mFileName;
    private boolean isChangePwd = false;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取Bus单例，并注册
        EventBus.getDefault().register(this);
        //获取FirebaseUser对象
        user = FirebaseAuth.getInstance().getCurrentUser();
        //获取firebaseStorage实例
        mStorageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(FB_STORAGE_REFERENCE);


        mBitmapUtils = new BitmapUtils(getActivity());

        mCircleProgressDialogFragment = CircleProgressDialogFragment.newInstance(getString(R.string.prograssBar_uploading));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_user, container, false);
        mCircleImageView = (CircleImageView) mView.findViewById(R.id.user_head_picture);
        mNickname = (AppCompatTextView) mView.findViewById(R.id.user_nickname);
        mEmail = (AppCompatTextView) mView.findViewById(R.id.user_email);
        mAdView = (AdView) mView.findViewById(R.id.banner_adView);

        getActivity().setTitle(R.string.title_activity_user);


        mUser = UserUtils.getUserFromCache(getActivity().getApplicationContext());
        LogUtils.getInstance(TAG).setMessage("displayUserInfo: " + mUser.getUid()).debug();

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("0ACA1878D607E6C4360F91E0A0379C2F")
//                .addTestDevice("4DA2263EDB49C1F2C00F9D130B823096")
                .build();
        mAdView.loadAd(adRequest);

        initView();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!SPUtils.getBoolean(getActivity(), "User", "google", false)) {
            mCircleImageView.setOnClickListener(this);
            mNickname.setOnClickListener(this);
            setHasOptionsMenu(true);
        }

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                CircularRevealUtils.show(mAdView);
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
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mAdView.destroy();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_user, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_password:
                Fragment fragment = new ChangePasswordFragment();
                FragmentUtils.replaceFragment(getFragmentManager(),
                        R.id.common_fragment_container,
                        fragment,
                        FragmentUtils.REPLACE_BACK_STACK);
                break;
            case R.id.action_close_account:
                ConfirmationDialogFragment confirmationDialogFragment =
                        ConfirmationDialogFragment.newInstance(getString(R.string.delete_account_title),
                                getString(R.string.delete_account_content),
                                Constants.EXTRA_TYPE_CDF_DELETE_ACCOUNT);
                confirmationDialogFragment.show(getActivity().getFragmentManager(), "delete account");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        String nickname = mUser.getNickname();
        LogUtils.getInstance(TAG).setMessage("initView: " + nickname).debug();
        if (nickname != null) {
            mNickname.setText(nickname);
            mUser.setNickname(nickname);
        } else {
            mNickname.setText(R.string.user_setNickname);
        }
        String email = mUser.getEmail();
        LogUtils.getInstance(TAG).setMessage("initView: " + email).debug();
        if (email != null) {
            mUser.setEmail(email);
            mEmail.setText(email);
        }

        String url = mUser.getPhotoUrl();
        LogUtils.getInstance(TAG).setMessage("initView: " + url);
        if (url != null) {

            mBitmapUtils.display(mCircleImageView, url);
        }
    }

    private void updateUI(Uri mDownloadUrl) {
        if (mDownloadUrl != null) {
            BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
            bitmapUtils.display(mCircleImageView, mDownloadUrl.toString());
            mUser.setPhotoUrl(mDownloadUrl.toString());
        } else {
            mFileName = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_picture:
                PickPicDialogFragment pickPicFragment = new PickPicDialogFragment();
                pickPicFragment.show(getFragmentManager(), "PICK_PIC");
                break;
            case R.id.user_nickname:
                showDialog(1);
                break;
        }
    }

    private void showDialog(int type) {
        switch (type) {
            case 1:
                InputDialogFragment inputDialogFragment1 = InputDialogFragment
                        .newInstance(getString(R.string.dialog_set_nickname), 1);
                inputDialogFragment1.show(getActivity().getFragmentManager(), "setNickname");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void busAction(BusEvent busEvent) {
        //这里更新视图或者后台操作,从busAction获取传递参数.
        if (busEvent != null) {
            switch (busEvent.getFlag()) {
                case BUS_FLAG_CAMERA:
                    Log.d(TAG, "busEvent: " + busEvent.getFlag());
                    onCameraClick();
                    break;
                case BUS_FLAG_ALBUM:
                    Log.d(TAG, "busEvent: " + busEvent.getFlag());
                    onAlbumClick();
                    break;
                case BUS_FLAG_USERNAME:
                    if (busEvent.getMessage() != null) {
                        mNickname.setText(busEvent.getMessage());
                        mUser.setNickname(busEvent.getMessage());
                        UserUtils.saveUserToCache(getActivity().getApplicationContext(), mUser);
                        updateProfile(busEvent.getMessage(), null);
                    }
                    break;
                case BUS_FLAG_EMAIL:
                    if (busEvent.getMessage().contains("@")) {
                        AuthUtils.sendRPEmail(getActivity(), mView, busEvent.getMessage());
                    }
                    break;
                case BUS_FLAG_DELETE_ACCOUNT:
                    if (busEvent.getMessage() != null) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), busEvent.getMessage());
                        FDatabaseUtils.emptyNote(user.getUid());
                        FDatabaseUtils.emptyTrash(user.getUid());

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            LogUtils.getInstance(TAG).setMessage("User account deleted.").debug();
                                            startActivity(new Intent(getActivity(), LoginActivity.class));
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (BuildConfig.DEBUG) Log.d(TAG, e.toString());
                                    }
                                });
                            }
                        });
                    }
                    break;
            }
        }
    }

    private void updateProfile(@Nullable String nickname, @Nullable Uri uri) {
        UserProfileChangeRequest profileUpdates = null;
        if (nickname != null) {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname)
                    .build();
        }

        if (uri != null) {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri)
                    .build();
            LogUtils.getInstance(TAG).setMessage("updateProfile: " + profileUpdates.getPhotoUri().toString()).debug();
        }

        if (profileUpdates != null) {
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                LogUtils.getInstance(TAG).setMessage("User profile updated.").debug();
                                if (mUser != null) {
                                    BusEventUtils.post(Constants.BUS_FLAG_UPDATE_USER, null);
                                    UserUtils.updateUser(user.getUid(), mUser);
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK && mFileUri != null) {
                    uploadFromUri(data.getData(), user.getUid());
                }
                break;
            case ALBUM_CHOOSE:
                if (resultCode == RESULT_OK && mFileUri != null) {
                    uploadFromUri(data.getData(), user.getUid());
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        File file = new File(dir, UUID.randomUUID().toString() + ".jpg");

        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdirs();
            }
            boolean created = file.createNewFile();
            LogUtils.getInstance(TAG)
                    .setMessage("file.createNewFile:" + file.getAbsolutePath() + ":" + created)
                    .debug();
        } catch (IOException e) {
            LogUtils.getInstance(TAG)
                    .setMessage("file.createNewFile" + file.getAbsolutePath() + ":FAILED")
                    .error(e);
        }

        // Create content:// URI for file, required since Android N
        // See: https://developer.android.com/reference/android/support/v4/content/FileProvider.html

        mFileUri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER, file);
        LogUtils.getInstance(TAG).setMessage("file: " + mFileName).info();
        // Create and launch the intent
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        if (takePicIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePicIntent, TAKE_PICTURE);
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
        File file = new File(dir, UUID.randomUUID().toString() + ".jpg");
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdirs();
            }
            boolean created = file.createNewFile();
            LogUtils.getInstance(TAG)
                    .setMessage("file.createNewFile:" + file.getAbsolutePath() + ":" + created)
                    .debug();
        } catch (IOException e) {
            LogUtils.getInstance(TAG)
                    .setMessage("file.createNewFile" + file.getAbsolutePath() + ":FAILED")
                    .error(e);
        }

        mFileUri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER, file);
        LogUtils.getInstance(TAG).setMessage("file: " + mFileName).info();
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setType("image/*");
        albumIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        if (albumIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(albumIntent, ALBUM_CHOOSE);
        } else {
            SnackBarUtils.longSnackBar(mView, "No Album!", SnackBarUtils.TYPE_WARNING).show();
        }
    }

    private void uploadFromUri(Uri fileUri, String userId) {

        mCircleProgressDialogFragment.show(getActivity().getFragmentManager(), "progress");

        StorageReference photoRef = mStorageReference.child(userId).child("avatar")
                .child(fileUri.getLastPathSegment());

        // Upload file to Firebase Storage
        StorageTask<UploadTask.TaskSnapshot> uploadTask = photoRef.putFile(fileUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mUser.setPhotoUrl(taskSnapshot.getDownloadUrl().toString());
                updateUI(taskSnapshot.getDownloadUrl());
                UserUtils.saveUserToCache(getActivity().getApplicationContext(), mUser);
                updateProfile(null, taskSnapshot.getDownloadUrl());
                mCircleProgressDialogFragment.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mCircleProgressDialogFragment.dismiss();
                LogUtils.getInstance(TAG).setMessage("onFailure: ").error(e);
                mFileName = null;
                if (user.getPhotoUrl() != null) {
                    updateUI(user.getPhotoUrl());
                }
            }
        });
    }

}
