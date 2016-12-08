package com.art2cat.dev.moonlightnote.Controller.Settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;

import java.util.List;

/**
 * Created by Rorschach
 * on 12/8/16 7:25 PM.
 */

class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {
    private LayoutInflater layoutInflater;
    private List<String> mData;
    private Context context;

    SettingsAdapter(Context context, List<String> data) {
        layoutInflater = LayoutInflater.from(context);
        mData = data;
        this.context = context;
    }

    @Override
    public SettingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.settings_item, parent, false);
        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SettingsViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.item.setText(mData.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int code =
                        SPUtils.getInt(context.getApplicationContext(),
                                Constants.USER_CONFIG,
                                Constants.USER_CONFIG_SECURITY_ENABLE, 0);
                switch (mData.get(position)) {
                    case "Security":
                        FragmentUtils.changeFragment((Activity) context, new SecurityFragment());
                        Utils.lockApp(context, code);
                        break;
                    case "Policy":
                        FragmentUtils.changeFragment((Activity) context, new PolicyFragment());
                        break;
                    case "License":
                        FragmentUtils.changeFragment((Activity) context, new LicenseFragment());
                        break;
                    case "About":
                        FragmentUtils.changeFragment((Activity) context, new AboutFragment());
                        break;
                    case "Disable Security":
                        SPUtils.putInt(context.getApplicationContext(),
                                Constants.USER_CONFIG,
                                Constants.USER_CONFIG_SECURITY_ENABLE, 0);
                        break;
                    case "Password":
                        Utils.showToast(context, "Current not enable", 0);
                        break;
                    case "Pin":
                        FragmentUtils.changeFragment((Activity) context, new PinFragment());
                        break;
                    case "Pattern":
                        Utils.showToast(context, "Current not enable", 0);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class SettingsViewHolder extends RecyclerView.ViewHolder {
        public TextView item;

        SettingsViewHolder(View itemView) {
            super(itemView);
            item = (TextView) itemView.findViewById(R.id.settings_item);
        }
    }
}
