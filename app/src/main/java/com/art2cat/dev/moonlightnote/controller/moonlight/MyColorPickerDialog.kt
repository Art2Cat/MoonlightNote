package com.art2cat.dev.moonlightnote.controller.moonlight

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton

import com.art2cat.dev.moonlightnote.R
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog
import com.turkialkhateeb.materialcolorpicker.ColorListener

import java.util.ArrayList

/**
 * Created by Rorschach
 * on 12/9/16 11:10 PM.
 */

internal class MyColorPickerDialog(context: Context) : ColorChooserDialog(context) {
    private var one: ImageButton? = null
    private var two: ImageButton? = null
    private var three: ImageButton? = null
    private var four: ImageButton? = null
    private var five: ImageButton? = null
    private var six: ImageButton? = null
    private var seven: ImageButton? = null
    private var eight: ImageButton? = null
    private var nine: ImageButton? = null
    private var ten: ImageButton? = null
    private var eleven: ImageButton? = null
    private var twelve: ImageButton? = null
    private var thirteen: ImageButton? = null
    private var fourteen: ImageButton? = null
    private var fifteen: ImageButton? = null
    private var sixteen: ImageButton? = null
    private var seventeen: ImageButton? = null
    private var eighteen: ImageButton? = null
    private var nineteen: ImageButton? = null
    private var twentyOne: Button? = null
    private var colors: MutableList<Int>? = null
    private var buttons: MutableList<ImageButton>? = null
    private var myColorListener: ColorListener? = null
    private val listener = View.OnClickListener { v ->
        if (myColorListener != null)
            myColorListener!!.OnColorClick(v, v.tag as Int)
        dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        View view = LayoutInflater.from(getContext())
        //                .inflate(com.turkialkhateeb.materialcolorpicker.R.layout.color_picker_dialog, null);
        setContentView(R.layout.dialog_color_picker)
        //        int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.padding);
        //        view.setPadding(padding, padding, padding, 0);

        one = findViewById(R.id.b1) as ImageButton
        two = findViewById(R.id.b2) as ImageButton
        three = findViewById(R.id.b3) as ImageButton
        four = findViewById(R.id.b4) as ImageButton
        five = findViewById(R.id.b5) as ImageButton
        six = findViewById(R.id.b6) as ImageButton
        seven = findViewById(R.id.b7) as ImageButton
        eight = findViewById(R.id.b8) as ImageButton
        nine = findViewById(R.id.b9) as ImageButton
        ten = findViewById(R.id.b10) as ImageButton
        eleven = findViewById(R.id.b11) as ImageButton
        twelve = findViewById(R.id.b12) as ImageButton
        thirteen = findViewById(R.id.b13) as ImageButton
        fourteen = findViewById(R.id.b14) as ImageButton
        fifteen = findViewById(R.id.b15) as ImageButton
        sixteen = findViewById(R.id.b16) as ImageButton
        seventeen = findViewById(R.id.b17) as ImageButton
        eighteen = findViewById(R.id.b18) as ImageButton
        nineteen = findViewById(R.id.b19) as ImageButton
        val twenty = findViewById(R.id.b20) as ImageButton
        twentyOne = findViewById(R.id.b21) as Button


        colors = ArrayList<Int>()
        colors!!.add(Red)
        colors!!.add(Pink)
        colors!!.add(Purple)
        colors!!.add(DeepPurple)
        colors!!.add(Indigo)
        colors!!.add(Blue)
        colors!!.add(LightBlue)
        colors!!.add(Cyan)
        colors!!.add(Teal)
        colors!!.add(Green)
        colors!!.add(LightGreen)
        colors!!.add(Lime)
        colors!!.add(Yellow)
        colors!!.add(Amber)
        colors!!.add(Orange)
        colors!!.add(DeepOrange)
        colors!!.add(Brown)
        colors!!.add(Grey)
        colors!!.add(BlueGray)
        //        colors.add(Black);
        colors!!.add(White)

        buttons = ArrayList<ImageButton>()
        buttons!!.add(one)
        buttons!!.add(two)
        buttons!!.add(three)
        buttons!!.add(four)
        buttons!!.add(five)
        buttons!!.add(six)
        buttons!!.add(seven)
        buttons!!.add(eight)
        buttons!!.add(nine)
        buttons!!.add(ten)
        buttons!!.add(eleven)
        buttons!!.add(twelve)
        buttons!!.add(thirteen)
        buttons!!.add(fourteen)
        buttons!!.add(fifteen)
        buttons!!.add(sixteen)
        buttons!!.add(seventeen)
        buttons!!.add(eighteen)
        buttons!!.add(nineteen)
        //        buttons.add(twenty);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Colorize()
        } else {
            ColorizeOld()
        }

        twenty.visibility = View.GONE
        twentyOne!!.visibility = View.INVISIBLE

        setListeners()
    }

    private fun setListeners() {
        for (i in buttons!!.indices) {
            buttons!![i].tag = colors!![i]
            buttons!![i].setOnClickListener(listener)
        }
        twentyOne!!.tag = colors!![19]
        twentyOne!!.setOnClickListener(listener)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun Colorize() {
        for (i in buttons!!.indices) {
            val d = ShapeDrawable(OvalShape())
            d.setBounds(58, 58, 58, 58)
            Log.e("Shape drown no", i.toString() + "")
            buttons!![i].visibility = View.INVISIBLE

            d.paint.style = Paint.Style.FILL
            d.paint.color = colors!![i]

            buttons!![i].background = d
        }
        animate()

    }

    private fun ColorizeOld() {
        for (i in buttons!!.indices) {
            val d = ShapeDrawable(OvalShape())
            d.paint.color = colors!![i]
            d.paint.strokeWidth = 1f
            d.setBounds(58, 58, 58, 58)
            buttons!![i].visibility = View.INVISIBLE
            d.paint.style = Paint.Style.FILL
            d.paint.color = colors!![i]
            buttons!![i].background = d
        }
        animate()
    }

    private fun animate() {
        Log.e("animate", "true")
        val r1 = Runnable {
            Log.e("animator 1", "r")
            animator(one)
        }

        val r2 = Runnable {
            animator(two)
            animator(six)
        }

        val r3 = Runnable {
            animator(three)
            animator(seven)
            animator(eleven)
        }

        val r4 = Runnable {
            animator(four)
            animator(eight)
            animator(twelve)
            animator(sixteen)
        }

        val r5 = Runnable {
            animator(five)
            animator(nine)
            animator(thirteen)
            animator(seventeen)
        }

        val r6 = Runnable {
            animator(ten)
            animator(fourteen)
            animator(eighteen)
        }

        val r7 = Runnable {
            animator(fifteen)
            animator(nineteen)
        }

        //        Runnable r8 = new Runnable() {
        //            @Override
        //            public void run() {
        //                animator(twenty);
        //            }
        //        };

        val r9 = Runnable {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            animation.interpolator = AccelerateInterpolator()
            twentyOne!!.animation = animation
            twentyOne!!.visibility = View.VISIBLE
            animation.start()
        }


        val handler = android.os.Handler()
        val counter = 85
        handler.postDelayed(r1, counter.toLong())
        handler.postDelayed(r2, (counter * 2).toLong())
        handler.postDelayed(r3, (counter * 3).toLong())
        handler.postDelayed(r4, (counter * 4).toLong())
        handler.postDelayed(r5, (counter * 5).toLong())
        handler.postDelayed(r6, (counter * 6).toLong())
        handler.postDelayed(r7, (counter * 7).toLong())
        //        handler.postDelayed(r8,counter * 8);
        handler.postDelayed(r9, (counter * 8).toLong())
    }

    private fun animator(imageButton: ImageButton) {
        val animation = AnimationUtils.loadAnimation(context, com.turkialkhateeb.materialcolorpicker.R.anim.color_item)
        animation.interpolator = AccelerateInterpolator()
        imageButton.animation = animation
        imageButton.visibility = View.VISIBLE
        animation.start()
    }

    override fun setColorListener(listener: ColorListener) {
        this.myColorListener = listener
    }

    companion object {

        //CONSTANTS
        val Red = 0xffF44336.toInt()
        val Pink = 0xffE91E63.toInt()
        val Purple = 0xff9C27B0.toInt()
        val DeepPurple = 0xff673AB7.toInt()
        val Indigo = 0xff3F51B5.toInt()
        val Blue = 0xff2196F3.toInt()
        val LightBlue = 0xff03A9F4.toInt()
        val Cyan = 0xff00BCD4.toInt()
        val Teal = 0xff009688.toInt()
        val Green = 0xff4CAF50.toInt()
        val LightGreen = 0xff8BC34A.toInt()
        val Lime = 0xffCDDC39.toInt()
        val Yellow = 0xffFFEB3B.toInt()
        val Amber = 0xffFFC107.toInt()
        val Orange = 0xffFF9800.toInt()
        val DeepOrange = 0xffFF5722.toInt()
        val Brown = 0xff795548.toInt()
        val Grey = 0xff9E9E9E.toInt()
        val BlueGray = 0xff607D8B.toInt()
        //    public final int Black =      0xff000000;
        val White = 0xffffffff.toInt()
    }


}
