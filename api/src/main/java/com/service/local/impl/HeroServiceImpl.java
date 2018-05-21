package com.service.local.impl;

import com.dao.HeroDao;
import com.dao.entity.Hero;
import com.service.local.IHeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created on 2017/06/14.
 * 
 * @author zhiqiang bao
 */
@Service
public class HeroServiceImpl implements IHeroService {

    private HeroDao heroDao;

    @Autowired
    public HeroServiceImpl(HeroDao heroDao) {
        this.heroDao = heroDao;
    }

    @Override
    public List<Hero> listAll() {
        return heroDao.findAll();
    }

    @Override
    public Hero findById(int id) {
        return heroDao.findOne(id);
    }

}
