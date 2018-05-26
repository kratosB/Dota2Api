package com.job;

import com.service.local.IMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 用job是因为怕短时间内调用steam的接口的次数太多，导致key被锁 Created on 2018/5/22.
 * 
 * @author zhiqiang bao
 */
@Service
public class Dota2Job {

    private IMatchService matchServiceImpl;

    @Autowired
    public Dota2Job(IMatchService matchServiceImpl) {
        this.matchServiceImpl = matchServiceImpl;
    }

    @Scheduled(fixedRate = 30000)
    public void updateMatchDetail() {
        matchServiceImpl.updateMatchDetailJob();
    }

}
