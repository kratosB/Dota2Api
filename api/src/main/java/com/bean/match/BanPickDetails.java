package com.bean.match;

import com.bean.heroitem.Bean;

import java.util.HashMap;
import java.util.List;

/**
 * Created on 2017/06/19.
 */
public class BanPickDetails {

    HashMap<Integer, Hero> heroes;

    public BanPickDetails() {
        heroes = new HashMap<>();
    }

    public HashMap<Integer, Hero> getHeroes() {
        return heroes;
    }

    public void setHeroes(HashMap<Integer, Hero> heroes) {
        this.heroes = heroes;
    }

    public void pickHero(int heroId) {
        if (heroes.containsKey(heroId)) {
            Hero hero = heroes.get(heroId);
            hero.pickCount = hero.pickCount + 1;
            hero.totalCount = hero.totalCount + 1;
        } else {
            Hero hero = new Hero();
            hero.pickCount = 1;
            hero.totalCount = 1;
            heroes.put(heroId, hero);
        }
    }

    public void banHero(int heroId) {
        if (heroes.containsKey(heroId)) {
            Hero hero = heroes.get(heroId);
            hero.banCount = hero.banCount + 1;
            hero.totalCount = hero.totalCount + 1;
        } else {
            Hero hero = new Hero();
            hero.banCount = 1;
            hero.totalCount = 1;
            heroes.put(heroId, hero);
        }
    }

    public void setHeroName(List<Bean> heroesBean) {
        heroesBean.stream().forEach(heroBean -> {
            if (heroes.containsKey(heroBean.getId())) {
                Hero hero = heroes.get(heroBean.getId());
                hero.heroName = heroBean.getLocalized_name();
            }
        });
    }

    public class Hero {

        private String heroName;

        private int totalCount;

        private int pickCount;

        private int banCount;

        public String getHeroName() {
            return heroName;
        }

        public void setHeroName(String heroName) {
            this.heroName = heroName;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getPickCount() {
            return pickCount;
        }

        public void setPickCount(int pickCount) {
            this.pickCount = pickCount;
        }

        public int getBanCount() {
            return banCount;
        }

        public void setBanCount(int banCount) {
            this.banCount = banCount;
        }
    }
}
