package com.fizanyatik.sportsclub;

public class MatchList {
    private String match_details;
    private String match_result;
    private String match_scorecard;

    private String team1_name;
    private String team1_score;
    private String top_team1_name;
    private String top_team1_image;
    private String top_team1_score;
    private String top2_team1_name;
    private String top2_team1_image;
    private String top2_team1_score;

    private String team2_name;
    private String team2_score;
    private String top_team2_name;
    private String top_team2_image;
    private String top_team2_score;
    private String top2_team2_name;
    private String top2_team2_image;
    private String top2_team2_score;

    public MatchList(String match_details, String match_result, String match_scorecard, String team1_name,
                     String team1_score, String top_team1_name, String top_team1_image, String top_team1_score, String top2_team1_name,
                     String top2_team1_image, String top2_team1_score, String team2_name, String team2_score, String top_team2_name,
                     String top_team2_image, String top_team2_score, String top2_team2_name, String top2_team2_image, String top2_team2_score) {

        this.match_details = match_details;
        this.match_result = match_result;
        this.match_scorecard = match_scorecard;
        this.team1_name = team1_name;
        this.team1_score = team1_score;
        this.top_team1_name = top_team1_name;
        this.top_team1_image = top_team1_image;
        this.top_team1_score = top_team1_score;
        this.top2_team1_name = top2_team1_name;
        this.top2_team1_image = top2_team1_image;
        this.top2_team1_score = top2_team1_score;
        this.team2_name = team2_name;
        this.team2_score = team2_score;
        this.top_team2_name = top_team2_name;
        this.top_team2_image = top_team2_image;
        this.top_team2_score = top_team2_score;
        this.top2_team2_name = top2_team2_name;
        this.top2_team2_image = top2_team2_image;
        this.top2_team2_score = top2_team2_score;
    }

    public String getMatch_scorecard() {
        return match_scorecard;
    }

    public void setMatch_scorecard(String match_scorecard) {
        this.match_scorecard = match_scorecard;
    }


    public String getTop_team1_image() {
        return top_team1_image;
    }

    public void setTop_team1_image(String top_team1_image) {
        this.top_team1_image = top_team1_image;
    }

    public String getTop2_team1_image() {
        return top2_team1_image;
    }

    public void setTop2_team1_image(String top2_team1_image) {
        this.top2_team1_image = top2_team1_image;
    }

    public String getTop_team2_image() {
        return top_team2_image;
    }

    public void setTop_team2_image(String top_team2_image) {
        this.top_team2_image = top_team2_image;
    }

    public String getTop2_team2_image() {
        return top2_team2_image;
    }

    public void setTop2_team2_image(String top2_team2_image) {
        this.top2_team2_image = top2_team2_image;
    }


    public String getMatch_details() {
        return match_details;
    }

    public void setMatch_details(String match_details) {
        this.match_details = match_details;
    }

    public String getMatch_result() {
        return match_result;
    }

    public void setMatch_result(String match_result) {
        this.match_result = match_result;
    }

    public String getTeam1_name() {
        return team1_name;
    }

    public void setTeam1_name(String team1_name) {
        this.team1_name = team1_name;
    }

    public String getTeam1_score() {
        return team1_score;
    }

    public void setTeam1_score(String team1_score) {
        this.team1_score = team1_score;
    }

    public String getTop_team1_name() {
        return top_team1_name;
    }

    public void setTop_team1_name(String top_team1_name) {
        this.top_team1_name = top_team1_name;
    }

    public String getTop_team1_score() {
        return top_team1_score;
    }

    public void setTop_team1_score(String top_team1_score) {
        this.top_team1_score = top_team1_score;
    }

    public String getTop2_team1_name() {
        return top2_team1_name;
    }

    public void setTop2_team1_name(String top2_team1_name) {
        this.top2_team1_name = top2_team1_name;
    }

    public String getTop2_team1_score() {
        return top2_team1_score;
    }

    public void setTop2_team1_score(String top2_team1_score) {
        this.top2_team1_score = top2_team1_score;
    }

    public String getTeam2_name() {
        return team2_name;
    }

    public void setTeam2_name(String team2_name) {
        this.team2_name = team2_name;
    }

    public String getTeam2_score() {
        return team2_score;
    }

    public void setTeam2_score(String team2_score) {
        this.team2_score = team2_score;
    }

    public String getTop_team2_name() {
        return top_team2_name;
    }

    public void setTop_team2_name(String top_team2_name) {
        this.top_team2_name = top_team2_name;
    }

    public String getTop_team2_score() {
        return top_team2_score;
    }

    public void setTop_team2_score(String top_team2_score) {
        this.top_team2_score = top_team2_score;
    }

    public String getTop2_team2_name() {
        return top2_team2_name;
    }

    public void setTop2_team2_name(String top2_team2_name) {
        this.top2_team2_name = top2_team2_name;
    }

    public String getTop2_team2_score() {
        return top2_team2_score;
    }

    public void setTop2_team2_score(String top2_team2_score) {
        this.top2_team2_score = top2_team2_score;
    }
}
