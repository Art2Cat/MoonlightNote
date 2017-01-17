package com.art2cat.dev.moonlightnote.CustomView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.ToastUtils;

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

    public void showShortSnackBar(View view, String content, int type) {
        SnackBarUtils.shortSnackBar(view, content, type).show();
    }

    public void showLongSnackBar(View view, String content, int type) {
        SnackBarUtils.longSnackBar(view, content, type).show();
    }

    public void showShortToast(String content) {
        ToastUtils.with(mActivity).setMessage(content).showShortToast();
    }

    public void showLongToast(String content) {
        ToastUtils.with(mActivity).setMessage(content).showLongToast();
    }

    public BaseFragment setArgs(Moonlight moonlight, int flag) {
        Bundle args = new Bundle();
        args.putParcelable("moonlight", moonlight);
        args.putInt("flag", flag);
        this.setArguments(args);
        return this;
    }

    public interface DrawerLocker {
        public void setDrawerEnabled(boolean enabled);
    }
}
