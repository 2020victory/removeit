package com.sanjaydevtech.chineseappdetector;

public class ScreenItem {
    private String title;
    private String description;
    private int img;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImg() {
        return img;
    }

    public ScreenItem(String title, String description, int img) {
        this.title = title;
        this.description = description;
        this.img = img;
    }
}
