package com.art2cat.dev.moonlightnote.controller.settings;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.art2cat.dev.moonlightnote.R;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class CommonSettingsFragment extends Fragment {

  private static final String TAG = CommonSettingsFragment.class.getName();

  private SettingsTypeEnum type;

  public CommonSettingsFragment() {
    // Required empty public constructor
  }

  public abstract String getContent();

  public abstract Fragment newInstance();

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    Bundle arguments = getArguments();
    if (Objects.nonNull(arguments)) {
      type = (SettingsTypeEnum) arguments.getSerializable(SettingsTypeEnum.class.getSimpleName());
    }
    Activity activity = getActivity();
    Objects.requireNonNull(activity);
    LinearLayout linearLayout = new LinearLayout(activity);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    linearLayout.setLayoutParams(params);
    ScrollView scrollView = new ScrollView(activity);
    scrollView.setLayoutParams(params);
    linearLayout.addView(scrollView);
    TextView textView = new TextView(activity);
    textView.setLayoutParams(params);
    int padding = getResources().getDimensionPixelOffset(R.dimen.padding);
    textView.setPadding(padding, padding, padding, padding);

    switch (type) {
      case ABOUT:
        textView.setGravity(Gravity.CENTER);
        textView.setText(getContent());
        activity.setTitle(R.string.settings_about);
        break;
      case LICENSE:
        textView.setGravity(Gravity.CENTER);
        textView.setText(getContent());
        activity.setTitle(R.string.settings_license);
        break;
      case POLICY:
        textView.setGravity(Gravity.START);
        textView.setText(getContent());
        activity.setTitle(R.string.settings_policy);
        break;
      default:
        Log.d(TAG, "onCreateView: No SettingsTypeEnum found");
        break;
    }

    setHasOptionsMenu(true);
    scrollView.addView(textView);
    return linearLayout;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      getActivity().onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

}
