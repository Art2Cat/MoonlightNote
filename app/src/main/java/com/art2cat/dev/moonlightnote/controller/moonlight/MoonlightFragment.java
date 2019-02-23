package com.art2cat.dev.moonlightnote.controller.moonlight;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by rorschach.h on 8/13/16.
 */
public class MoonlightFragment extends MoonlightListFragment {

  @Override
  public Query getQuery(DatabaseReference databaseReference) {
    return databaseReference.child("users-moonlight")
        .child(getUid()).child("note");
  }

  @Override
  public boolean isTrash() {
    return false;
  }
}
