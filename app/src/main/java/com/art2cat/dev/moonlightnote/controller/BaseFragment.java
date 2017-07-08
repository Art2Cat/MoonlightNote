package com.art2cat.dev.moonlightnote.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.model.Moonlight;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;

import java.util.ArrayList;

/**
 * Created by Rorschach
 * on 2017/1/8 14:32.
 */

public abstract class BaseFragment extends Fragment {
    private static final String KEY_INDEX = "index";
    protected Activity mActivity;
    private int mCurrentIndex = 0;

    /**
     * 更改toolbar三个点颜色
     *
     * @param activity Activity
     * @param color    颜色
     */
    public static void setOverflowButtonColor(Activity activity, final int color) {
        @SuppressLint("PrivateResource")
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<>();
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
        v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    public void showShortSnackBar(View view, String content, int type) {
        SnackBarUtils.shortSnackBar(view, content, type).show();
    }

    public void showLongSnackBar(View view, String content, int type) {
        SnackBarUtils.longSnackBar(view, content, type).show();
    }

    public void showShortToast(String content) {
        Toast.makeText(MoonlightApplication.getContext(), content, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String content) {
        Toast.makeText(MoonlightApplication.getContext(), content, Toast.LENGTH_LONG).show();
    }

    public BaseFragment setArgs(Moonlight moonlight, int flag) {
        Bundle args = new Bundle();
        args.putParcelable("moonlight", moonlight);
        args.putInt("flag", flag);
        this.setArguments(args);
        return this;
    }

    public interface DrawerLocker {
        void setDrawerEnabled(boolean enabled);
    }
}
