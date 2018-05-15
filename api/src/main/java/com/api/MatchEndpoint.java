package com.api;

import com.api.req.GetMatchHistoryReq;
import com.bean.match.*;
import com.config.Configuration;
import com.dao.entity.Hero;
import com.service.HeroService;
import com.service.MatchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 2017/06/14.
 */
@RestController
public class MatchEndpoint {

    private MatchService matchService;

    private Configuration configuration;

    private HeroService heroService;

    @Autowired
    public MatchEndpoint(Configuration configuration, MatchService matchService, HeroService heroService) {
        this.configuration = configuration;
        this.matchService = matchService;
        this.heroService = heroService;
    }

    @ApiOperation("列取所有赛事信息")
    @RequestMapping(value = "/api/match/listLeague", method = RequestMethod.GET)
    public LeaguesEntity listLeague() {
        return matchService.listLeague();
    }

    @ApiOperation("获取某一项赛事的信息")
    @RequestMapping(value = "/api/match/getLeague", method = RequestMethod.GET)
    public MatchesEntity getLeague(@ApiParam(name = "id", required = true) @RequestParam(name = "id") int id) {
        return matchService.getLeague(id);
    }

    @ApiOperation("获取比赛历史")
    @RequestMapping(value = "/api/match/getMatchHistory", method = RequestMethod.POST)
    public String getMatchHistory(@RequestBody GetMatchHistoryReq getMatchHistoryReq) {
        return matchService.getMatchHistory(getMatchHistoryReq);
    }

    @ApiOperation("根据比赛id，更新比赛详情")
    @RequestMapping(value = "/api/match/updateMatchDetail", method = RequestMethod.GET)
    public void updateMatchDetail(@RequestParam Long matchId) {
        matchService.updateMatchDetail(matchId);
    }

    // http://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/v1/?key=EFA1E81676FCC47157EA871A67741EF5&account_id=76561198088256001&hero_id=71&start_at_match_id=1848644028

    @ApiOperation("列取某一项赛事赛事信息_正赛（从某场比赛开始）")
    @RequestMapping(value = "/api/match/getLeagueAfter", method = RequestMethod.GET)
    public List<Match> getLeagueAfter(@ApiParam(name = "leagueId", required = true) @RequestParam(name = "leagueId") int leagueId,
            @ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        return matchService.getLeagueAfter(leagueId, matchId);
    }

    @ApiOperation("获取某场比赛的具体信息")
    @RequestMapping(value = "/api/match/getMatchDetail", method = RequestMethod.GET)
    public MatchDetail getMatchDetail(@ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        return matchService.getMatchDetail(matchId);
    }

    @ApiOperation("获取赛事信息_正赛（从某场比赛开始）ban pick数据")
    @RequestMapping(value = "/api/match/getBanPick", method = RequestMethod.GET)
    public List<BanPickDetails.BanPickHero> getBanPick(
            @ApiParam(name = "leagueId", required = true) @RequestParam(name = "leagueId") int leagueId,
            @ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        BanPickDetails banPickDetails = matchService.getBanPick(leagueId, matchId);
        List<Hero> heroList = heroService.listAll();
        banPickDetails.setHeroName(heroList);
        getWinRate(banPickDetails);
        return sort(banPickDetails);
    }

    @ApiOperation("根据英雄id获取所有比赛的比赛id")
    @RequestMapping(value = "/api/match/getMatchHistoryByAllHero", method = RequestMethod.GET)
    public String getMatchHistoryByAllHero(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        return matchService.getMatchHistoryByAllHero(steamId);
    }

    @ApiOperation("根据英雄id获取所有比赛的比赛id")
    @RequestMapping(value = "/api/match/getMatchIdBySteamIdAndHeroId", method = RequestMethod.GET)
    public List<Long> getMatchIdBySteamIdAndHeroId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId,
            @ApiParam(name = "heroId") @RequestParam(name = "heroId", required = false) int heroId) {
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        return matchService.getMatchIdBySteamIdAndHeroId(steamId, heroId, null);
    }

    private List<BanPickDetails.BanPickHero> sort(BanPickDetails banPickDetails) {
        List<BanPickDetails.BanPickHero> heroList = new ArrayList<>();
        HashMap<Integer, BanPickDetails.BanPickHero> heroes = banPickDetails.getHeroes();
        while (!heroes.keySet().isEmpty()) {
            int maxCount = 0;
            int index = 0;
            for (Integer key : heroes.keySet()) {
                if (heroes.get(key).getTotalCount() > maxCount) {
                    maxCount = heroes.get(key).getTotalCount();
                    index = key;
                }
            }
            heroList.add(heroes.get(index));
            heroes.remove(index);
        }
        return heroList;
    }

    private void getWinRate(BanPickDetails banPickDetails) {
        HashMap<Integer, BanPickDetails.BanPickHero> heroes = banPickDetails.getHeroes();
        for (int heroId : heroes.keySet()) {
            int winCount = heroes.get(heroId).getWinCount();
            int loseCount = heroes.get(heroId).getLoseCount();
            if (winCount + loseCount == 0) {
                System.out.println(heroes.get(heroId).getHeroName());
            } else {
                heroes.get(heroId).setWinRate(winCount / (winCount + loseCount));
            }
        }
    }
}
