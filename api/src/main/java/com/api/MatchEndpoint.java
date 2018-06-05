package com.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.req.GetMatchHistoryReq;
import com.config.Config;
import com.service.local.IMatchService;
import com.service.steam.ISteamMatchService;
import com.util.SteamIdConverter;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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

    private Config config;

    @Autowired
    public MatchEndpoint(Config config, IMatchService matchServiceImpl, ISteamMatchService steamMatchServiceImpl) {
        this.config = config;
        this.matchServiceImpl = matchServiceImpl;
        this.steamMatchServiceImpl = steamMatchServiceImpl;
    }

    @ApiOperation("从steam，根据比赛id，更新比赛详情")
    @GetMapping(value = "/api/match/steam/updateMatchDetailByMatchId")
    public void updateMatchDetailByMatchId(@RequestParam Long matchId) {
        logger.info("开始从steam，根据比赛id，更新比赛详情，matchId = {}", matchId);
        matchServiceImpl.updateMatchDetailByMatchId(matchId);
        logger.info("结束从steam，根据比赛id，更新比赛详情，matchId = {}", matchId);
    }

    @ApiOperation("从steam，根据steamId，批量更新比赛Id")
    @GetMapping(value = "/api/match/steam/updateMatchIdsBySteamId")
    public void updateMatchIdsBySteamId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        logger.info("开始从steam，根据比赛steamId，批量更新比赛Id，steamId = {}", steamId);
        if (steamId == null) {
            steamId = config.getAdminSteamId();
        } else {
            steamId = SteamIdConverter.defaultInstance().getId64(steamId);
        }
        matchServiceImpl.updateMatchIdsBySteamId(steamId);
        logger.info("结束从steam，根据比赛steamId，批量更新比赛Id，steamId = {}", steamId);
    }

    @ApiOperation("从steam，根据leagueId和正赛第一场matchId，批量更新比赛Id")
    @GetMapping(value = "/api/match/steam/updateMatchIdsByLeagueId")
    public void updateMatchIdsByLeagueId(
            @ApiParam(name = "leagueId", required = true) @RequestParam(name = "leagueId") int leagueId,
            @ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        logger.info("开始从steam，根据leagueId和正赛第一场matchId，批量更新比赛Id，leagueId = {}", leagueId);
        matchServiceImpl.updateMatchIdsByLeagueId(leagueId, matchId);
        logger.info("结束从steam，根据leagueId和正赛第一场matchId，批量更新比赛Id，leagueId = {}", leagueId);
    }

    @ApiOperation("从steam获取比赛历史")
    @PostMapping(value = "/api/match/steam/getMatchHistory")
    public String getMatchHistory(@ModelAttribute GetMatchHistoryReq getMatchHistoryReq) {
        logger.info("开始从steam获取比赛历史，getMatchHistoryReq = {}", getMatchHistoryReq);
        String matchHistory = steamMatchServiceImpl.getMatchHistory(getMatchHistoryReq);
        logger.info("结束从steam获取比赛历史，matchHistory.length = {}", matchHistory.length());
        return matchHistory;
    }

    @ApiOperation("从steam获取某场比赛的具体信息")
    @GetMapping(value = "/api/match/steam/getMatchDetailByMatchId")
    public String getMatchDetailByMatchId(
            @ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        logger.info("开始从steam获取某场比赛的具体信息，matchId = {}", matchId);
        String matchDetail = steamMatchServiceImpl.getMatchDetailByMatchId(matchId);
        logger.info("结束从steam获取某场比赛的具体信息，MatchDetail.length() = {}", matchDetail.length());
        return matchDetail;
    }

}
