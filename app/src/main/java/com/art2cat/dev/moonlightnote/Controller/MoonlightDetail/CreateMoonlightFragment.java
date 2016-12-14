package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import com.art2cat.dev.moonlightnote.Model.Moonlight;

/**
 * Created by art2cat
 * on 10/1/16.
 */

public class CreateMoonlightFragment extends MoonlightDetailFragment{
    @Override
    public MoonlightDetailFragment newInstance() {
        MoonlightDetailFragment moonlightDetailFragment = new CreateMoonlightFragment();
        return moonlightDetailFragment;
    }

    @Override
    public MoonlightDetailFragment newInstance(Moonlight moonlight) {
        return null;
    }

}
