package com.art2cat.dev.moonlightnote.controller.user;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.controller.BaseFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.CircleProgressDialogFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.InputDialogFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.PicturePickerDialogFragment;
import com.art2cat.dev.moonlightnote.controller.login.LoginActivity;
import com.art2cat.dev.moonlightnote.model.BusEvent;
import com.art2cat.dev.moonlightnote.model.Constants;
import com.art2cat.dev.moonlightnote.model.User;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.utils.SPUtils;
import com.art2cat.dev.moonlightnote.utils.UserUtils;
import com.art2cat.dev.moonlightnote.utils.firebase.AuthUtils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.utils.material_animation.CircularRevealUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.art2cat.dev.moonlightnote.model.Constants.ALBUM_CHOOSE;
import static com.art2cat.dev.moonlightnote.model.Constants.BUS_FLAG_ALBUM;
import static com.art2cat.dev.moonlightnote.model.Constants.BUS_FLAG_CAMERA;
import static com.art2cat.dev.moonlightnote.model.Constants.BUS_FLAG_DELETE_ACCOUNT;
import static com.art2cat.dev.moonlightnote.model.Constants.BUS_FLAG_EMAIL;
import static com.art2cat.dev.moonlightnote.model.Constants.BUS_FLAG_USERNAME;
import static com.art2cat.dev.moonlightnote.model.Constants.FB_STORAGE_REFERENCE;
import static com.art2cat.dev.moonlightnote.model.Constants.TAKE_PICTURE;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;


/**
 * A simple {@link BaseFragment} subclass.
 */
public class UserFragment extends BaseFragment implements View.OnClickListener {
    
    private final String TAG = UserFragment.class.getName();
    private View mView;
    private CircleImageView mCircleImageView;
    private AppCompatTextView mNickname;
    private AppCompatTextView mEmail;
    private AdView mAdView;
    private CircleProgressDialogFragment mCircleProgressDialogFragment;
    private FirebaseUser user;
    private User mUser;
    //    private Uri mFileUri = null;
    private StorageReference mStorageReference;
    
    public UserFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(FB_STORAGE_REFERENCE);
        
        mCircleProgressDialogFragment =
                CircleProgressDialogFragment.newInstance(getString(R.string.prograssBar_uploading));
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_user, container, false);
        mCircleImageView = mView.findViewById(R.id.user_head_picture);
        mNickname = mView.findViewById(R.id.user_nickname);
        mEmail = mView.findViewById(R.id.user_email);
        mAdView = mView.findViewById(R.id.banner_adView);
        
        mActivity.setTitle(R.string.title_activity_user);
        
        
        mUser = UserUtils.getUserFromCache(mActivity.getApplicationContext());
        
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
        if (!SPUtils.getBoolean(mActivity, "User", "google", false)) {
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
                if (isResumed() && !isRemoving()) {
                    Fragment fragment = new ChangePasswordFragment();
                    FragmentUtils.replaceFragment(getFragmentManager(), R.id.common_fragment_container, fragment,
                                                  FragmentUtils.REPLACE_BACK_STACK);
                }
                break;
            case R.id.action_close_account:
                ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment
                        .newInstance(getString(R.string.delete_account_title),
                                     getString(R.string.delete_account_content),
                                     Constants.EXTRA_TYPE_CDF_DELETE_ACCOUNT);
                confirmationDialogFragment.show(mActivity.getFragmentManager(), "delete account");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void initView() {
        String nickname = mUser.getNickname();
        if (nickname != null) {
            mNickname.setText(nickname);
            mUser.setNickname(nickname);
        } else {
            mNickname.setText(R.string.user_setNickname);
        }
        String email = mUser.getEmail();
        if (email != null) {
            mUser.setEmail(email);
            mEmail.setText(email);
        }
        
        displayImage(Uri.parse(mUser.getPhotoUrl()));
    }
    
    private void displayImage(Uri mDownloadUrl) {
        if (mDownloadUrl != null) {
            Picasso.with(MoonlightApplication.getContext()).load(mDownloadUrl).memoryPolicy(NO_CACHE, NO_STORE)
                   .config(Bitmap.Config.RGB_565).into(mCircleImageView);
            mUser.setPhotoUrl(mDownloadUrl.toString());
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_picture:
                PicturePickerDialogFragment pickPicFragment = new PicturePickerDialogFragment();
                pickPicFragment.show(getFragmentManager(), "PICK_PIC");
                break;
            case R.id.user_nickname:
                InputDialogFragment inputDialogFragment1 =
                        InputDialogFragment.newInstance(getString(R.string.dialog_set_nickname), 1);
                inputDialogFragment1.show(mActivity.getFragmentManager(), "setNickname");
                break;
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void busAction(BusEvent busEvent) {
        //这里更新视图或者后台操作,从busAction获取传递参数.
        if (busEvent != null) {
            switch (busEvent.getFlag()) {
                case BUS_FLAG_CAMERA:
                    onCameraClick(mView, true);
                    break;
                case BUS_FLAG_ALBUM:
                    onAlbumClick(mView, true);
                    break;
                case BUS_FLAG_USERNAME:
                    if (busEvent.getMessage() != null) {
                        mNickname.setText(busEvent.getMessage());
                        mUser.setNickname(busEvent.getMessage());
                        UserUtils.saveUserToCache(mActivity.getApplicationContext(), mUser);
                        updateProfile(busEvent.getMessage(), null);
                    }
                    break;
                case BUS_FLAG_EMAIL:
                    if (busEvent.getMessage().contains("@")) {
                        AuthUtils.sendRPEmail(mActivity, mView, busEvent.getMessage());
                    }
                    break;
                case BUS_FLAG_DELETE_ACCOUNT:
                    if (busEvent.getMessage() != null) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential =
                                EmailAuthProvider.getCredential(user.getEmail(), busEvent.getMessage());
                        FDatabaseUtils.emptyNote(user.getUid());
                        FDatabaseUtils.emptyTrash(user.getUid());
    
                        user.reauthenticate(credential)
                            .addOnCompleteListener(task -> user.delete().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    if (BuildConfig.DEBUG) { Log.d(TAG, "busAction: User account deleted."); }
                                    startActivity(new Intent(mActivity, LoginActivity.class));
                                }
                            }).addOnFailureListener(e -> {
                                if (BuildConfig.DEBUG) { Log.d(TAG, e.toString()); }
                            }));
                    }
                    break;
            }
        }
    }
    
    private void updateProfile(@Nullable String nickname, @Nullable Uri uri) {
        UserProfileChangeRequest profileUpdates = null;
        if (nickname != null) {
            profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(nickname).build();
        }
        
        if (uri != null) {
            profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "updateProfile Photo uri: " + profileUpdates.getPhotoUri().toString());
            }
        }
        
        if (profileUpdates != null) {
            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (BuildConfig.DEBUG) { Log.d(TAG, "updateProfile: User profile updated."); }
                    if (mUser != null) {
                        BusEventUtils.post(Constants.BUS_FLAG_UPDATE_USER, null);
                        UserUtils.updateUser(user.getUid(), mUser);
                    }
                }
            });
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (mFileUri != null) {
                        uploadFromUri(mFileUri, user.getUid());
                        galleryAddPic(mFileUri);
                    } else {
                        Toast.makeText(mActivity, "mFileUri is Null", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case ALBUM_CHOOSE:
                if (resultCode == RESULT_OK) {
                    uploadFromUri(data.getData(), user.getUid());
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void uploadFromUri(Uri fileUri, String userId) {
        
        mCircleProgressDialogFragment.show(mActivity.getFragmentManager(), "progress");
        
        StorageReference photoRef = mStorageReference.child(userId).child("avatar").child(fileUri.getLastPathSegment());
        
        // Upload file to Firebase Storage
        StorageTask<UploadTask.TaskSnapshot> uploadTask = photoRef.putFile(fileUri);
        
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            mUser.setPhotoUrl(taskSnapshot.getDownloadUrl().toString());
            displayImage(taskSnapshot.getDownloadUrl());
            UserUtils.saveUserToCache(mActivity.getApplicationContext(), mUser);
            updateProfile(null, taskSnapshot.getDownloadUrl());
            mCircleProgressDialogFragment.dismiss();
        }).addOnFailureListener(e -> {
            mCircleProgressDialogFragment.dismiss();
            Log.e(TAG, "uploadFromUri: onFailure: ", e);
            displayImage(user.getPhotoUrl());
        });
    }
    
}
