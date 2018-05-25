package com.job;

import com.config.Config;
import com.service.local.IMatchService;
import com.util.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用job是因为怕短时间内调用steam的接口的次数太多，导致key被锁 Created on 2018/5/22.
 * 
 * @author zhiqiang bao
 */
@Service
public class Dota2Job {

    private IMatchService matchServiceImpl;

    private Config config;

    @Autowired
    public Dota2Job(IMatchService matchServiceImpl,Config config) {
        this.matchServiceImpl = matchServiceImpl;
        this.config = config;
    }

    @Scheduled(fixedRate = 30000)
    public void updateMatchDetail() {
        matchServiceImpl.updateMatchDetailJob();
    }

    @Scheduled(fixedRate = 1000000)
    public void enableServiceAvailable() {
        config.setServiceAvailable(true);
    }

}
