package com.bean.heroitem;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.List;

/**
 * Created on 2017/06/14.
 */
public class Items{

    private List<Bean> items;

    private int status;

    public List<Bean> getItems() {
        return items;
    }

    public void setItems(List<Bean> items) {
        this.items = items;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNameById(int id) {
        for (Bean item : items) {
            if (item.getId() == id) {
                return item.getName();
            }
        }
        return "";
    }

}
