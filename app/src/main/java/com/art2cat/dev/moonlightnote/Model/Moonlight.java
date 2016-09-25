package com.art2cat.dev.moonlightnote.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by art2cat
 * on 9/17/16.
 */
public class Moonlight {
    public String id;
    public String title;
    public String content;
    public String photo;
    public long date;
    public String label;
    public String photoName;

    public Moonlight() {

    }

    public Moonlight(String id, String title, String content, long date, String label) {
        this.id= id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.label = label;
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
    public String getPhoto() {
        return photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> moonlight = new HashMap<>();
        moonlight.put("id", id);
        moonlight.put("title", title);
        moonlight.put("content", content);
        moonlight.put("photo", photo);
        moonlight.put("date", date);
        moonlight.put("label", label);
        moonlight.put("photoname", photoName);
        return moonlight;
    }
}
