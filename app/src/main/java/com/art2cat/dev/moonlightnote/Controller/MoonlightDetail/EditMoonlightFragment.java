package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.os.Bundle;

import com.art2cat.dev.moonlightnote.Model.Moonlight;

/**
 * Created by art2cat
 * on 10/1/16.
 */

public class EditMoonlightFragment extends MoonlightDetailFragment {

    @Override
    public MoonlightDetailFragment setArgs(Moonlight moonlight) {
        Bundle args = new Bundle();
        args.putParcelable("moonlight", moonlight);
        this.setArguments(args);
        return this;
    }
}
