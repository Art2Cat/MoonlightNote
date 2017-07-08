package com.art2cat.dev.moonlightnote.controller.settings;

import android.util.Log;

import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.utils.ToastUtils;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;

public class MoonlightPinActivity extends AppLockActivity {

//    @Override
//    public void showForgotDialog() {
//        Resources res = getResources();
//        // Create the builder with required paramaters - Context, Title, Positive Text
//        CustomDialog.Builder builder = new CustomDialog.Builder(this,
//                res.getString(R.string.activity_dialog_title),
//                res.getString(R.string.activity_dialog_accept));
//        builder.content(res.getString(R.string.activity_dialog_content));
//        builder.negativeText(res.getString(R.string.activity_dialog_decline));
//
//        //Set theme
//        builder.darkTheme(false);
//        builder.typeface(Typeface.SANS_SERIF);
//        builder.positiveColor(res.getColor(R.color.colorPrimary)); // int res, or int colorRes parameter versions available as well.
//        builder.negativeColor(res.getColor(R.color.colorPrimary));
//        builder.rightToLeft(false); // Enables right to left positioning for languages that may require so.
//        builder.titleAlignment(BaseDialog.Alignment.CENTER);
//        builder.buttonAlignment(BaseDialog.Alignment.CENTER);
//        builder.setButtonStacking(false);
//
//        //Set text sizes
//        builder.titleTextSize((int) res.getDimension(R.dimen.activity_dialog_title_size));
//        builder.contentTextSize((int) res.getDimension(R.dimen.activity_dialog_content_size));
//        builder.positiveButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_positive_button_size));
//        builder.negativeButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_negative_button_size));
//
//        //Build the dialog.
//        CustomDialog customDialog = builder.build();
//        customDialog.setCanceledOnTouchOutside(false);
//        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        customDialog.setClickListener(new CustomDialog.ClickListener() {
//            @Override
//            public void onConfirmClick() {
//                Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MoonlightPinActivity.this, MoonlightPinActivity.class);
//                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.CONFIRM_PIN);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onCancelClick() {
//                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Show the dialog.
//        customDialog.show();
//    }

    @Override
    public void showForgotDialog() {
        ToastUtils.with(this)
                .setMessage("For security this feature has been disabled!")
                .showShortToast();
    }

    @Override
    public void onPinFailure(int attempts) {
        if (attempts == 5) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    @Override
    public void onPinSuccess(int attempts) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onPinSuccess: " + attempts);
        finish();
    }

    @Override
    protected void onPinCodeSuccess() {
        super.onPinCodeSuccess();
        if (getIntent().getIntExtra(AppLock.EXTRA_TYPE, 78) == 0) {
            ToastUtils.with(this)
                    .setMessage("App protection will enable next time.")
                    .showShortToast();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        super.onBackPressed();
    }
}
