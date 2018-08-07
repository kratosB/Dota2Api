package com.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String steamId;

    private String friendId;
}
