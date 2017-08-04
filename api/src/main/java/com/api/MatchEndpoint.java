package com.api;

import com.bean.heroitem.Bean;
import com.bean.heroitem.HeroesEntity;
import com.bean.match.*;
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

    @Autowired
    private MatchService matchService;

    @Autowired
    private HeroService heroService;

    @ApiOperation("列取赛事信息")
    @RequestMapping(value = "/api/match/listLeague", method = RequestMethod.GET)
    public LeaguesEntity listLeague() {
        return matchService.listLeague();
    }

    @ApiOperation("列取赛事信息")
    @RequestMapping(value = "/api/match/getLeague", method = RequestMethod.GET)
    public MatchesEntity getLeague(@ApiParam(name = "id", required = true) @RequestParam(name = "id") int id) {
        return matchService.getLeague(id);
    }

    @ApiOperation("列取赛事信息_正赛（从某场比赛开始）")
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
    public List<BanPickDetails.Hero> getBanPick(
            @ApiParam(name = "leagueId", required = true) @RequestParam(name = "leagueId") int leagueId,
            @ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        BanPickDetails banPickDetails = matchService.getBanPick(leagueId, matchId);
        HeroesEntity heroesEntity = heroService.listAll();
        List<Bean> heroes = heroesEntity.getResult().getHeroes();
        banPickDetails.setHeroName(heroes);
        getWinRate(banPickDetails);
        return sort(banPickDetails);
    }

    private List<BanPickDetails.Hero> sort(BanPickDetails banPickDetails) {
        List<BanPickDetails.Hero> heroList = new ArrayList<>();
        HashMap<Integer, BanPickDetails.Hero> heroes = banPickDetails.getHeroes();
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
        HashMap<Integer, BanPickDetails.Hero> heroes = banPickDetails.getHeroes();
        for (int heroId : heroes.keySet()) {
            int winCount = heroes.get(heroId).getWinCount();
            int loseCount = heroes.get(heroId).getLoseCount();
            heroes.get(heroId).setWinRate(winCount / (winCount + loseCount));
        }
    }
}
