package com.art2cat.dev.moonlightnote.controller.moonlight;

import com.art2cat.dev.moonlightnote.model.Moonlight;

/**
 * Created by rorschach.h on 10/1/16.
 */

public class EditMoonlightFragment extends MoonlightDetailFragment {

  public static EditMoonlightFragment newInstance(Moonlight moonlight, int flag) {

    EditMoonlightFragment editMoonlightFragment = new EditMoonlightFragment();
    editMoonlightFragment.setArgs(moonlight, flag);
    return editMoonlightFragment;
  }
}
