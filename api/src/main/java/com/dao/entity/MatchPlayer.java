package com.dao.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 2018/5/11.
 *
 * @author zhiqiang bao
 */
@Data
@Entity
@Table(name = "match_player")
public class MatchPlayer {

    @Id
    private Long id;

    private Long matchId;

    private Long accountId;

    private Integer playerSlot;

    private Integer heroId;

    private Integer item0;

    private Integer item1;

    private Integer item2;

    private Integer item3;

    private Integer item4;

    private Integer item5;

    private Integer backpack0;

    private Integer backpack1;

    private Integer backpack2;

    private Integer kills;

    private Integer deaths;

    private Integer assists;

    private Integer leaverStatus;

    private Integer lastHits;

    private Integer denies;

    private Integer goldPerMin;

    private Integer xpPerMin;

    private Integer level;

    private Integer heroDamage;

    private Integer towerDamage;

    private Integer heroHealing;

    private Integer gold;

    private Integer goldSpent;

    private Integer scaledHeroDamage;

    private Integer scaledTowerDamage;

    private Integer scaledHeroHealing;

    private String abilityUpgrades;

}
