package com.api;

import com.bean.heroitem.HeroesEntity;
import com.dao.CustomerDao;
import com.entity.Customer;
import com.service.HeroService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created on 2017/06/14.
 */
@RestController
public class HeroEndpoint {

    @Value("${env}")
    private String str;

    @Autowired
    private HeroService heroService;

    @Autowired
    private CustomerDao customerDao;

    @ApiOperation("获取所有hero名称")
    @RequestMapping(value = "/api/hero/listAll", method = RequestMethod.GET)
    public HeroesEntity listAll() {
        return heroService.listAll();
    }

    @ApiOperation("根据id获取hero名称")
    @RequestMapping(value = "/api/hero/listById", method = RequestMethod.GET)
    public String listById(
            @ApiParam(name = "id", required = true, defaultValue = "1") @RequestParam(name = "id", defaultValue = "1") int id) {
        return heroService.listById(id);
    }

    @ApiOperation("health")
    @RequestMapping(value = "/api/health", method = RequestMethod.GET)
    public String health() {
        return str;
    }

    @ApiOperation("listCustomer")
    @RequestMapping(value = "/api/listCustomer", method = RequestMethod.GET)
    public Customer listCustomer() {
        return customerDao.findOne("C0000000641");
    }
}
