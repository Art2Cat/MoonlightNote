package com.art2cat.dev.moonlightnote.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rorschach.h on 12/17/16 5:10 PM.
 */

public class NoteLab {

  private int size;

  private List<Moonlight> MoonlightNote;

  public NoteLab() {
    MoonlightNote = new ArrayList<>();
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

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
