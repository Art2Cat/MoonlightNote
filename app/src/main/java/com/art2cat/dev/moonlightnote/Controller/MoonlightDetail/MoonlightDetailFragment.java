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
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.art2cat.dev.moonlightnote.Firebase.DatabaseTools;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusAction;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusProvider;
import com.art2cat.dev.moonlightnote.Utils.CustomSpinner;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoonlightDetailFragment extends Fragment implements AdapterView.OnItemSelectedListener
        , View.OnClickListener {
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
    private ArrayAdapter<String> mArrayAdapter;

    private Moonlight moonlight;
    private DatabaseTools mDatabaseTools;
    private boolean flag;
    private String userId;
    private String keyid;
    private String mLabel;
    private Uri mFileUri = null;
    private DatabaseReference moonlightReference;
    private ValueEventListener moonlightListener;
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

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private MyRunnable myRunnable = new MyRunnable();

    private class MyRunnable implements Runnable{

        @Override
        public void run() {
            if (moonlight.getTitle()!= null) {
                mTitle.setText(moonlight.getTitle());
            }
            if (moonlight.getContent() != null) {
                mContent.setText(moonlight.getContent());
            }
            if (moonlight.getPhoto()!= null) {
                BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
                bitmapUtils.display(mPhoto,moonlight.getPhoto());
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

    public static MoonlightDetailFragment newInstance() {
        MoonlightDetailFragment moonlightDetailFragment = new MoonlightDetailFragment();
        return moonlightDetailFragment;
    }
    
    public static MoonlightDetailFragment newInstance(String keyid) {
        MoonlightDetailFragment moonlightDetailFragment = new MoonlightDetailFragment();
        Bundle args = new Bundle();
        args.putString("keyId", keyid);
        moonlightDetailFragment.setArguments(args);
        return moonlightDetailFragment;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取Bus单例，并注册
        BusProvider.getInstance().register(this);
        //获取用户id
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //新建DatabaseTools对象
        mDatabaseTools = new DatabaseTools(getActivity(), userId);
        //新建moonlight对象
        moonlight = new Moonlight();
        //获取firebaseStorage实例
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE);
        //获取系统当前时间
        long date = System.currentTimeMillis();
        moonlight.setDate(date);
        
        if (getArguments() != null) {
            keyid = getArguments().getString("keyId");
            getMoonlight(keyid);
            flag = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Date date = new Date(System.currentTimeMillis());
        getActivity().setTitle(Utils.dateFormat(date));
        mView = inflater.inflate(R.layout.fragment_moonlight_detail, null);
        mTitle = (TextInputEditText) mView.findViewById(R.id.title_TIET);
        mContent = (TextInputEditText) mView.findViewById(R.id.content_TIET);
        mPhoto = (AppCompatImageView) mView.findViewById(R.id.moonlight_photo);


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
        if (flag) {
            mHandler.postDelayed(myRunnable, 500);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if (flag) {
            Log.d(TAG, "keyid" + keyid);
            mDatabaseTools.updateMoonlight(keyid, moonlight);
        } else {
            if (moonlight.getPhoto() != null || moonlight.getContent() != null
                    || moonlight.getTitle() != null) {
                mDatabaseTools.addMoonlight(moonlight);
            }
        }
        if (mLabel != null) {
            uploadUserConfig();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        mDatabaseTools.removeListener();
        if (moonlightListener != null) {
            moonlightReference.removeEventListener(moonlightListener);
        }
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    private void updateUI() {
        if (mDownloadUrl != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(mFileUri));
                mPhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
            bitmapUtils.display(mPhoto, mDownloadUrl.toString());
            mCardView.setVisibility(View.VISIBLE);

        } else {
            mFileName = null;
            mCardView.setVisibility(View.GONE);
        }
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
        uploadFromUri(file, userId, 1);
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
                PickPicFragment pickPicFragment = new PickPicFragment();
                pickPicFragment.show(getFragmentManager(), "PICK_PIC");
                break;
            case R.id.bottomBar_audio:
                break;
            case R.id.moonlight_photo:
                //网页浏览图片。。。
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(moonlight.getPhoto()));
                startActivity(intent);
                break;
            case R.id.delete_image:
                StorageReference photoRef = mStorageReference.child(userId).child("photos")
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
                        Log.w(TAG, "onFailure: " + e.toString() );
                    }
                });
                moonlight.setPhoto(null);
                mCardView.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK && mFileUri != null) {
                    Log.d(TAG, "mFileUri:" + mFileUri);
                    uploadFromUri(data.getData(), userId, 0);
                }
                break;
            case ALBUM_CHOOSE:
                if (resultCode == RESULT_OK && mFileUri != null) {
                    Log.d(TAG, "mFileUri:" + mFileUri);
                    uploadFromUri(data.getData(), userId, 0);
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

        if (type ==0) {
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
                    mFileName = taskSnapshot.getMetadata().getName();
                    Log.d(TAG, "onSuccess: downloadUrl:  " + mDownloadUrl.toString());
                    progressDialog.dismiss();
                    moonlight.setPhoto(mDownloadUrl.toString());
                    updateUI();
                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    mFileName = null;
                    mDownloadUrl = null;
                    updateUI();
                }
            });
        } else {
            Log.w(TAG, "uploadFromUri: failed" );
        }

    }

    /**
     * 从firebase的database获取moonlight
     *
     * @param keyId 当前读取moonlight的keyId
     * @return 返回从firebase中获取的moonlight
     */
    public void getMoonlight(String keyId) {
        moonlightReference = FirebaseDatabase.getInstance().getReference()
                .child("users-moonlight").child(userId).child("note").child(keyId);
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
        moonlightReference.addValueEventListener(moonlightListener);
    }
}
