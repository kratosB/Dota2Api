package com.api;

import com.api.req.GetMatchHistoryReq;
import com.bean.match.MatchDetail;
import com.config.Configuration;
import com.service.local.IMatchService;
import com.service.steam.ISteamMatchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created on 2017/06/14.
 * 
 * @author zhiqiang bao
 */
@RestController
public class MatchEndpoint {

    private Logger logger = LoggerFactory.getLogger(MatchEndpoint.class);

    private IMatchService matchServiceImpl;

    private ISteamMatchService steamMatchServiceImpl;

    private Configuration configuration;

    @Autowired
    public MatchEndpoint(Configuration configuration, IMatchService matchServiceImpl, ISteamMatchService steamMatchServiceImpl) {
        this.configuration = configuration;
        this.matchServiceImpl = matchServiceImpl;
        this.steamMatchServiceImpl = steamMatchServiceImpl;
    }

    @ApiOperation("从steam获取比赛历史")
    @PostMapping(value = "/api/match/steam/getMatchHistory")
    public String getMatchHistory(@RequestBody GetMatchHistoryReq getMatchHistoryReq) {
        logger.info("开始从steam获取比赛历史，getMatchHistoryReq = {}", getMatchHistoryReq);
        String matchHistory = steamMatchServiceImpl.getMatchHistory(getMatchHistoryReq);
        logger.info("结束从steam获取比赛历史，matchHistory.length = {}", matchHistory.length());
        return matchHistory;
    }

    @ApiOperation("从steam，根据比赛id，更新比赛详情")
    @GetMapping(value = "/api/match/steam/updateMatchDetailByMatchId")
    public void updateMatchDetailByMatchId(@RequestParam Long matchId) {
        logger.info("开始从steam，根据比赛id，更新比赛详情，matchId = {}", matchId);
        matchServiceImpl.updateMatchDetailByMatchId(matchId);
        logger.info("结束从steam，根据比赛id，更新比赛详情，matchId = {}", matchId);
    }

    @ApiOperation("从steam，根据比赛steamId，批量更新比赛详情")
    @GetMapping(value = "/api/match/steam/updateMatchDetail")
    public void updateMatchDetail(@ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        logger.info("开始从steam，根据比赛steamId，批量更新比赛详情，steamId = {}", steamId);
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        matchServiceImpl.updateMatchDetail(steamId);
        logger.info("结束从steam，根据比赛steamId，批量更新比赛详情，steamId = {}", steamId);
    }

    // http://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/v1/?key=EFA1E81676FCC47157EA871A67741EF5&account_id=76561198088256001&hero_id=71&start_at_match_id=1848644028

    @ApiOperation("从steam获取某场比赛的具体信息")
    @GetMapping(value = "/api/match/steam/getMatchDetail")
    public MatchDetail getMatchDetail(@ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        logger.info("开始从steam获取某场比赛的具体信息，matchId = {}", matchId);
        MatchDetail matchDetail = steamMatchServiceImpl.getMatchDetail(matchId);
        logger.info("结束从steam获取某场比赛的具体信息，MatchDetail = {}", matchDetail);
        return matchDetail;
    }

    @ApiOperation("从steam，根据英雄id获取所有比赛的比赛id")
    @GetMapping(value = "/api/match/steam/getMatchIdBySteamIdAndHeroId")
    public List<Long> getMatchIdBySteamIdAndHeroId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId,
            @ApiParam(name = "heroId") @RequestParam(name = "heroId", required = false) int heroId) {
        logger.info("开始从steam，根据英雄id获取所有比赛的比赛id，steamId = {}，heroId = {}", steamId, heroId);
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        List<Long> matchIdList = matchServiceImpl.getMatchIdBySteamIdAndHeroId(steamId, heroId, null);
        logger.info("结束从steam，根据英雄id获取所有比赛的比赛id，matchIdList.size = {}", matchIdList.size());
        return matchIdList;
    }

}
