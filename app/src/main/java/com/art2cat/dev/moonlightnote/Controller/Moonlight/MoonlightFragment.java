package com.art2cat.dev.moonlightnote.Controller.Moonlight;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by art2cat
 * on 8/13/16.
 */
public class MoonlightFragment extends MoonlightListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("users-moonlight")
                .child(getUid()).child("note");
    }
}
