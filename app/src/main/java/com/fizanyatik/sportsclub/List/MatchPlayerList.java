package com.fizanyatik.sportsclub.List;

public class MatchPlayerList {
    private String first;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    private String last;

    public MatchPlayerList(String first, String last, String image, String parent) {
        this.first = first;
        this.last = last;
        this.image = image;
        this.parent = parent;
    }

    private String image;
    private String parent;
}
