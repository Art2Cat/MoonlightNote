package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Controller.Moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.Firebase.DatabaseTools;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusAction;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusProvider;
import com.art2cat.dev.moonlightnote.Utils.CustomSpinner;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.art2cat.dev.moonlightnote.Utils.MenuUtils;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.UserConfigUtils;
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
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MoonlightDetailFragment extends Fragment implements AdapterView.OnItemSelectedListener
        , View.OnClickListener, FragmentManager.OnBackStackChangedListener {
    private View mView;
    private TextInputLayout mTitleTextInputLayout;
    private TextInputLayout mContentTextInputLayout;
    private TextInputEditText mTitle;
    private TextInputEditText mContent;
    private AppCompatTextView mDate;
    private CardView mCardView;
    private AppCompatImageView mPhoto;
    private CustomSpinner mLabelSpinner;
    private AppCompatSpinner mColor;
    private ProgressDialog progressDialog;
    private ProgressBar mProgressBar;
    private LinearLayoutCompat mProgressBarContainer;
    private ArrayAdapter<String> mArrayAdapter;

    private Moonlight moonlight;
    private DatabaseTools mDatabaseTools;
    private boolean mEditFlag;
    private String mUserId;
    private String mKeyId;
    private String mLabel;
    private Uri mFileUri = null;
    private DatabaseReference mMoonlightRef;
    private ValueEventListener mMoonlightListener;
    private ValueEventListener moonlightListener;
    private DatabaseReference myReference;
    private StorageReference mStorageRef;
    private StorageReference mStorageReference;
    private String mFileName;
    private Uri mDownloadUrl;

    private static final String REQUIRED = "Required";

    private static final int STORAGE_PERMS = 101;
    private static final int TAKE_PICTURE = 102;
    private static final int ALBUM_CHOOSE = 103;
    private static final int CROP_PIC = 104;


    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";
    private static final String KEY_LINK = "key_link";

    private static final String TAG = "MoonlightDetailFragment";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private MyRunnable myRunnable = new MyRunnable();

    @Override
    public void onBackStackChanged() {
        Log.d(TAG, "onBackStackChanged: ");
    }

    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            if (moonlight.getTitle() != null) {
                mTitle.setText(moonlight.getTitle());
            }
            if (moonlight.getContent() != null) {
                mContent.setText(moonlight.getContent());
            }
            if (moonlight.getPhotoUrl() != null) {
                BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
                bitmapUtils.display(mPhoto, moonlight.getPhotoUrl());
                mCardView.setVisibility(View.VISIBLE);
            }
            if (moonlight.getLabel() != null) {
                List<String> data;
                //从本地读取用户配置
                data = UserConfigUtils.readLabelFromUserConfig(getActivity());
                int index = data.indexOf(moonlight.getLabel());
                mLabelSpinner.setSelection(index);
            }
        }
    }

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
        BusProvider.getInstance().register(this);
        //获取用户id
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //新建DatabaseTools对象
        mDatabaseTools = new DatabaseTools(getActivity(), mUserId);
        myReference = FirebaseDatabase.getInstance().getReference();
        //新建moonlight对象
        moonlight = new Moonlight();
        //获取firebaseStorage实例
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = firebaseStorage.getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE);
        //获取系统当前时间
        long date = System.currentTimeMillis();
        moonlight.setDate(date);

        //当能argument不为空时，从argument中获取keyId，同时调用getMoonlight方法获取moonlight信息，editFlag设为true
        if (getArguments() != null) {
            mKeyId = getArguments().getString("keyId");
            getMoonlight(mKeyId);
            mEditFlag = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //新建日期对象
        Date date = new Date(moonlight.getDate());
        //标题栏设置编辑时间
        getActivity().setTitle(Utils.dateFormat(date));
        //视图初始化
        mView = inflater.inflate(R.layout.fragment_moonlight_detail, null);
        mTitle = (TextInputEditText) mView.findViewById(R.id.title_TIET);
        mContent = (TextInputEditText) mView.findViewById(R.id.content_TIET);
        mPhoto = (AppCompatImageView) mView.findViewById(R.id.moonlight_photo);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.moonlight_photo_progressBar);
        mProgressBarContainer = (LinearLayoutCompat) mView.findViewById(R.id.progressBar_container);
        mLabelSpinner = (CustomSpinner) mView.findViewById(R.id.bottomBar_label);
        AppCompatImageButton mCamera = (AppCompatImageButton) mView.findViewById(R.id.bottomBar_camera);
        AppCompatImageButton mAudio = (AppCompatImageButton) mView.findViewById(R.id.bottomBar_audio);
        mCardView = (CardView) mView.findViewById(R.id.photo_container);
        AppCompatButton mDeletePhoto = (AppCompatButton) mView.findViewById(R.id.delete_image);

        displaySpinner(mLabelSpinner);

        mCamera.setOnClickListener(this);
        mAudio.setOnClickListener(this);
        mDeletePhoto.setOnClickListener(this);
        mLabelSpinner.setOnItemSelectedListener(this);

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

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //当editFlag为true时延时0.5秒更新UI（防止UI已更新，moonlight数据未载入）
        if (mEditFlag) {
            mHandler.postDelayed(myRunnable, 500);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        //当editFlag为true且moonlight不为空时更新moonlight信息到服务器
        if (mEditFlag && moonlight != null) {
            Log.d(TAG, "mKeyId" + mKeyId);
            updateMoonlight(mKeyId, moonlight);
        }
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
        removeListener();
        //mLabel不为空时，上传用户配置到服务器
        if (mLabel != null) {
            uploadUserConfig();
        }
    }

    @Override
    public void onDestroy() {
        //取消对Bus的注册
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    /**
     * 更新显示图片信息
     * @param mFileUri 图片地址
     */
    private void updatePhoto(Uri mFileUri) {
        //当图片地址不为空时，首先从本地读取bitmap设置图片，bitmap为空，则从网络加载
        //图片地址为空则不加载图片
        if (mFileUri != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(mFileUri));
                if (bitmap != null) {
                    mPhoto.setImageBitmap(bitmap);
                } else {
                    BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
                    bitmapUtils.display(mPhoto, mDownloadUrl.toString());
                }
                mCardView.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                Log.d(TAG, "load local file failed" + e.toString());
            }
        } else {
            mFileName = null;
            mCardView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //如mEditFlag为true，加载edit_moonlight_menu，反之则加载create_moonlight_menu
        if (mEditFlag) {
            inflater.inflate(R.menu.edit_moontlight_menu, menu);
        } else {
            inflater.inflate(R.menu.create_moonlight_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                //当moonlight图片，标题，内容不为空空时，添加moonlight到服务器
                if (moonlight.getPhotoUrl() != null || moonlight.getContent() != null
                        || moonlight.getTitle() != null) {
                    addMoonlight(moonlight);
                    getActivity().startActivity(new Intent(getActivity(), MoonlightActivity.class));
                } else {
                    Utils.showToast(getContext(), "Empty! Can't add it!", 0);
                }
                break;
            case R.id.menu_delete:
                //将moonlight设置为空，删除服务器中指定的moonlight数据
                moonlight = null;
                removeMoonlight(mKeyId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示Spinner
     *
     * @param spinner spinner对象
     */
    private void displaySpinner(CustomSpinner spinner) {

        try {
            List<String> data;
            //从本地读取用户配置
            data = UserConfigUtils.readLabelFromUserConfig(getActivity());
            //如果本地读取信息不为空，则设置spinner适配器
            if (data != null) {
                mArrayAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, data);
                mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(mArrayAdapter);
                spinner.setSelection(1);
            } else {
                data = new ArrayList<String>();
                data.add("Default");
                data.add("New Label");
                UserConfigUtils.writeLabelToUserConfig(getActivity(), data);
                mArrayAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, data);
                mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Log.d(TAG, "displaySpinner: " + data.size());
                spinner.setAdapter(mArrayAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加新标签到标签列表中
     *
     * @param label 需要添加的标签
     */
    private void addNewLabelToList(@NonNull String label) {
        //从本地读取用户配置信息，当用户信息不为空时，将新标签写入用户配置中。
        List<String> data = UserConfigUtils.readLabelFromUserConfig(getActivity());
        if (data != null) {
            data.add(label);
            Log.d(TAG, "addNewLabelToList: ");
            UserConfigUtils.writeLabelToUserConfig(getActivity(), data);
            mLabel = "label";
        }
    }

    private void uploadUserConfig() {
        Uri file = Uri.fromFile(new File(getActivity().getFilesDir().getPath() + "/UserConfig.json"));
        uploadFromUri(file, mUserId, 1);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = mArrayAdapter.getItem(position);
        if (item != null) {
            if (item.equals("New Label")) {
                showLabelDialog();
            } else {
                moonlight.setLabel(item);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showLabelDialog() {
        LabelDialogFragment labelDialogFragment = new LabelDialogFragment();
        labelDialogFragment.show(getFragmentManager(), "labelDialog");
    }

    //这个注解一定要有,表示订阅了TestAction,并且方法的用 public 修饰的.方法名可以随意取,重点是参数,它是根据你的参数进行判断
    @Subscribe
    public void busAction(BusAction busAction) {
        //这里更新视图或者后台操作,从TestAction获取传递参数.
        if (busAction.getString() != null) {
            //
            mArrayAdapter = null;
            addNewLabelToList(busAction.getString());
            displaySpinner(mLabelSpinner);
        }

        if (busAction.getInt() != 0) {
            switch (busAction.getInt()) {
                case 3:
                    onCameraClick();
                    break;
                case 2:
                    onAlbumClick();
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottomBar_camera:
                //使用PopupMenu选择使用相机还是相册添加图片
                MenuUtils.showPopupMenu(getActivity(), v, R.menu.photo_choose_menu, new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_camera:
                                onCameraClick();
                                Toast.makeText(getActivity(), "camera chose", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.popup_album:
                                onAlbumClick();
                                Toast.makeText(getActivity(), "album chose", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                break;
            case R.id.bottomBar_audio:
                break;
            case R.id.moonlight_photo:
                //网页浏览图片。。。
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(moonlight.getPhotoUrl()));
                startActivity(intent);
                break;
            case R.id.delete_image:
                //
                removePhoto();
                moonlight.setPhotoUrl(null);
                mCardView.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult: ");
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "take Picture");
                    uploadFromUri(mFileUri, mUserId, 0);
                }
                break;
            case ALBUM_CHOOSE:
                Log.d(TAG, "album choose");
                if (resultCode == RESULT_OK && data.getData() != null) {
                    Uri fileUri = data.getData();
                    uploadFromUri(fileUri, mUserId, 0);
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
            EasyPermissions.requestPermissions(getActivity(), "If you want to do this continue, " +
                            "you should give App storage permission ",
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

        mFileUri = FileProvider.getUriForFile(getActivity(), Constants.FILE_PROVIDER, file);
        Log.i(TAG, "file: " + mFileUri);
        // Create and launch the intent
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        if (takePicIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePicIntent, TAKE_PICTURE);
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
            EasyPermissions.requestPermissions(getActivity(), "If you want to do this continue, " +
                            "you should give App storage permission ",
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

        Uri fileUri = FileProvider.getUriForFile(getActivity(), Constants.FILE_PROVIDER, file);
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setType("image/*");
        albumIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        if (albumIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(albumIntent, ALBUM_CHOOSE);
        } else {
            SnackBarUtils.longSnackBar(mView, "No Album!", SnackBarUtils.TYPE_WARNING).show();
        }
    }

    private void uploadFromUri(final Uri fileUri, String userId, final int type) {

        mProgressBarContainer.setVisibility(View.VISIBLE);

        StorageTask<UploadTask.TaskSnapshot> uploadTask = null;

        if (type == 0) {
            // Get a reference to store file at photos/<FILENAME>.jpg
            StorageReference photoRef = mStorageReference.child(userId).child("photos")
                    .child(fileUri.getLastPathSegment());
            Log.d(TAG, "uploadFromUri: " + fileUri.getLastPathSegment());
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
                    if (type == 0) {
                        mFileName = taskSnapshot.getMetadata().getName();
                    }
                    Log.d(TAG, "onSuccess: downloadUrl:  " + mDownloadUrl.toString());
                    moonlight.setPhotoName(mFileName);
                    moonlight.setPhotoUrl(mDownloadUrl.toString());
                    mProgressBarContainer.setVisibility(View.GONE);
                    updatePhoto(fileUri);
                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mFileName = null;
                    mDownloadUrl = null;
                    mProgressBarContainer.setVisibility(View.GONE);
                    updatePhoto(fileUri);
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onPaused: ");
                    mProgressBarContainer.setVisibility(View.GONE);
                    SnackBarUtils.shortSnackBar(mView, "upload paused", SnackBarUtils.TYPE_INFO).show();
                }
            });
        } else {
            Log.w(TAG, "uploadFromUri: failed");
        }

    }

    private void addMoonlight(final Moonlight moonlight) {
        // [START single_value_read]

        myReference.child(mUserId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        updateMoonlight(null, moonlight);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void updateMoonlight(@Nullable String keyId, Moonlight moonlight) {
        String mKey;
        if (keyId == null) {
            mKey = myReference.child("moonlight").push().getKey();
        } else {
            mKey = keyId;
        }
        moonlight.setId(mKey);
        Map<String, Object> moonlightValues = moonlight.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users-moonlight/" + mUserId + "/note/" + mKey, moonlightValues);


        myReference.updateChildren(childUpdates);
    }

    /**
     * 从firebase的database获取moonlight
     *
     * @param keyId 当前读取moonlight的keyId
     */
    private void getMoonlight(String keyId) {
        mMoonlightRef = FirebaseDatabase.getInstance().getReference()
                .child("users-moonlight").child(mUserId).child("note").child(keyId);
        moonlightListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        moonlight = dataSnapshot.getValue(Moonlight.class);

                    }

                    Log.d(TAG, "moonlight.getTitle: " + moonlight.getTitle());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadMoonlight:onCancelled", databaseError.toException());
                SnackBarUtils.shortSnackBar(mView, "Failed to load moonlight.", SnackBarUtils.TYPE_WARNING).show();

            }
        };
        mMoonlightRef.addValueEventListener(moonlightListener);
    }

    private void removePhoto() {
        StorageReference photoRef = mStorageReference.child(mUserId).child("photos")
                .child(mFileName);
        Log.d(TAG, "onClick: " + mFileName);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");
                SnackBarUtils.shortSnackBar(mView, "Image removed!", SnackBarUtils.TYPE_INFO).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: " + e.toString());
            }
        });
    }

    private void removeListener() {
        // Remove mMoonlight value event listener
        if (mMoonlightListener != null) {
            mMoonlightRef.removeEventListener(mMoonlightListener);
        }
    }

    public void removeMoonlight(String keyId) {
        try {
            FirebaseDatabase.getInstance().getReference().child("users-moonlight")
                    .child(mUserId).child("note").child(keyId).removeValue(
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Utils.showToast(getContext(), "Delete completed!", 0);
                            getActivity().startActivity(new Intent(getActivity(), MoonlightActivity.class));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
