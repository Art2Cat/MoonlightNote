package com.art2cat.dev.moonlightnote.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rorschach
 * on 12/17/16 5:10 PM.
 */

public class NoteLab {
    public List<Moonlight> MoonlightNote;

    public NoteLab() {
        MoonlightNote = new ArrayList<Moonlight>();
    }

    public NoteLab(List<Moonlight> MoonlightNote) {
        this.MoonlightNote = MoonlightNote;
    }

    public List<Moonlight> getMoonlights() {
        return MoonlightNote;
    }

    public void setMoonlight(Moonlight moonlight) {
        MoonlightNote.add(moonlight);
    }

    public Moonlight getMoonlight(int i) {
        return MoonlightNote.get(i);
    }
}
