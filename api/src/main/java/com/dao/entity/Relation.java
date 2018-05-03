package com.dao.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 2018/5/3.
 * 
 * @author zhiqiang bao
 */
@Data
@Entity
@Table(name = "relation")
public class Relation {

    @Id
    private int id;

    private String steamId;

    private String friendId;
}
