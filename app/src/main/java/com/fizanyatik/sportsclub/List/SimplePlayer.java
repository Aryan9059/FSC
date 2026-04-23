package com.fizanyatik.sportsclub.List;

import java.io.Serializable;

// Implement Serializable to pass this object via Intent
public class SimplePlayer implements Serializable {
    String uid;
    String name;
    String team;
    String imageUrl;
    boolean isSelected = false;

    public SimplePlayer(String uid, String name, String team, String imageUrl) {
        this.uid = uid;
        this.name = name;
        this.team = team;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getTeam() { return team; }
    public String getImageUrl() { return imageUrl; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}