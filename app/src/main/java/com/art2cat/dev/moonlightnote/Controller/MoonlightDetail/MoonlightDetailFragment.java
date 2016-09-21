package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.art2cat.dev.moonlightnote.Firebase.DbTools;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.Model.UserConfig;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusAction;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusProvider;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Subscribe;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
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
    private AppCompatSpinner mLabelSpinner;
    private AppCompatSpinner mColor;

    private Moonlight moonlight;
    private DbTools mDbTools;
    private String userId;

    private String mLabel;
    private List<String> mLabelList = new ArrayList<>();


    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private static final String TAG = "MoonlightDetailFragment";

    public MoonlightDetailFragment() {
        // Required empty public constructor
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        userId = SPUtils.getString(getActivity(), "User", "Id", null);
        mDbTools = new DbTools(getActivity(), userId);
        moonlight = new Moonlight();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE);
        long date = System.currentTimeMillis();
        moonlight.setDate(date);
        moonlight.setLabel("Default");
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
        mLabelSpinner = (AppCompatSpinner) mView.findViewById(R.id.bottomBar_label);
        mColor = (AppCompatSpinner) mView.findViewById(R.id.bottomBar_color);

        displaySpinner(mLabelSpinner, 1);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_moonlight_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
        mDbTools.addMoonlight(moonlight);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        mDbTools.removeListener();
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    private void displaySpinner(AppCompatSpinner spinner, int type) {
        if (type == 1) {
            UserConfig userConfig = readUserConfig();
            if (userConfig != null) {
                List<String> data = userConfig.getLabels();
                data.add("New Label");
                mLabelList = data;
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, mLabelList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(arrayAdapter);
            } else {
                List<String> data = new ArrayList<String>();
                data.add("Default");
                data.add("New Label");
                mLabelList = data;
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, mLabelList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Log.d(TAG, "displaySpinner: " + data.size());
                spinner.setAdapter(arrayAdapter);
            }

        }
    }

    public List<String> getLabel() {
        List<String> label = new ArrayList<>();
        return label;
    }

    private void addNewLabelToList(String label) {
        if (label != null) {
            mLabelList.remove(mLabelList.size() - 1);
            mLabelList.add(label);
            writeUserConfig(mLabelList);
        }
    }


    public void writeUserConfig(List<String> labelList) {
        UserConfig userConfig = new UserConfig();
        userConfig.setLabels(labelList);
        //获取userConfig文件目录
        String userConfigFile = getActivity().getDir("userConfig", Context.MODE_PRIVATE).getPath();

        try (Writer writer = new FileWriter(userConfigFile + "/UserConfig.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(userConfig, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private UserConfig readUserConfig() {
        String userConfigFile = getActivity().getDir("userConfig", Context.MODE_PRIVATE).getPath();

        try (Reader reader = new FileReader(userConfigFile + "/UserConfig.json")) {
            Gson gson = new Gson();
            //从Gson文件中解析UserConfig类
            UserConfig userConfig = gson.fromJson(reader, UserConfig.class);

            return userConfig;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uploadUserConfig() {
        StorageReference userConfigRef = mStorageReference.child("userConfig");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mLabelList.size() - 1 != position) {
            String label = mLabelList.get(position);
            moonlight.setLabel(label);
        } else {
            showLabelDialog();
            //addNewLabelToList(getActivity().getIntent().getStringExtra("label"));
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
            addNewLabelToList(busAction.getString());
        }
    }

}
