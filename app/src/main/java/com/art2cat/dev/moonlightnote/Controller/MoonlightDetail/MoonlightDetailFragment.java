package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
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
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.UserConfigUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoonlightDetailFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private View mView;
    private TextInputLayout mTitleTextInputLayout;
    private TextInputLayout mContentTextInputLayout;
    private TextInputEditText mTitle;
    private TextInputEditText mContent;
    private AppCompatTextView mDate;
    private AppCompatImageButton mCamera;
    private AppCompatImageButton mAudio;
    private CustomSpinner mLabelSpinner;
    private AppCompatSpinner mColor;
    private ArrayAdapter<String> mArrayAdapter;

    private Moonlight moonlight;
    private DatabaseTools mDatabaseTools;
    private String userId;

    private String mLabel;


    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private static final int SPINNER_TYPE_LABEL = 1;
    private static final int SPINNER_TYPE_COLOR = 2;
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
        userId = SPUtils.getString(getActivity(), "User", "Id", null);
        mDatabaseTools = new DatabaseTools(getActivity(), userId);
        moonlight = new Moonlight();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE);
        long date = System.currentTimeMillis();
        moonlight.setDate(date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_moonlight_detail, null);
        mTitle = (TextInputEditText) mView.findViewById(R.id.title_TIET);
        mContent = (TextInputEditText) mView.findViewById(R.id.content_TIET);

        mDate = (AppCompatTextView) mView.findViewById(R.id.bottomBar_date);
        Date date = new Date(System.currentTimeMillis());
        mDate.setText(Utils.dateFormat(date));
        mLabelSpinner = (CustomSpinner) mView.findViewById(R.id.bottomBar_label);
        mColor = (AppCompatSpinner) mView.findViewById(R.id.bottomBar_color);

        displaySpinner(mLabelSpinner, SPINNER_TYPE_LABEL);

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

    private void initToolbar(){

    }

    private void displaySpinner(CustomSpinner spinner, int type) {
        switch (type) {
            case 1:
                try {
                    List<String> data;
                    data = UserConfigUtils.readLabelFromUserConfig(getActivity());
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
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                break;
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
        } else {
            Log.d(TAG, "addNewLabelToList: " + data);
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
            displaySpinner(mLabelSpinner, SPINNER_TYPE_LABEL);
        }
    }

}
