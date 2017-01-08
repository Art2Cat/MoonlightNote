package com.art2cat.dev.moonlightnote.CustomView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;

/**
 * Created by Rorschach
 * on 2017/1/8 14:32.
 */

public class BaseFragment extends Fragment {
    public Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    public void showShortSnackbar(View view, String content) {
        SnackBarUtils.shortSnackBar(view, content, SnackBarUtils.TYPE_INFO).show();
    }

    public void showLongSnackbar(View view, String content) {
        SnackBarUtils.longSnackBar(view, content, SnackBarUtils.TYPE_INFO).show();
    }

    public void showToast(String content) {
        Toast.makeText(mActivity, content, Toast.LENGTH_SHORT).show();
    }
}
