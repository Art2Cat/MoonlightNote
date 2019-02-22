package com.art2cat.dev.moonlightnote.model;

/**
 * Created by art2cat on 9/21/16.
 */

public class BusEvent {

  private String message;

  private Moonlight moonlight;

  private int flag;

  public BusEvent() {
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getFlag() {
    return flag;
  }

  public void setFlag(int flag) {
    this.flag = flag;
  }


  public Moonlight getMoonlight() {
    return moonlight;
  }

  public void setMoonlight(Moonlight moonlight) {
    this.moonlight = moonlight;
  }
}
