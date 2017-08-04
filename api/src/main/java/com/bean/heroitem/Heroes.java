package com.bean.heroitem;

import java.util.List;

/**
 * Created on 2017/06/14.
 */
public class Heroes{

    private List<Bean> heroes;

    private int status;

    private int count;

    public List<Bean> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<Bean> heroes) {
        this.heroes = heroes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNameById(int id) {
        for (Bean hero : heroes) {
            if (hero.getId() == id) {
                return hero.getLocalized_name();
            }
        }
        return "";
    }
}
