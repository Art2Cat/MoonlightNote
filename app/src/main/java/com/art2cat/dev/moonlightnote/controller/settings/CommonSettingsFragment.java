package com.art2cat.dev.moonlightnote.controller.settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.art2cat.dev.moonlightnote.model.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class CommonSettingsFragment extends Fragment {

  private int mType;

  public CommonSettingsFragment() {
    // Required empty public constructor
  }

  public abstract String getContent();

  public abstract Fragment newInstance();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    if (getArguments() != null) {
      mType = getArguments().getInt("type");
    }
    LinearLayout linearLayout = new LinearLayout(getActivity());
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    linearLayout.setLayoutParams(params);
    ScrollView scrollView = new ScrollView(getActivity());
    scrollView.setLayoutParams(params);
    linearLayout.addView(scrollView);
    TextView textView = new TextView(getActivity());
    textView.setLayoutParams(params);
    int padding = getResources().getDimensionPixelOffset(R.dimen.padding);
    textView.setPadding(padding, padding, padding, padding);
    switch (mType) {
      case Constants.FRAGMENT_ABOUT:
        textView.setGravity(Gravity.CENTER);
        textView.setText(getContent());
        getActivity().setTitle(R.string.settings_about);
        break;
      case Constants.FRAGMENT_LICENSE:
        textView.setGravity(Gravity.CENTER);
        textView.setText(getContent());
        getActivity().setTitle(R.string.settings_license);
        break;
      case Constants.FRAGMENT_POLICY:
        textView.setGravity(Gravity.START);
        textView.setText(getContent());
        getActivity().setTitle(R.string.settings_policy);
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
    switch (item.getItemId()) {
      case android.R.id.home:
        getActivity().onBackPressed();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

}
