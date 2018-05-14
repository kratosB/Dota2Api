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

    private Long accountId;

    private Integer playerSlot;

    private Integer heroId;

    @Column(name="item_0")
    private Integer item0;

    @Column(name="item_1")
    private Integer item1;

    @Column(name="item_2")
    private Integer item2;

    @Column(name="item_3")
    private Integer item3;

    @Column(name="item_4")
    private Integer item4;

    @Column(name="item_5")
    private Integer item5;

    @Column(name="backpack_0")
    private Integer backpack0;

    @Column(name="backpack_1")
    private Integer backpack1;

    @Column(name="backpack_2")
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
