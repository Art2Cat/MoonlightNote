package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.os.Bundle;

import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;

/**
 * Created by rorschach
 * on 11/5/16 7:08 PM.
 */

public class TrashDetailFragment extends MoonlightDetailFragment {
    @Override
    public MoonlightDetailFragment newInstance() {
        return null;
    }

    @Override
    public MoonlightDetailFragment newInstance(Moonlight moonlight) {
        MoonlightDetailFragment moonlightDetailFragment = new EditMoonlightFragment();
        Bundle args = new Bundle();
        args.putParcelable("moonlight", moonlight);
        args.putInt("trash", Constants.EXTRA_TYPE_TRASH);
        moonlightDetailFragment.setArguments(args);
        return moonlightDetailFragment;
    }
}
