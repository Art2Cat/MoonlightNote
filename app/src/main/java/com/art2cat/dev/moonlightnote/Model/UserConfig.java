package com.art2cat.dev.moonlightnote.Model;

import java.util.List;
import java.util.Map;

/**
 * Created by art2cat
 * on 9/20/16.
 */
public class UserConfig {
    public String userId;

    public List<String> labels;

    public Map<String, Integer> colors;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Integer> getColors() {
        return colors;
    }

    public void setColors(Map<String, Integer> colors) {
        this.colors = colors;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> label) {
        this.labels = label;
    }
}
