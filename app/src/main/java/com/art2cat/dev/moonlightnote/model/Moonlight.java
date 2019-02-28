package com.art2cat.dev.moonlightnote.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rorschach.h on 9/17/16.
 */
public class Moonlight implements Parcelable, Cloneable {

  public static final Creator<Moonlight> CREATOR = new Creator<Moonlight>() {
    @Override
    public Moonlight createFromParcel(Parcel in) {
      return new Moonlight(in);
    }

    @Override
    public Moonlight[] newArray(int size) {
      return new Moonlight[size];
    }
  };

  private String id;
  private String title;
  private String content;
  private String imageUrl;
  private String audioUrl;
  private long date;
  private long audioDuration;
  private String label;
  private String imageName;
  private String audioName;
  private int color;
  private boolean trash;

  public Moonlight() {

  }

  public Moonlight(String id, String title, String content, String imageUrl, String audioUrl,
      long date, long audioDuration,
      String label, String imageName, String audioName, int color, boolean trash) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.imageUrl = imageUrl;
    this.audioUrl = audioUrl;
    this.date = date;
    this.audioDuration = audioDuration;
    this.label = label;
    this.imageName = imageName;
    this.audioName = audioName;
    this.color = color;
    this.trash = trash;
  }

  protected Moonlight(Parcel in) {
    id = in.readString();
    title = in.readString();
    content = in.readString();
    imageUrl = in.readString();
    audioUrl = in.readString();
    date = in.readLong();
    audioDuration = in.readLong();
    label = in.readString();
    imageName = in.readString();
    audioName = in.readString();
    color = in.readInt();
    trash = in.readByte() != 0;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public long getDate() {
    return date;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  public String getAudioUrl() {
    return audioUrl;
  }

  public void setAudioUrl(String audioUrl) {
    this.audioUrl = audioUrl;
  }

  public String getAudioName() {
    return audioName;
  }

  public void setAudioName(String audioName) {
    this.audioName = audioName;
  }

  public long getAudioDuration() {
    return audioDuration;
  }

  public void setAudioDuration(long audioDuration) {
    this.audioDuration = audioDuration;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public boolean isTrash() {
    return trash;
  }

  public void setTrash(boolean trash) {
    this.trash = trash;
  }

  public Map<String, Object> toMap() {
    HashMap<String, Object> moonlight = new HashMap<>();
    moonlight.put("id", id);
    moonlight.put("title", title);
    moonlight.put("content", content);
    moonlight.put("imageUrl", imageUrl);
    moonlight.put("audioUrl", audioUrl);
    moonlight.put("date", date);
    moonlight.put("audioDuration", audioDuration);
    moonlight.put("label", label);
    moonlight.put("imageName", imageName);
    moonlight.put("audioName", audioName);
    moonlight.put("color", color);
    moonlight.put("trash", trash);
    return moonlight;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(id);
    parcel.writeString(title);
    parcel.writeString(content);
    parcel.writeString(imageUrl);
    parcel.writeString(audioUrl);
    parcel.writeLong(date);
    parcel.writeLong(audioDuration);
    parcel.writeString(label);
    parcel.writeString(imageName);
    parcel.writeString(audioName);
    parcel.writeInt(color);
    parcel.writeByte((byte) (trash ? 1 : 0));
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    super.clone();
    return new Moonlight(id, title, content, imageUrl, audioUrl,
        date, audioDuration, label, imageName, audioName, color, trash);
  }
}
