package com.art2cat.dev.moonlightnote.Controller.Settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.Controller.CommonDialogFragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * Created by Rorschach
 * on 12/8/16 7:25 PM.
 */

class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {
    private LayoutInflater layoutInflater;
    private  List<Map<String, Object>> mData;
    private Context context;

    SettingsAdapter(Context context,  List<Map<String, Object>> data) {
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
        final Map<String, Object> map = mData.get(position);
        holder.item.setText((CharSequence) map.get("Title"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int code =
                        SPUtils.getInt(context.getApplicationContext(),
                                Constants.USER_CONFIG,
                                Constants.USER_CONFIG_SECURITY_ENABLE, 0);
                switch ((int)map.get("Type")) {
                    case 304:
                        FragmentUtils.changeFragment((Activity) context, new SecurityFragment());
                        Utils.lockApp(context, code);
                        break;
                    case 301:
                        FragmentUtils.changeFragment((Activity) context, new PolicyFragment().newInstance());
                        break;
                    case 303:
                        FragmentUtils.changeFragment((Activity) context, new LicenseFragment().newInstance());
                        break;
                    case 302:
                        FragmentUtils.changeFragment((Activity) context, new AboutFragment().newInstance());
                        break;
                    case 305:
                        ConfirmationDialogFragment confirmationDialogFragment =
                                ConfirmationDialogFragment.newInstance("Confirmation", "Disable protection and decrypt all your data?", 4);
                        confirmationDialogFragment.show(((Activity)context).getFragmentManager(),null);
                        SPUtils.putInt(context.getApplicationContext(),
                                Constants.USER_CONFIG,
                                Constants.USER_CONFIG_SECURITY_ENABLE, 0);
                        break;
                    case 307:
                        Utils.showToast(context, "Current not available", 0);
                        break;
                    case 306:
                        FragmentUtils.changeFragment((Activity) context, new PinFragment());
                        break;
                    case 308:
                        Utils.showToast(context, "Current not available", 0);
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
