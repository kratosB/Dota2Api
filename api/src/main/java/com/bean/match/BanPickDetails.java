package com.bean.match;

import java.util.HashMap;
import java.util.List;

import com.dao.entity.Hero;

import lombok.Data;

/**
 * Created on 2017/06/19.
 */
public class BanPickDetails {

    HashMap<Integer, BanPickHero> heroes;

    public BanPickDetails() {
        heroes = new HashMap<>();
    }

    public HashMap<Integer, BanPickHero> getHeroes() {
        return heroes;
    }

    public void setHeroes(HashMap<Integer, BanPickHero> heroes) {
        this.heroes = heroes;
    }

    public void pickHero(int heroId) {
        if (heroes.containsKey(heroId)) {
            BanPickHero hero = heroes.get(heroId);
            hero.pickCount = hero.pickCount + 1;
            hero.totalCount = hero.totalCount + 1;
        } else {
            BanPickHero hero = new BanPickHero();
            hero.pickCount = 1;
            hero.totalCount = 1;
            heroes.put(heroId, hero);
        }
    }

    public void banHero(int heroId) {
        if (heroes.containsKey(heroId)) {
            BanPickHero hero = heroes.get(heroId);
            hero.banCount = hero.banCount + 1;
            hero.totalCount = hero.totalCount + 1;
        } else {
            BanPickHero hero = new BanPickHero();
            hero.banCount = 1;
            hero.totalCount = 1;
            heroes.put(heroId, hero);
        }
    }

    public void addWinCount(int heroId) {
        if (heroes.containsKey(heroId)) {
            BanPickHero hero = heroes.get(heroId);
            hero.winCount = hero.winCount + 1;
        } else {
            BanPickHero hero = new BanPickHero();
            hero.winCount = 1;
            heroes.put(heroId, hero);
        }
    }

    public void addLoseCount(int heroId) {
        if (heroes.containsKey(heroId)) {
            BanPickHero hero = heroes.get(heroId);
            hero.loseCount = hero.loseCount + 1;
        } else {
            BanPickHero hero = new BanPickHero();
            hero.loseCount = 1;
            heroes.put(heroId, hero);
        }
    }

    public void setHeroName(List<Hero> heroList) {
        heroList.forEach(hero -> {
            if (heroes.containsKey(hero.getId())) {
                BanPickHero banPickHero = heroes.get(hero.getId());
                banPickHero.heroName = hero.getLocalizedName();
            }
        });
    }

    @Data
    public class BanPickHero {

        private String heroName;

        private int totalCount;

        private int pickCount;

        private int banCount;

        private int winCount;

        private int loseCount;

        private double winRate;

    }
}
