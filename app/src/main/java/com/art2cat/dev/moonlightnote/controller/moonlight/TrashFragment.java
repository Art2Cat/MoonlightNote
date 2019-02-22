package com.art2cat.dev.moonlightnote.controller.moonlight;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Art on 2016/11/2 20:06.
 */

public class TrashFragment extends MoonlightListFragment {

  @Override
  public Query getQuery(DatabaseReference databaseReference) {
    return databaseReference.child("users-moonlight")
        .child(getUid()).child("trash");
  }

  @Override
  public boolean isTrash() {
    return true;
  }
}
