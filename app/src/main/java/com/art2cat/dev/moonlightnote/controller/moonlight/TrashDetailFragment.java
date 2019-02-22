package com.art2cat.dev.moonlightnote.controller.moonlight;

import com.art2cat.dev.moonlightnote.model.Moonlight;

/**
 * Created by rorschach on 11/5/16 7:08 PM.
 */

public class TrashDetailFragment extends MoonlightDetailFragment {

  public static TrashDetailFragment newInstance(Moonlight moonlight, int flag) {

    TrashDetailFragment trashDetailFragment = new TrashDetailFragment();
    trashDetailFragment.setArgs(moonlight, flag);
    return trashDetailFragment;
  }
}
