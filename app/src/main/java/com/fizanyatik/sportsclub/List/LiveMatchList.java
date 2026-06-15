package com.fizanyatik.sportsclub.List;

public class LiveMatchList {
    // --- Identity ---
    private final String key;
    private final String matchName;
    private final String seriesName;

    // --- Current innings state ---
    private final String battingTeam;
    private final String bowlingTeam;
    private final int runs;
    private final int wickets;
    private final String overs;
    private final int target;
    private final boolean isFirstInnings;

    // --- Per-team scores (for both rows in the card) ---
    private final String nscScore;   // e.g. "85/3 (10.0)" or "-"
    private final String sbrScore;

    // --- 1st innings completed score label ---
    private final String firstInningsScoreStr;
    private final String firstInningsTeam;

    // --- NSC top performers ---
    private final String topNscBatterUid;
    private final String topNscBatterName;
    private final String topNscBatterScore;   // e.g. "35* (20)"
    private final String topNscBowlerUid;
    private final String topNscBowlerName;
    private final String topNscBowlerScore;   // e.g. "2-18"

    // --- SBR top performers ---
    private final String topSbrBatterUid;
    private final String topSbrBatterName;
    private final String topSbrBatterScore;
    private final String topSbrBowlerUid;
    private final String topSbrBowlerName;
    private final String topSbrBowlerScore;

    public LiveMatchList(String key, String matchName, String seriesName,
                         String battingTeam, String bowlingTeam,
                         int runs, int wickets, String overs,
                         int target, boolean isFirstInnings,
                         String nscScore, String sbrScore,
                         String firstInningsScoreStr, String firstInningsTeam,
                         String topNscBatterUid, String topNscBatterName, String topNscBatterScore,
                         String topNscBowlerUid, String topNscBowlerName, String topNscBowlerScore,
                         String topSbrBatterUid, String topSbrBatterName, String topSbrBatterScore,
                         String topSbrBowlerUid, String topSbrBowlerName, String topSbrBowlerScore) {
        this.key = key;
        this.matchName = matchName;
        this.seriesName = seriesName;
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
        this.runs = runs;
        this.wickets = wickets;
        this.overs = overs;
        this.target = target;
        this.isFirstInnings = isFirstInnings;
        this.nscScore = nscScore;
        this.sbrScore = sbrScore;
        this.firstInningsScoreStr = firstInningsScoreStr;
        this.firstInningsTeam = firstInningsTeam;
        this.topNscBatterUid = topNscBatterUid;
        this.topNscBatterName = topNscBatterName;
        this.topNscBatterScore = topNscBatterScore;
        this.topNscBowlerUid = topNscBowlerUid;
        this.topNscBowlerName = topNscBowlerName;
        this.topNscBowlerScore = topNscBowlerScore;
        this.topSbrBatterUid = topSbrBatterUid;
        this.topSbrBatterName = topSbrBatterName;
        this.topSbrBatterScore = topSbrBatterScore;
        this.topSbrBowlerUid = topSbrBowlerUid;
        this.topSbrBowlerName = topSbrBowlerName;
        this.topSbrBowlerScore = topSbrBowlerScore;
    }

    public String getKey()                  { return key; }
    public String getMatchName()            { return matchName; }
    public String getSeriesName()           { return seriesName; }
    public String getBattingTeam()          { return battingTeam; }
    public String getBowlingTeam()          { return bowlingTeam; }
    public int getRuns()                    { return runs; }
    public int getWickets()                 { return wickets; }
    public String getOvers()               { return overs; }
    public int getTarget()                  { return target; }
    public boolean isFirstInnings()         { return isFirstInnings; }
    public String getNscScore()             { return nscScore; }
    public String getSbrScore()             { return sbrScore; }
    public String getFirstInningsScoreStr() { return firstInningsScoreStr; }
    public String getFirstInningsTeam()     { return firstInningsTeam; }
    public String getTopNscBatterUid()      { return topNscBatterUid; }
    public String getTopNscBatterName()     { return topNscBatterName; }
    public String getTopNscBatterScore()    { return topNscBatterScore; }
    public String getTopNscBowlerUid()      { return topNscBowlerUid; }
    public String getTopNscBowlerName()     { return topNscBowlerName; }
    public String getTopNscBowlerScore()    { return topNscBowlerScore; }
    public String getTopSbrBatterUid()      { return topSbrBatterUid; }
    public String getTopSbrBatterName()     { return topSbrBatterName; }
    public String getTopSbrBatterScore()    { return topSbrBatterScore; }
    public String getTopSbrBowlerUid()      { return topSbrBowlerUid; }
    public String getTopSbrBowlerName()     { return topSbrBowlerName; }
    public String getTopSbrBowlerScore()    { return topSbrBowlerScore; }
}
