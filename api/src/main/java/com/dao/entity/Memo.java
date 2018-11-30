package com.dao.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Created on 2018/11/30.
 *
 * @author zhiqiang bao
 */
@Data
@Entity
@Table(name = "memo")
public class Memo {

    @Id
    private long id;

    private String openId;

    private String content;

    private Date createdTime;

    private Date updatedTime;
}
