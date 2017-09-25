package com.art2cat.dev.moonlightnote.controller.moonlight;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import com.art2cat.dev.moonlightnote.R;
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rorschach
 * on 12/9/16 11:10 PM.
 */

class MyColorPickerDialog extends ColorChooserDialog {

    //CONSTANTS
    //    public final int Black =      0xff000000;
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
    private ColorListener myColorListener;
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (myColorListener != null)
                myColorListener.OnColorClick(v, (int) v.getTag());
            dismiss();
        }
    };

    MyColorPickerDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        View view = LayoutInflater.from(getContext())
//                .inflate(com.turkialkhateeb.materialcolorpicker.R.layout.color_picker_dialog, null);
        setContentView(R.layout.dialog_color_picker);
//        int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.padding);
//        view.setPadding(padding, padding, padding, 0);

        one = findViewById(R.id.b1);
        two = findViewById(R.id.b2);
        three = findViewById(R.id.b3);
        four = findViewById(R.id.b4);
        five = findViewById(R.id.b5);
        six = findViewById(R.id.b6);
        seven = findViewById(R.id.b7);
        eight = findViewById(R.id.b8);
        nine = findViewById(R.id.b9);
        ten = findViewById(R.id.b10);
        eleven = findViewById(R.id.b11);
        twelve = findViewById(R.id.b12);
        thirteen = findViewById(R.id.b13);
        fourteen = findViewById(R.id.b14);
        fifteen = findViewById(R.id.b15);
        sixteen = findViewById(R.id.b16);
        seventeen = findViewById(R.id.b17);
        eighteen = findViewById(R.id.b18);
        nineteen = findViewById(R.id.b19);
        ImageButton twenty = findViewById(R.id.b20);
        twentyOne = findViewById(R.id.b21);


        colors = new ArrayList<>();
        colors.add(red);
        colors.add(pink);
        colors.add(Purple);
        colors.add(DeepPurple);
        colors.add(Indigo);
        colors.add(Blue);
        colors.add(LightBlue);
        colors.add(Cyan);
        colors.add(Teal);
        colors.add(Green);
        colors.add(LightGreen);
        colors.add(Lime);
        colors.add(Yellow);
        colors.add(Amber);
        colors.add(Orange);
        colors.add(DeepOrange);
        colors.add(Brown);
        colors.add(Grey);
        colors.add(BlueGray);
//        colors.add(Black);
        colors.add(White);

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
//        buttons.add(twenty);


        Colorize();

        twenty.setVisibility(View.GONE);
        twentyOne.setVisibility(View.INVISIBLE);

        setListeners();
    }

    private void setListeners() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setTag(colors.get(i));
            buttons.get(i).setOnClickListener(listener);
        }
        twentyOne.setTag(colors.get(19));
        twentyOne.setOnClickListener(listener);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void Colorize() {
        for (int i = 0; i < buttons.size(); i++) {
            ShapeDrawable d = new ShapeDrawable(new OvalShape());
            d.setBounds(58, 58, 58, 58);
            buttons.get(i).setVisibility(View.INVISIBLE);

            d.getPaint().setStyle(Paint.Style.FILL);
            d.getPaint().setColor(colors.get(i));

            buttons.get(i).setBackground(d);
        }
        animate();

    }


    private void animate() {

        Runnable r1 = () -> {
            animator(one);
        };

        Runnable r2 = () -> {
            animator(two);
            animator(six);
        };

        Runnable r3 = () -> {
            animator(three);
            animator(seven);
            animator(eleven);
        };

        Runnable r4 = () -> {
            animator(four);
            animator(eight);
            animator(twelve);
            animator(sixteen);
        };

        Runnable r5 = () -> {
            animator(five);
            animator(nine);
            animator(thirteen);
            animator(seventeen);
        };

        Runnable r6 = () -> {
            animator(ten);
            animator(fourteen);
            animator(eighteen);
        };

        Runnable r7 = () -> {
            animator(fifteen);
            animator(nineteen);
        };

//        Runnable r8 = () -> {
//            @Override
//            public void run() {
//                animator(twenty);
//            }
//        };

        Runnable r9 = () -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
            animation.setInterpolator(new AccelerateInterpolator());
            twentyOne.setAnimation(animation);
            twentyOne.setVisibility(View.VISIBLE);
            animation.start();
        };


        android.os.Handler handler = new android.os.Handler();
        int counter = 85;
        handler.postDelayed(r1, counter);
        handler.postDelayed(r2, counter * 2);
        handler.postDelayed(r3, counter * 3);
        handler.postDelayed(r4, counter * 4);
        handler.postDelayed(r5, counter * 5);
        handler.postDelayed(r6, counter * 6);
        handler.postDelayed(r7, counter * 7);
//        handler.postDelayed(r8,counter * 8);
        handler.postDelayed(r9, counter * 8);
    }

    private void animator(final ImageButton imageButton) {
        Animation animation = AnimationUtils.loadAnimation(getContext(),
                R.anim.color_item);
        animation.setInterpolator(new AccelerateInterpolator());
        imageButton.setAnimation(animation);
        imageButton.setVisibility(View.VISIBLE);
        animation.start();
    }

    public void setColorListener(ColorListener listener) {
        this.myColorListener = listener;
    }


}
