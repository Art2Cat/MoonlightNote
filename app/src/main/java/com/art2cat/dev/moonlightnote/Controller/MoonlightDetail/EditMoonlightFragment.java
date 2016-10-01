package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.os.Bundle;

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
    public MoonlightDetailFragment newInstance(String keyid) {
        MoonlightDetailFragment moonlightDetailFragment = new EditMoonlightFragment();
        Bundle args = new Bundle();
        args.putString("keyId", keyid);
        moonlightDetailFragment.setArguments(args);
        return moonlightDetailFragment;
    }
}
