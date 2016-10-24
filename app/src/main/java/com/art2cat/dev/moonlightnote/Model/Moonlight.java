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
    public long date;
    public String label;
    public String imageName;

    public Moonlight() {

    }

    public Moonlight(String id, String title, String content, String imageUrl, long date,
                     String label, String imageName) {
        this.id= id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.date = date;
        this.label = label;
        this.imageName = imageName;
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

    public Map<String, Object> toMap() {
        HashMap<String, Object> moonlight = new HashMap<>();
        moonlight.put("id", id);
        moonlight.put("title", title);
        moonlight.put("content", content);
        moonlight.put("imageUrl", imageUrl);
        moonlight.put("date", date);
        moonlight.put("label", label);
        moonlight.put("imageName", imageName);
        return moonlight;
    }
}
