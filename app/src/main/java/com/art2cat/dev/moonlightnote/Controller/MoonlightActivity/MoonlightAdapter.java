package com.art2cat.dev.moonlightnote.Controller.MoonlightActivity;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * Created by art2cat
 * on 9/9/16.
 */
public class MoonlightAdapter extends RecyclerView.Adapter<MoonlightAdapter.MoonlightViewHolder> {
    private List<Moonlight> mdata;
    private Context mContext;

    public MoonlightAdapter() {

    }

    public MoonlightAdapter(Context context, List<Moonlight> data) {
        mContext = context;
        mdata = data;
    }

    @Override
    public MoonlightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MoonlightViewHolder holder = new MoonlightViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_moonlight, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MoonlightViewHolder holder, int position) {
        if (mdata != null) {
            holder.onBindMoonlight(mdata.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mdata != null) {
            return mdata.size();
        } else {
            return 0;
        }
    }


    public class MoonlightViewHolder extends RecyclerView.ViewHolder {
        public TextView date_tv;
        public TextView earnOrCost_tv;
        public TextView money_tv;
        public TextView payment_tv;
        public TextView detail_tv;
        public CardView cardView;

        public Moonlight moonlight;

        public MoonlightViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.item_main);

            date_tv = (TextView) view.findViewById(R.id.item_date);
            earnOrCost_tv = (TextView) view.findViewById(R.id.item_cost_or_earn);
            money_tv = (TextView) view.findViewById(R.id.item_money);
            payment_tv = (TextView) view.findViewById(R.id.item_payment);
            detail_tv = (TextView) view.findViewById(R.id.item_detail);
        }

        public void onBindMoonlight(Moonlight moonlight) {
            this.moonlight = moonlight;
            Date date = new Date(moonlight.date);
            date_tv.setText(Utils.dateFormat(date));
        }

    }

}
