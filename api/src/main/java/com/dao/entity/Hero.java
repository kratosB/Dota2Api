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
@Table(name = "hero")
public class Hero {

    @Id
    private int id;

    private String name;

    private String localizedName;

}
