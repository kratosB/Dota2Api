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
