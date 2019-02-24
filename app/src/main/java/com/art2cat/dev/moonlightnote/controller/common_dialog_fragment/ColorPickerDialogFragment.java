package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.constants.ColorConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by rorschach.h on 12/9/16 11:10 PM.
 */

public class ColorPickerDialogFragment
    extends DialogFragment {

  private static final String TAG = ColorPickerDialogFragment.class.getName();
  private ImageButton one;
  private ImageButton two;
  private ImageButton three;
  private ImageButton four;
  private ImageButton five;
  private ImageButton six;
  private ImageButton seven;
  private ImageButton eight;
  private ImageButton nine;
  private ImageButton ten;
  private ImageButton eleven;
  private ImageButton twelve;
  private ImageButton thirteen;
  private ImageButton fourteen;
  private ImageButton fifteen;
  private ImageButton sixteen;
  private ImageButton seventeen;
  private ImageButton eighteen;
  private ImageButton nineteen;
  private Button twentyOne;

  private List<Integer> colors;
  private List<ImageButton> buttons;

  private ColorListener colorListener;
  private final View.OnClickListener listener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      if (Objects.nonNull(colorListener)) {
        colorListener.onColorClick(v, (int) v.getTag());
      }
      dismiss();
    }
  };

  public ColorPickerDialogFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    colors = new ArrayList<>();
    colors.add(ColorConstants.RED);
    colors.add(ColorConstants.PINK);
    colors.add(ColorConstants.PURPLE);
    colors.add(ColorConstants.DEEP_PURPLE);
    colors.add(ColorConstants.INDIGO);
    colors.add(ColorConstants.BLUE);
    colors.add(ColorConstants.LIGHT_BLUE);
    colors.add(ColorConstants.CYAN);
    colors.add(ColorConstants.TEAL);
    colors.add(ColorConstants.GREEN);
    colors.add(ColorConstants.LIGHT_GREEN);
    colors.add(ColorConstants.LIME);
    colors.add(ColorConstants.YELLOW);
    colors.add(ColorConstants.AMBER);
    colors.add(ColorConstants.ORANGE);
    colors.add(ColorConstants.DEEP_ORANGE);
    colors.add(ColorConstants.BROWN);
    colors.add(ColorConstants.GREY);
    colors.add(ColorConstants.BLUE_GRAY);
    colors.add(0xff000000);
    colors.add(0xffffffff);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_color_picker, null);
    one = view.findViewById(R.id.b1);
    two = view.findViewById(R.id.b2);
    three = view.findViewById(R.id.b3);
    four = view.findViewById(R.id.b4);
    five = view.findViewById(R.id.b5);
    six = view.findViewById(R.id.b6);
    seven = view.findViewById(R.id.b7);
    eight = view.findViewById(R.id.b8);
    nine = view.findViewById(R.id.b9);
    ten = view.findViewById(R.id.b10);
    eleven = view.findViewById(R.id.b11);
    twelve = view.findViewById(R.id.b12);
    thirteen = view.findViewById(R.id.b13);
    fourteen = view.findViewById(R.id.b14);
    fifteen = view.findViewById(R.id.b15);
    sixteen = view.findViewById(R.id.b16);
    seventeen = view.findViewById(R.id.b17);
    eighteen = view.findViewById(R.id.b18);
    nineteen = view.findViewById(R.id.b19);
    twentyOne = view.findViewById(R.id.b21);

    buttons = new ArrayList<>();
    buttons.add(one);
    buttons.add(two);
    buttons.add(three);
    buttons.add(four);
    buttons.add(five);
    buttons.add(six);
    buttons.add(seven);
    buttons.add(eight);
    buttons.add(nine);
    buttons.add(ten);
    buttons.add(eleven);
    buttons.add(twelve);
    buttons.add(thirteen);
    buttons.add(fourteen);
    buttons.add(fifteen);
    buttons.add(sixteen);
    buttons.add(seventeen);
    buttons.add(eighteen);
    buttons.add(nineteen);

    colorize();

    twentyOne.setVisibility(View.INVISIBLE);

    setListeners();
    builder.setView(view);
    builder.setTitle(R.string.select_your_color);
    return builder.create();
  }

  private void setListeners() {
    for (int i = 0; i < buttons.size(); i++) {
      buttons.get(i).setTag(colors.get(i));
      buttons.get(i).setOnClickListener(listener);
    }
    twentyOne.setTag(colors.get(20));
    twentyOne.setOnClickListener(listener);
  }

  private void colorize() {
    for (int i = 0; i < buttons.size(); i++) {
      ShapeDrawable d = new ShapeDrawable(new OvalShape());
      d.setBounds(58, 58, 58, 58);
      if (BuildConfig.DEBUG) {
        Log.i(TAG, "Shape drown no " + i);
      }
      buttons.get(i).setVisibility(View.INVISIBLE);

      d.getPaint().setStyle(Paint.Style.FILL);
      d.getPaint().setColor(colors.get(i));

      buttons.get(i).setBackground(d);
    }
    animate();
  }

  private void animate() {
    Log.i(TAG, "animate true");
    Runnable r1 = () -> animator(one);

    Runnable r2 = () -> {
      animator(two);
      animator(five);
    };

    Runnable r3 = () -> {
      animator(three);
      animator(six);
      animator(nine);
    };

    Runnable r4 = () -> {
      animator(four);
      animator(seven);
      animator(ten);
      animator(thirteen);
    };

    Runnable r5 = () -> {
      animator(eight);
      animator(eleven);
      animator(fourteen);
      animator(seventeen);
    };

    Runnable r6 = () -> {
      animator(twelve);
      animator(fifteen);
      animator(eighteen);
    };

    Runnable r7 = () -> {
      animator(sixteen);
      animator(nineteen);
    };

    Runnable r8 = () -> {
      Animation animation =
          AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
      animation.setInterpolator(new AccelerateInterpolator());
      twentyOne.setAnimation(animation);
      twentyOne.setVisibility(View.VISIBLE);
      animation.start();
    };

    final Handler handler = new Handler();
    int counter = 85;
    handler.postDelayed(r1, counter);
    handler.postDelayed(r2, counter * 2);
    handler.postDelayed(r3, counter * 3);
    handler.postDelayed(r4, counter * 4);
    handler.postDelayed(r5, counter * 5);
    handler.postDelayed(r6, counter * 6);
    handler.postDelayed(r7, counter * 7);
    handler.postDelayed(r8, counter * 8);
  }

  private void animator(final ImageButton imageButton) {
    Animation animation = AnimationUtils
        .loadAnimation(getContext(), R.anim.color_item);
    animation.setInterpolator(new AccelerateInterpolator());
    imageButton.setAnimation(animation);
    imageButton.setVisibility(View.VISIBLE);
    animation.start();
  }

  public void setColorListener(ColorListener listener) {
    this.colorListener = listener;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    colors.clear();
    colors = null;
    for (ImageButton imageButton : buttons) {
      // help gc
      imageButton = null;
    }
    buttons.clear();
    buttons = null;
  }
}
