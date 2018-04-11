package com.api;

import com.dao.entity.Hero;
import com.service.HeroService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created on 2017/06/14.
 *
 * @author zhiqiang bao
 */
@RestController
public class HeroEndpoint {

    private HeroService heroService;

    @Autowired
    public HeroEndpoint(HeroService heroService) {
        this.heroService = heroService;
    }

    @ApiOperation("获取所有hero名称")
    @RequestMapping(value = "/api/hero/listAll", method = RequestMethod.GET)
    public List<Hero> listAll() {
        return heroService.listAll();
    }

    @ApiOperation("根据id获取hero名称")
    @RequestMapping(value = "/api/hero/listById", method = RequestMethod.GET)
    public Hero listById(
            @ApiParam(name = "id", required = true, defaultValue = "1") @RequestParam(name = "id", defaultValue = "1") int id) {
        return heroService.listById(id);
    }

    @ApiOperation("更新hero数据")
    @PostMapping(value = "/api/hero/updateHeroData")
    public void updateHeroData() {
        heroService.updateHeroData();
    }
}
