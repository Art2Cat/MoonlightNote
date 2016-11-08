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
    public String imageUrl;
    public String audioUrl;
    public long date;
    public String label;
    public String imageName;
    public String audioName;
    public boolean trash;

    public Moonlight() {

    }

    public Moonlight(String id, String title, String content, String imageUrl, String audioUrl,
                     long date,
                     String label, String imageName, String audioName, boolean trash) {
        this.id= id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.date = date;
        this.label = label;
        this.imageName = imageName;
        this.audioName = audioName;
        this.trash = trash;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
        moonlight.put("label", label);
        moonlight.put("imageName", imageName);
        moonlight.put("audioName", audioName);
        moonlight.put("trash", trash);
        return moonlight;
    }
}
