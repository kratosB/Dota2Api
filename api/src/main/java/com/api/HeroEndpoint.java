package com.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dao.entity.Hero;
import com.service.local.IHeroService;
import com.service.steam.ISteamHeroItemService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Created on 2017/06/14.
 *
 * @author zhiqiang bao
 */
@RestController
public class HeroEndpoint {

    private Logger logger = LoggerFactory.getLogger(HeroEndpoint.class);

    private IHeroService heroServiceImpl;

    private ISteamHeroItemService steamHeroItemServiceImpl;

    @Autowired
    public HeroEndpoint(IHeroService heroServiceImpl, ISteamHeroItemService steamHeroItemServiceImpl) {
        this.heroServiceImpl = heroServiceImpl;
        this.steamHeroItemServiceImpl = steamHeroItemServiceImpl;
    }

    @ApiOperation("获取所有hero")
    @GetMapping(value = "/api/hero/listAll")
    public List<Hero> listAll() {
        logger.info("开始获取所有hero数据");
        List<Hero> heroList = heroServiceImpl.listAll();
        logger.info("结束获取所有hero数据");
        return heroList;
    }

    @ApiOperation("根据heroId获取hero")
    @GetMapping(value = "/api/hero/findById")
    public Hero findById(
            @ApiParam(name = "heroId", required = true, defaultValue = "1") @RequestParam(name = "heroId", defaultValue = "1") int heroId) {
        logger.info("开始根据heroId获取hero数据，heroId = {}", heroId);
        Hero hero = heroServiceImpl.findById(heroId);
        logger.info("结束根据heroId获取hero数据，hero = {}", hero);
        return hero;
    }

    @ApiOperation("从steam更新hero数据")
    @PostMapping(value = "/api/hero/steam/updateHeroData")
    public void updateHeroData() {
        logger.info("开始从steam更新hero数据");
        steamHeroItemServiceImpl.updateHeroData();
        logger.info("结束从steam更新hero数据");
    }
}
