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
    private var twenty: ImageButton? = null
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

        one = findViewById(R.id.b1)
        two = findViewById(R.id.b2)
        three = findViewById(R.id.b3)
        four = findViewById(R.id.b4)
        five = findViewById(R.id.b5)
        six = findViewById(R.id.b6)
        seven = findViewById(R.id.b7)
        eight = findViewById(R.id.b8)
        nine = findViewById(R.id.b9)
        ten = findViewById(R.id.b10)
        eleven = findViewById(R.id.b11)
        twelve = findViewById(R.id.b12)
        thirteen = findViewById(R.id.b13)
        fourteen = findViewById(R.id.b14)
        fifteen = findViewById(R.id.b15)
        sixteen = findViewById(R.id.b16)
        seventeen = findViewById(R.id.b17)
        eighteen = findViewById(R.id.b18)
        nineteen = findViewById(R.id.b19)
        twenty = findViewById(R.id.b20)
        twentyOne = findViewById(R.id.b21)


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
        buttons!!.add(one as ImageButton)
        buttons!!.add(two as ImageButton)
        buttons!!.add(three as ImageButton)
        buttons!!.add(four as ImageButton)
        buttons!!.add(five as ImageButton)
        buttons!!.add(six as ImageButton)
        buttons!!.add(seven as ImageButton)
        buttons!!.add(eight as ImageButton)
        buttons!!.add(nine as ImageButton)
        buttons!!.add(ten as ImageButton)
        buttons!!.add(eleven as ImageButton)
        buttons!!.add(twelve as ImageButton)
        buttons!!.add(thirteen as ImageButton)
        buttons!!.add(fourteen as ImageButton)
        buttons!!.add(fifteen as ImageButton)
        buttons!!.add(sixteen as ImageButton)
        buttons!!.add(seventeen as ImageButton)
        buttons!!.add(eighteen as ImageButton)
        buttons!!.add(nineteen as ImageButton)
        //        buttons.add(twenty);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Colorize()
        } else {
            ColorizeOld()
        }

        twenty!!.visibility = View.GONE
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
            animator(one as ImageButton)
        }

        val r2 = Runnable {
            animator(two as ImageButton)
            animator(six as ImageButton)
        }

        val r3 = Runnable {
            animator(three as ImageButton)
            animator(seven as ImageButton)
            animator(eleven as ImageButton)
        }

        val r4 = Runnable {
            animator(four as ImageButton)
            animator(eight as ImageButton)
            animator(twelve as ImageButton)
            animator(sixteen as ImageButton)
        }

        val r5 = Runnable {
            animator(five as ImageButton)
            animator(nine as ImageButton)
            animator(thirteen as ImageButton)
            animator(seventeen as ImageButton)
        }

        val r6 = Runnable {
            animator(ten as ImageButton)
            animator(fourteen as ImageButton)
            animator(eighteen as ImageButton)
        }

        val r7 = Runnable {
            animator(fifteen as ImageButton)
            animator(nineteen as ImageButton)
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
