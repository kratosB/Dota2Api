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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(LeagueEndpoint.class);

    private ILeagueService leagueServiceImpl;

    private IHeroService heroServiceImpl;

    public LeagueEndpoint(ILeagueService leagueServiceImpl, IHeroService heroServiceImpl) {
        this.leagueServiceImpl = leagueServiceImpl;
        this.heroServiceImpl = heroServiceImpl;
    }

    @ApiOperation("从steam列取所有赛事信息")
    @GetMapping(value = "/api/league/steam/listLeague")
    public LeaguesEntity listLeague() {
        logger.info("开始从steam列取所有赛事信息");
        LeaguesEntity leaguesEntity = leagueServiceImpl.listLeague();
        logger.info("结束从steam列取所有赛事信息");
        return leaguesEntity;
    }

    @ApiOperation("从steam获取某一项赛事的信息")
    @GetMapping(value = "/api/league/steam/getLeague")
    public MatchesEntity getLeague(@ApiParam(name = "leagueId", required = true) @RequestParam(name = "leagueId") int leagueId) {
        logger.info("开始从steam获取某一项赛事的信息，leagueId = {}", leagueId);
        MatchesEntity matchesEntity = leagueServiceImpl.getLeague(leagueId);
        logger.info("结束从steam获取某一项赛事的信息，matchesEntity = {}", matchesEntity);
        return matchesEntity;
    }

    @ApiOperation("从steam列取某一项赛事赛事信息_正赛（从某场比赛开始）")
    @GetMapping(value = "/api/league/steam/getLeagueAfter")
    public List<Match> getLeagueAfter(@ApiParam(name = "leagueId", required = true) @RequestParam(name = "leagueId") int leagueId,
            @ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        logger.info("开始从steam列取某一项赛事赛事信息_正赛（从某场比赛开始），leagueId = {}，matchId = {}", leagueId, matchId);
        List<Match> matchList = leagueServiceImpl.getLeagueAfter(leagueId, matchId);
        logger.info("结束从steam列取某一项赛事赛事信息_正赛（从某场比赛开始），matchList.size = {}", matchList.size());
        return matchList;
    }

    @ApiOperation("从steam获取赛事信息_正赛（从某场比赛开始）ban pick数据")
    @GetMapping(value = "/api/league/steam/getBanPick")
    public List<BanPickDetails.BanPickHero> getBanPick(
            @ApiParam(name = "leagueId", required = true) @RequestParam(name = "leagueId") int leagueId,
            @ApiParam(name = "matchId", required = true) @RequestParam(name = "matchId") long matchId) {
        logger.info("开始从steam获取赛事信息_正赛（从某场比赛开始）ban pick数据，leagueId = {}，matchId = {}", leagueId, matchId);
        BanPickDetails banPickDetails = leagueServiceImpl.getBanPick(leagueId, matchId);
        List<Hero> heroList = heroServiceImpl.listAll();
        banPickDetails.setHeroName(heroList);
        getWinRate(banPickDetails);
        List<BanPickDetails.BanPickHero> banPickHeroList = sort(banPickDetails);
        logger.info("结束从steam获取赛事信息_正赛（从某场比赛开始）ban pick数据，leagueId = {}，matchId = {}", leagueId, matchId);
        return banPickHeroList;
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
