package com.art2cat.dev.moonlightnote.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * 自定义Spinner
 * Created by art2cat
 * on 9/7/16.
 */
public class CustomSpinner extends Spinner {
    OnItemSelectedListener listener;
    private AdapterView<?> lastParent;
    private View lastView;
    private long lastId;

    //继承Spinner构造方法
    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialListener();
    }

    @Override
    public void setSelection(int position) {
        //当选择的位置匹配且监听器不为null
        if (position == getSelectedItemPosition() && listener != null) {
            listener.onItemSelected(lastParent, lastView, position, lastId);
        } else {
            super.setSelection(position);
        }

    }

    /**
     * 初始化监听器
     */
    private void initialListener() {
        super.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                lastParent = parent;
                lastView = view;
                lastId = id;
                if (listener != null) {
                    listener.onItemSelected(parent, view, position, id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (listener != null) {
                    listener.onNothingSelected(parent);
                }
            }
        });

    }

    public void setOnItemSelectedEvenIfUnchangedListener(
            OnItemSelectedListener listener) {
        this.listener = listener;
    }
}
