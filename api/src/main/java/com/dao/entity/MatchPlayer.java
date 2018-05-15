package com.dao.entity;

import lombok.Data;

import javax.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long matchId;

    private int win;

    private Long accountId;

    private int playerSlot;

    private int heroId;

    @Column(name="item_0")
    private int item0;

    @Column(name="item_1")
    private int item1;

    @Column(name="item_2")
    private int item2;

    @Column(name="item_3")
    private int item3;

    @Column(name="item_4")
    private int item4;

    @Column(name="item_5")
    private int item5;

    @Column(name="backpack_0")
    private int backpack0;

    @Column(name="backpack_1")
    private int backpack1;

    @Column(name="backpack_2")
    private int backpack2;

    private int kills;

    private int deaths;

    private int assists;

    private int leaverStatus;

    private int lastHits;

    private int denies;

    private int goldPerMin;

    private int xpPerMin;

    private int level;

    private int heroDamage;

    private int towerDamage;

    private int heroHealing;

    private int gold;

    private int goldSpent;

    private int scaledHeroDamage;

    private int scaledTowerDamage;

    private int scaledHeroHealing;

    private String abilityUpgrades;

}
