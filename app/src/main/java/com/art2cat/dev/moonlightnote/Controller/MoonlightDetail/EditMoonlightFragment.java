package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.os.Bundle;

import com.art2cat.dev.moonlightnote.Model.Moonlight;

/**
 * Created by art2cat
 * on 10/1/16.
 */

public class EditMoonlightFragment extends MoonlightDetailFragment {
    @Override
    public MoonlightDetailFragment newInstance() {
        return null;
    }

    @Override
    public MoonlightDetailFragment newInstance(Moonlight moonlight) {
        MoonlightDetailFragment moonlightDetailFragment = new EditMoonlightFragment();
        Bundle args = new Bundle();
        args.putParcelable("moonlight", moonlight);
        moonlightDetailFragment.setArguments(args);
        return moonlightDetailFragment;
    }
}
