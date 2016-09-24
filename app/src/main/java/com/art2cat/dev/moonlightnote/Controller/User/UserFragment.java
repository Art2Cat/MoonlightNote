package com.art2cat.dev.moonlightnote.Controller.User;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.PickPicFragment;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusAction;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusProvider;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private CircleImageView mCircleImageView;
    private AppCompatTextView mNickname;
    private AppCompatTextView mEmail;
    private AppCompatButton mChangePassword;
    private ProgressDialog progressDialog;
    private FirebaseUser user;
    private User mUser;

    private Uri mFileUri = null;

    private DatabaseReference myReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private String mFileName;
    private Uri mDownloadUrl;


    private static final int STORAGE_PERMS = 101;
    private static final int TAKE_PICTURE = 102;
    private static final int ALBUM_CHOOSE = 103;
    private static final String TAG = "UserFragment";

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取Bus单例，并注册
        BusProvider.getInstance().register(this);
        //获取FirebaseUser对象
        user = FirebaseAuth.getInstance().getCurrentUser();

        mUser = Utils.getUserInfo(user);

        //获取firebaseStorage实例
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE);

        myReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_user, container, false);
        mCircleImageView = (CircleImageView) mView.findViewById(R.id.user_head_picture);
        mNickname = (AppCompatTextView) mView.findViewById(R.id.user_nickname);
        mEmail = (AppCompatTextView) mView.findViewById(R.id.user_email);
        mChangePassword = (AppCompatButton) mView.findViewById(R.id.user_change_password);

        initView();
        updateUI(Uri.parse(mUser.getAvatarUrl()));
        mCircleImageView.setOnClickListener(this);
        mChangePassword.setOnClickListener(this);
        mNickname.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
        updateUser(user.getUid(), mUser);
        updateProfile(mUser.getUsername(), mDownloadUrl);
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    private void initView() {
        String photoUri = mUser.getAvatarUrl();
        if (photoUri != null) {
            BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
            bitmapUtils.display(mCircleImageView, photoUri);
        }
        String nickname = mUser.getUsername();
        if (nickname != null) {
            mNickname.setText(nickname);
        } else {
            mNickname.setText(R.string.user_setNickname);
        }
        String email = mUser.getEmail();
        if (email != null) {
            mEmail.setText(email);
        }
    }

    private void updateUI(Uri mDownloadUrl) {
        if (mDownloadUrl != null) {
            BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
            bitmapUtils.display(mCircleImageView, mDownloadUrl.toString());

        } else {
            mFileName = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_picture:
                PickPicFragment pickPicFragment = new PickPicFragment();
                pickPicFragment.show(getFragmentManager(), "PICK_PIC");
                break;
            case R.id.user_nickname:
                showDialog(1);
                break;
            case R.id.user_change_password:
                break;
        }
    }

    private void showDialog(int type) {
        switch (type) {
            case 1:
                SetNicknameFragment setNicknameFragment = new SetNicknameFragment();
                setNicknameFragment.show(getFragmentManager(), "labelDialog");
                break;
            case 2:
                break;
        }
    }

    @Subscribe
    public void busAction(BusAction busAction) {
        //这里更新视图或者后台操作,从TestAction获取传递参数.
        if (busAction.getString() != null) {
            mNickname.setText(busAction.getString());
            mUser.setUsername(busAction.getString());
        }

        if (busAction.getInt() != 0) {
            switch (busAction.getInt()) {
                case 1:
                    onCameraClick();
                    break;
                case 2:
                    onAlbumClick();
                    break;
            }
        }
    }

    private void updateProfile(String nickname, Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK && mFileUri != null) {
                    Log.d(TAG, "mFileUri:" + mFileUri);
                    uploadFromUri(data.getData(), user.getUid(), 0);
                }
                break;
            case ALBUM_CHOOSE:
                if (resultCode == RESULT_OK && mFileUri != null) {
                    Log.d(TAG, "mFileUri:" + mFileUri);
                    uploadFromUri(data.getData(), user.getUid(), 0);

                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @AfterPermissionGranted(STORAGE_PERMS)
    private void onCameraClick() {
        // Check that we have permission to read images from external storage.
        String perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!EasyPermissions.hasPermissions(getActivity(), perm)) {
            EasyPermissions.requestPermissions(getActivity(), "If you want to do this continue, you should give App storage permission ",
                    STORAGE_PERMS, perm);
            return;
        }
        // Choose file storage location, must be listed in res/xml/file_paths.xml
        File dir = new File(Environment.getExternalStorageDirectory() + "/photos");
        File file = new File(dir, UUID.randomUUID().toString() + ".jpg");
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdir();
            }
            boolean created = file.createNewFile();
            Log.d(TAG, "file.createNewFile:" + file.getAbsolutePath() + ":" + created);
        } catch (IOException e) {
            Log.e(TAG, "file.createNewFile" + file.getAbsolutePath() + ":FAILED", e);
        }

        // Create content:// URI for file, required since Android N
        // See: https://developer.android.com/reference/android/support/v4/content/FileProvider.html

        mFileUri = FileProvider.getUriForFile(getActivity(), "com.art2cat.dev.moonlightnote.file", file);
        Log.i(TAG, "file: " + mFileName);
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
            EasyPermissions.requestPermissions(getActivity(), "If you want to do this continue, you should give App storage permission ",
                    STORAGE_PERMS, perm);
            return;
        }

        // Choose file storage location, must be listed in res/xml/file_paths.xml
        File dir = new File(Environment.getExternalStorageDirectory() + "/photos");
        File file = new File(dir, UUID.randomUUID().toString() + ".jpg");
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdir();
            }
            boolean created = file.createNewFile();
            Log.d(TAG, "file.createNewFile:" + file.getAbsolutePath() + ":" + created);
        } catch (IOException e) {
            Log.e(TAG, "file.createNewFile" + file.getAbsolutePath() + ":FAILED", e);
        }

        mFileUri = FileProvider.getUriForFile(getActivity(), "com.art2cat.dev.moonlightnote.fileprovider", file);
        Log.i(TAG, "file: " + mFileName);
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setType("image/*");
        albumIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        if (albumIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(albumIntent, ALBUM_CHOOSE);
        } else {
            SnackBarUtils.longSnackBar(mView, "No Album!", SnackBarUtils.TYPE_WARNING).show();
        }
    }

    private void uploadFromUri(Uri fileUri, String userId, int type) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        StorageTask<UploadTask.TaskSnapshot> uploadTask = null;

        if (type == 0) {
            // Get a reference to store file at photos/<FILENAME>.jpg
            StorageReference photoRef = mStorageReference.child(userId).child("avatar")
                    .child(fileUri.getLastPathSegment());

            // Upload file to Firebase Storage
            uploadTask = photoRef.putFile(fileUri);
        } else if (type == 1) {
            StorageReference storageReference = mStorageReference.child(userId).child("userconfig")
                    .child(fileUri.getLastPathSegment());
            uploadTask = storageReference.putFile(fileUri);
        }

        if (uploadTask != null) {
            uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDownloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: downloadUrl:  " + mDownloadUrl.toString());
                    progressDialog.dismiss();
                    mUser.setAvatarUrl(mDownloadUrl.toString());
                    updateUI(mDownloadUrl);
                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    mFileName = null;
                    mDownloadUrl = null;
                    updateUI(mDownloadUrl);
                }
            });
        } else {
            Log.w(TAG, "uploadFromUri: failed");
        }

    }

    public void updateUser(String userId, User user) {
        Log.d(TAG, "createUser: ");
        myReference.child("user").push();

        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user/" + userId, userValues);

        myReference.updateChildren(childUpdates);
    }
}
