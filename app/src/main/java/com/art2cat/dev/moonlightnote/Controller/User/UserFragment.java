package com.art2cat.dev.moonlightnote.Controller.User;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements View.OnClickListener{
    private View mView;
    private CircleImageView mCircleImageView;
    private TextInputEditText mNickname;
    private TextInputEditText mEmail;
    private AppCompatButton mChangePassword;
    private FirebaseUser mUser;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String photoUri = mUser.getPhotoUrl().toString();
        if (photoUri != null) {
            BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
            bitmapUtils.display(mCircleImageView, photoUri);
        }

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_picture:
                break;
            case R.id.user_nickname:
                break;
            case R.id.user_change_password:
                break;
        }
    }
}
