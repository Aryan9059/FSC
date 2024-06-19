package com.fizanyatik.sportsclub.List;

public class FeedList {
    private String text;
    private String date;
    private String title;
    private String image;
    private String type;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    private String parent;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public FeedList(String text, String date, String title, String image, String type, String parent) {
        this.text = text;
        this.date = date;
        this.title = title;
        this.image = image;
        this.type = type;
        this.parent = parent;
    }
}
