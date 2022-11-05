package com.fizanyatik.sportsclub;

public class FeedList {
    private String text;
    private String date;
    private String title;
    private String image;

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

    public FeedList(String text, String date, String title, String image) {
        this.text = text;
        this.date = date;
        this.title = title;
        this.image = image;
    }
}
