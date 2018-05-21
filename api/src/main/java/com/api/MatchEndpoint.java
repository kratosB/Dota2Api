package com.api;

import com.api.req.GetMatchHistoryReq;
import com.bean.match.MatchDetail;
import com.config.Configuration;
import com.service.local.IMatchService;
import com.service.steam.ISteamMatchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    private IMatchService matchServiceImpl;

    private ISteamMatchService steamMatchServiceImpl;

    private Configuration configuration;

    @Autowired
    public MatchEndpoint(Configuration configuration, IMatchService matchServiceImpl, ISteamMatchService steamMatchServiceImpl) {
        this.configuration = configuration;
        this.matchServiceImpl = matchServiceImpl;
        this.steamMatchServiceImpl = steamMatchServiceImpl;
    }

    @ApiOperation("获取比赛历史")
    @PostMapping(value = "/api/match/getMatchHistory")
    public String getMatchHistory(@RequestBody GetMatchHistoryReq getMatchHistoryReq) {
        return steamMatchServiceImpl.getMatchHistory(getMatchHistoryReq);
    }

    @ApiOperation("根据比赛id，更新比赛详情")
    @GetMapping(value = "/api/match/updateMatchDetailByMatchId")
    public void updateMatchDetailByMatchId(@RequestParam Long matchId) {
        matchServiceImpl.updateMatchDetailByMatchId(matchId);
    }

    @ApiOperation("根据比赛steamId，批量更新比赛详情")
    @GetMapping(value = "/api/match/updateMatchDetail")
    public void updateMatchDetail(@ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        matchServiceImpl.updateMatchDetail(steamId);
    }

    // http://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/v1/?key=EFA1E81676FCC47157EA871A67741EF5&account_id=76561198088256001&hero_id=71&start_at_match_id=1848644028

    @ApiOperation("获取某场比赛的具体信息")
    @GetMapping(value = "/api/match/getMatchDetail")
    public MatchDetail getMatchDetail(@ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        return steamMatchServiceImpl.getMatchDetail(matchId);
    }

    @ApiOperation("根据英雄id获取所有比赛的比赛id")
    @GetMapping(value = "/api/match/getMatchIdBySteamIdAndHeroId")
    public List<Long> getMatchIdBySteamIdAndHeroId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId,
            @ApiParam(name = "heroId") @RequestParam(name = "heroId", required = false) int heroId) {
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        return matchServiceImpl.getMatchIdBySteamIdAndHeroId(steamId, heroId, null);
    }

}
