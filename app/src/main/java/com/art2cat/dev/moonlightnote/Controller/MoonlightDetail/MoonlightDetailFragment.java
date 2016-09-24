package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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
    private AppCompatImageButton mCamera;
    private AppCompatImageButton mAudio;
    private CardView mCardView;
    private AppCompatImageView mPhoto;
    private CustomSpinner mLabelSpinner;
    private AppCompatSpinner mColor;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> mArrayAdapter;

    private Moonlight moonlight;
    private DatabaseTools mDatabaseTools;
    private String userId;

    private String mLabel;
    private Uri mFileUri = null;

    private StorageReference mStorageRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private long mValue;
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

    public MoonlightDetailFragment() {
        // Required empty public constructor
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
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE);
        //获取系统当前时间
        long date = System.currentTimeMillis();
        moonlight.setDate(date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_moonlight_detail, null);
        mTitle = (TextInputEditText) mView.findViewById(R.id.title_TIET);
        mContent = (TextInputEditText) mView.findViewById(R.id.content_TIET);
        mPhoto = (AppCompatImageView) mView.findViewById(R.id.moonlight_photo);

        mDate = (AppCompatTextView) mView.findViewById(R.id.bottomBar_date);
        Date date = new Date(System.currentTimeMillis());
        mDate.setText(Utils.dateFormat(date));
        mLabelSpinner = (CustomSpinner) mView.findViewById(R.id.bottomBar_label);
        mColor = (AppCompatSpinner) mView.findViewById(R.id.bottomBar_color);
        mCamera = (AppCompatImageButton) mView.findViewById(R.id.bottomBar_camera);
        mAudio = (AppCompatImageButton) mView.findViewById(R.id.bottomBar_audio);
        mCardView = (CardView) mView.findViewById(R.id.photo_container);

        displaySpinner(mLabelSpinner);

        mCamera.setOnClickListener(this);
        mAudio.setOnClickListener(this);
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
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
        mDatabaseTools.addMoonlight(moonlight);
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

    public List<String> getLabel() {
        List<String> label = new ArrayList<>();
        return label;
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
        }
    }

    private void uploadUserConfig() {
        StorageReference userConfigRef = mStorageReference.child("userConfig");
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
                case 1:
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
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK && mFileUri != null) {
                    Log.d(TAG, "mFileUri:" + mFileUri);
                    //new UploadFile().execute(data.getData().toString(), userId);
                    uploadFromUri(data.getData(), userId);
                }
                break;
            case ALBUM_CHOOSE:
                if (resultCode == RESULT_OK && mFileUri != null) {
                    Log.d(TAG, "mFileUri:" + mFileUri);

                    //new UploadFile().execute(data.getData().toString(), userId);
                    uploadFromUri(data.getData(), userId);

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

    public class UploadFile extends AsyncTask<String, Long, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            Uri uri = Uri.parse(params[0]);
            uploadFromUri(uri, params[1]);
            publishProgress(mValue);

            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            int value = values[0].intValue();
            Log.d(TAG, "onProgressUpdate: " + value);
            progressDialog.setProgress(value);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            updateUI();
        }
    }

    private void uploadFromUri(Uri fileUri, String userId) {
        // Get a reference to store file at photos/<FILENAME>.jpg
        StorageReference photoRef = mStorageReference.child(userId).child("photos")
                .child(fileUri.getLastPathSegment());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        // Upload file to Firebase Storage
        StorageTask<UploadTask.TaskSnapshot> uploadTask = photoRef.putFile(fileUri);

        uploadTask.addOnProgressListener(getActivity(), new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setProgress((int) progress);
            }
        }).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mDownloadUrl = taskSnapshot.getDownloadUrl();
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
    }
}
