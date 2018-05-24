package com.dao.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created on 2018/5/11.
 * 
 * @author zhiqiang bao
 */
@Data
@Entity
@Table(name = "match_history")
public class MatchHistory {

    @Id
    private Long matchId;

    private int radiantWin;

    private int duration;

    private int preGameDuration;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    private Long matchSeqNum;

    private int towerStatusRadiant;

    private int towerStatusDire;

    private int barracksStatusRadiant;

    private int barracksStatusDire;

    private int cluster;

    private int firstBloodTime;

    private int lobbyType;

    private int humanPlayers;

    private int leagueId;

    private int positiveVotes;

    private int negativeVotes;

    /**
     * game_mode
     0 - None
     1 - All Pick
     2 - Captain's Mode
     3 - Random Draft
     4 - Single Draft
     5 - All Random
     6 - Intro
     7 - Diretide
     8 - Reverse Captain's Mode
     9 - The Greeviling
     10 - Tutorial
     11 - Mid Only
     12 - Least Played
     13 - New Player Pool
     14 - Compendium Matchmaking
     15 - Co-op vs Bots
     16 - Captains Draft
     18 - Ability Draft
     20 - All Random Deathmatch
     21 - 1v1 Mid Only
     22 - Ranked Matchmaking
     */
    private int gameMode;

    private int flags;

    private int engine;

    private int radiantScore;

    private int direScore;

    private int radiantTeamId;

    private String radiantName;

    private Long radiantLogo;

    private int radiantTeamComplete;

    private int direTeamId;

    private String direName;

    private Long direLogo;

    private int direTeamComplete;

    private Long radiantCaptain;

    private Long direCaptain;

    private String picksBans;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

}
