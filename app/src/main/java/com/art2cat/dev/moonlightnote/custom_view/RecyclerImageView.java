package com.art2cat.dev.moonlightnote.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by Rorschach on 2017/1/22 上午11:37.
 */

public class RecyclerImageView extends PhotoView {

  public RecyclerImageView(Context context) {
    super(context);
  }

  public RecyclerImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RecyclerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    setImageDrawable(null);
  }
}
