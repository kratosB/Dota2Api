package com.api;

import com.bean.match.BanPickDetails;
import com.bean.match.LeaguesEntity;
import com.bean.match.Match;
import com.bean.match.MatchesEntity;
import com.dao.entity.Hero;
import com.service.local.IHeroService;
import com.service.local.ILeagueService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 2018/5/17.
 * 
 * @author zhiqiang bao
 */
@RestController
public class LeagueEndpoint {
    
    private ILeagueService leagueServiceImpl;

    private IHeroService heroServiceImpl;
    
    public LeagueEndpoint(ILeagueService leagueServiceImpl, IHeroService heroServiceImpl) {
        this.leagueServiceImpl = leagueServiceImpl;
        this.heroServiceImpl = heroServiceImpl;
    }

    @ApiOperation("列取所有赛事信息")
    @GetMapping(value = "/api/league/listLeague")
    public LeaguesEntity listLeague() {
        return leagueServiceImpl.listLeague();
    }

    @ApiOperation("获取某一项赛事的信息")
    @GetMapping(value = "/api/league/getLeague")
    public MatchesEntity getLeague(@ApiParam(name = "id", required = true) @RequestParam(name = "id") int id) {
        return leagueServiceImpl.getLeague(id);
    }

    @ApiOperation("列取某一项赛事赛事信息_正赛（从某场比赛开始）")
    @GetMapping(value = "/api/league/getLeagueAfter")
    public List<Match> getLeagueAfter(@ApiParam(name = "leagueId", required = true) @RequestParam(name = "leagueId") int leagueId,
                                      @ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        return leagueServiceImpl.getLeagueAfter(leagueId, matchId);
    }

    @ApiOperation("获取赛事信息_正赛（从某场比赛开始）ban pick数据")
    @GetMapping(value = "/api/league/getBanPick")
    public List<BanPickDetails.BanPickHero> getBanPick(
            @ApiParam(name = "leagueId", required = true) @RequestParam(name = "leagueId") int leagueId,
            @ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        BanPickDetails banPickDetails = leagueServiceImpl.getBanPick(leagueId, matchId);
        List<Hero> heroList = heroServiceImpl.listAll();
        banPickDetails.setHeroName(heroList);
        getWinRate(banPickDetails);
        return sort(banPickDetails);
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
