package com.fizanyatik.sportsclub.List;

public class RankList {
    private String name;
    private String image;
    private int rank;
    private float points;

    public RankList(String name, String image, int rank, float points) {
        this.name = name;
        this.image = image;
        this.rank = rank;
        this.points = points;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }
}
