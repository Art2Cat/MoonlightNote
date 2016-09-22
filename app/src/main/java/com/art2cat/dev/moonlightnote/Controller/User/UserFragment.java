package com.art2cat.dev.moonlightnote.Controller.User;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusAction;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusProvider;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.otto.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private CircleImageView mCircleImageView;
    private TextInputEditText mNickname;
    private TextInputEditText mEmail;
    private AppCompatButton mChangePassword;
    private User mUser;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取Bus单例，并注册
        BusProvider.getInstance().register(this);
        //获取FirebaseUser对象
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUser = Utils.getUserInfo(user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_user, container, false);
        mCircleImageView = (CircleImageView) mView.findViewById(R.id.user_head_picture);
        mNickname = (TextInputEditText) mView.findViewById(R.id.user_nickname);
        mEmail = (TextInputEditText) mView.findViewById(R.id.user_email);
        mChangePassword = (AppCompatButton) mView.findViewById(R.id.user_change_password);

        initView();
        mCircleImageView.setOnClickListener(this);
        mChangePassword.setOnClickListener(this);
        mNickname.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    private void initView(){
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
        if (email!= null) {
            mEmail.setText(email);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_picture:
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
        }

    }
}
