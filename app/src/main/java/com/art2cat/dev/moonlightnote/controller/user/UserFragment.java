package com.art2cat.dev.moonlightnote.controller.user;


import static android.app.Activity.RESULT_OK;
import static com.art2cat.dev.moonlightnote.constants.Constants.ALBUM_CHOOSE;
import static com.art2cat.dev.moonlightnote.constants.Constants.BUS_FLAG_ALBUM;
import static com.art2cat.dev.moonlightnote.constants.Constants.BUS_FLAG_CAMERA;
import static com.art2cat.dev.moonlightnote.constants.Constants.BUS_FLAG_DELETE_ACCOUNT;
import static com.art2cat.dev.moonlightnote.constants.Constants.BUS_FLAG_EMAIL;
import static com.art2cat.dev.moonlightnote.constants.Constants.BUS_FLAG_USERNAME;
import static com.art2cat.dev.moonlightnote.constants.Constants.FB_STORAGE_REFERENCE;
import static com.art2cat.dev.moonlightnote.constants.Constants.TAKE_PICTURE;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

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
import com.art2cat.dev.moonlightnote.constants.Constants;
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
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * A simple {@link BaseFragment} subclass.
 */
public class UserFragment extends BaseFragment implements View.OnClickListener {

  private final String TAG = UserFragment.class.getName();
  private View view;
  private CircleImageView circleImageView;
  private AppCompatTextView nicknameTextView;
  private AppCompatTextView emailTextView;
  private AdView adView;
  private CircleProgressDialogFragment circleProgressDialogFragment;
  private FirebaseUser firebaseUser;
  private User user;
  //    private Uri fileUri = null;
  private StorageReference storageReference;

  public UserFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(FB_STORAGE_REFERENCE);

    circleProgressDialogFragment =
        CircleProgressDialogFragment.newInstance(getString(R.string.prograssBar_uploading));
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_user, container, false);
    circleImageView = view.findViewById(R.id.user_head_picture);
    nicknameTextView = view.findViewById(R.id.user_nickname);
    emailTextView = view.findViewById(R.id.user_email);
    adView = view.findViewById(R.id.banner_adView);

    activity.setTitle(R.string.title_activity_user);

    user = UserUtils.getUserFromCache(activity.getApplicationContext());
    AdRequest adRequest;
    if (BuildConfig.DEBUG) {
      adRequest = new AdRequest.Builder()
          .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
          .addTestDevice("0ACA1878D607E6C4360F91E0A0379C2F")
          .addTestDevice("4DA2263EDB49C1F2C00F9D130B823096")
          .build();
    } else {
      adRequest = new AdRequest.Builder().build();

    }
    adView.loadAd(adRequest);

    initView();
    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (!SPUtils.getBoolean(activity, "User", "google", false)) {
      circleImageView.setOnClickListener(this);
      nicknameTextView.setOnClickListener(this);
      setHasOptionsMenu(true);
    }

    adView.setAdListener(new AdListener() {
      @Override
      public void onAdLoaded() {
        super.onAdLoaded();
        CircularRevealUtils.show(adView);
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
    adView.destroy();
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
          FragmentUtils
              .replaceFragment(getFragmentManager(), R.id.common_fragment_container, fragment,
                  FragmentUtils.REPLACE_BACK_STACK);
        }
        break;
      case R.id.action_close_account:
        ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment
            .newInstance(getString(R.string.delete_account_title),
                getString(R.string.delete_account_content),
                Constants.EXTRA_TYPE_CDF_DELETE_ACCOUNT);
        confirmationDialogFragment.show(activity.getFragmentManager(), "delete account");
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void initView() {
    String nickname = user.getNickname();
    if (Objects.nonNull(nickname)) {
      this.nicknameTextView.setText(nickname);
      user.setNickname(nickname);
    } else {
      this.nicknameTextView.setText(R.string.user_setNickname);
    }
    String email = user.getEmail();
    if (Objects.nonNull(email)) {
      user.setEmail(email);
      emailTextView.setText(email);
    }

    displayImage(Uri.parse(user.getPhotoUrl()));
  }

  private void displayImage(Uri mDownloadUrl) {
    if (Objects.nonNull(mDownloadUrl)) {
      Picasso.with(MoonlightApplication.getContext()).load(mDownloadUrl)
          .memoryPolicy(NO_CACHE, NO_STORE)
          .config(Bitmap.Config.RGB_565).into(circleImageView);
      user.setPhotoUrl(mDownloadUrl.toString());
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
        inputDialogFragment1.show(activity.getFragmentManager(), "setNickname");
        break;
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void busAction(BusEvent busEvent) {
    //这里更新视图或者后台操作,从busAction获取传递参数.
    if (Objects.nonNull(busEvent)) {
      switch (busEvent.getFlag()) {
        case BUS_FLAG_CAMERA:
          onCameraClick(view);
          break;
        case BUS_FLAG_ALBUM:
          onAlbumClick(view);
          break;
        case BUS_FLAG_USERNAME:
          if (Objects.nonNull(busEvent.getMessage())) {
            nicknameTextView.setText(busEvent.getMessage());
            user.setNickname(busEvent.getMessage());
            UserUtils.saveUserToCache(activity.getApplicationContext(), user);
            updateProfile(busEvent.getMessage(), null);
          }
          break;
        case BUS_FLAG_EMAIL:
          if (busEvent.getMessage().contains("@")) {
            AuthUtils.sendRPEmail(activity, view, busEvent.getMessage());
          }
          break;
        case BUS_FLAG_DELETE_ACCOUNT:
          if (Objects.nonNull(busEvent.getMessage())) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential =
                EmailAuthProvider.getCredential(user.getEmail(), busEvent.getMessage());
            FDatabaseUtils.emptyNote(user.getUid());
            FDatabaseUtils.emptyTrash(user.getUid());

            user.reauthenticate(credential)
                .addOnCompleteListener(task -> user.delete().addOnCompleteListener(task1 -> {
                  if (task1.isSuccessful()) {
                    if (BuildConfig.DEBUG) {
                      Log.d(TAG, "busAction: User account deleted.");
                    }
                    startActivity(new Intent(activity, LoginActivity.class));
                  }
                }).addOnFailureListener(e -> {
                  if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.toString());
                  }
                }));
          }
          break;
      }
    }
  }

  private void updateProfile(@Nullable String nickname, @Nullable Uri uri) {
    UserProfileChangeRequest profileUpdates = null;
    if (Objects.nonNull(nickname)) {
      profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(nickname).build();
    }

    if (Objects.nonNull(uri)) {
      profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "updateProfile Photo uri: " + profileUpdates.getPhotoUri().toString());
      }
    }

    if (Objects.nonNull(profileUpdates)) {
      firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          if (BuildConfig.DEBUG) {
            Log.d(TAG, "updateProfile: User profile updated.");
          }
          if (Objects.nonNull(user)) {
            BusEventUtils.post(Constants.BUS_FLAG_UPDATE_USER, null);
            UserUtils.updateUser(firebaseUser.getUid(), user);
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
          if (Objects.nonNull(fileUri)) {
            uploadFromUri(fileUri, firebaseUser.getUid());
            galleryAddPic(fileUri);
          } else {
            Toast.makeText(activity, "fileUri is Null", Toast.LENGTH_SHORT).show();
          }
        }
        break;
      case ALBUM_CHOOSE:
        if (resultCode == RESULT_OK) {
          uploadFromUri(data.getData(), firebaseUser.getUid());
        }
        break;
      default:
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void uploadFromUri(Uri fileUri, String userId) {

    circleProgressDialogFragment.show(activity.getFragmentManager(), "progress");

    StorageReference photoRef = storageReference.child(userId).child("avatar")
        .child(fileUri.getLastPathSegment());

    // Upload file to Firebase Storage
    StorageTask<UploadTask.TaskSnapshot> uploadTask = photoRef.putFile(fileUri);

    uploadTask.addOnSuccessListener(taskSnapshot -> {
      String filePath = Objects.requireNonNull(taskSnapshot.getMetadata()).getPath();
      user.setPhotoUrl(filePath);
      displayImage(Uri.parse(filePath));
      UserUtils.saveUserToCache(activity.getApplicationContext(), user);
      updateProfile(null, Uri.parse(filePath));
      circleProgressDialogFragment.dismiss();
    }).addOnFailureListener(e -> {
      circleProgressDialogFragment.dismiss();
      Log.e(TAG, "uploadFromUri: onFailure: ", e);
      displayImage(firebaseUser.getPhotoUrl());
    });
  }

}
