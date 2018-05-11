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

    private Long match_id;

    private Integer account_id;

    private Integer player_slot;

    private Integer hero_id;

    private Integer item_0;

    private Integer item_1;

    private Integer item_2;

    private Integer item_3;

    private Integer item_4;

    private Integer item_5;

    private Integer backpack_0;

    private Integer backpack_1;

    private Integer backpack_2;

    private Integer kills;

    private Integer deaths;

    private Integer assists;

    private Integer leaver_status;

    private Integer last_hits;

    private Integer denies;

    private Integer gold_per_min;

    private Integer xp_per_min;

    private Integer level;

    private Integer hero_damage;

    private Integer tower_damage;

    private Integer hero_healing;

    private Integer gold;

    private Integer gold_spent;

    private Integer scaled_hero_damage;

    private Integer scaled_tower_damage;

    private Integer scaled_hero_healing;

    private String ability_upgrades;

}
