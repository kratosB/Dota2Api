package com.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


/**
 * Created on 2018/3/20.
 *
 * @author zhiqiang bao
 */
@Data
@Entity
@Table(name = "player")
public class Player {

    @Id
    private String steamid;

    private String dotaAccountId;

    private int communityvisibilitystate;

    private int profilestate;

    private String personaname;

    private Long lastlogoff;

    private String profileurl;

    private String avatar;

    private String avatarmedium;

    private String avatarfull;

    private int personastate;

    private String primaryclanid;

    private Long timecreated;

    private int personastateflags;

    private String loccountrycode;
}
