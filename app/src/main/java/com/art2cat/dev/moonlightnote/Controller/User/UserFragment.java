package com.art2cat.dev.moonlightnote.Controller.User;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
    private View mView;
    private CircleImageView mCircleImageView;
    private FirebaseUser mUser;


    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_user, container, false);
        mCircleImageView = (CircleImageView) mView.findViewById(R.id.user_head_picture);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String photoUri = mUser.getPhotoUrl().toString();
        if (photoUri != null) {
            BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
            bitmapUtils.display(mCircleImageView, photoUri);
        }
        return mView;
    }

}
