package com.art2cat.dev.moonlightnote.CustomView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Rorschach
 * on 2017/1/8 14:32.
 */

public class BaseFragment extends Fragment {
    public Activity mActivity;

    /**
     * 更改toolbar三个点颜色
     *
     * @param activity Activity
     * @param color    颜色
     */
    public static void setOverflowButtonColor(Activity activity, final int color) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<View>();
                decorView.findViewsWithText(outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }
                AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
                overflow.setColorFilter(color);
                removeOnGlobalLayoutListener(decorView, this);
            }
        });
    }

    /**
     * 移除布局监听器
     *
     * @param v        view
     * @param listener 监听器
     */
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

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
