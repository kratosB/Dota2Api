package com.api;

import com.dao.entity.Hero;
import com.service.IHeroService;
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

    private IHeroService heroServiceImpl;

    @Autowired
    public HeroEndpoint(IHeroService heroServiceImpl) {
        this.heroServiceImpl = heroServiceImpl;
    }

    @ApiOperation("获取所有hero")
    @RequestMapping(value = "/api/hero/listAll", method = RequestMethod.GET)
    public List<Hero> listAll() {
        return heroServiceImpl.listAll();
    }

    @ApiOperation("根据id获取hero")
    @RequestMapping(value = "/api/hero/findById", method = RequestMethod.GET)
    public Hero findById(
            @ApiParam(name = "id", required = true, defaultValue = "1") @RequestParam(name = "id", defaultValue = "1") int id) {
        return heroServiceImpl.findById(id);
    }

    @ApiOperation("从steam更新hero数据")
    @PostMapping(value = "/api/hero/updateHeroData")
    public void updateHeroData() {
        heroServiceImpl.updateHeroData();
    }
}
