package com.service.impl;

import com.bean.match.BanPickDetails;
import com.bean.match.BanPicks;
import com.bean.match.LeaguesEntity;
import com.bean.match.Match;
import com.bean.match.MatchDetail;
import com.bean.match.MatchesEntity;
import com.bean.match.MatchesHistory;
import com.config.Configuration;
import com.service.ILeagueService;
import com.service.IMatchService;
import com.util.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2018/5/17.
 * 
 * @author zhiqiang bao
 */
@Service
public class LeagueServiceImpl implements ILeagueService {

    private Configuration configuration;

    private IMatchService matchServiceImpl;

    private String stringTrue = "true";

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public LeagueServiceImpl(Configuration configuration, IMatchService matchServiceImpl) {
        this.configuration = configuration;
        this.matchServiceImpl = matchServiceImpl;
    }

    @Override
    public LeaguesEntity listLeague() {
        String getLeague = "GetLeagueListing/";
        String language = "language=zh";
        String getHeroUrl = configuration.getIDota2Url() + getLeague + configuration.getApiVersion() + configuration.getApiKey()
                + configuration.getApiAnd() + language;
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        return JsonMapper.nonDefaultMapper().fromJson(response, LeaguesEntity.class);
    }

    @Override
    public MatchesEntity getLeague(int leagueId) {
        String getMatchHistory = "GetMatchHistory/";
        String leagueIdStr = "league_id=" + leagueId;
        String getHeroUrl = configuration.getIDota2Url() + getMatchHistory + configuration.getApiVersion()
                + configuration.getApiKey() + configuration.getApiAnd() + leagueIdStr;
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        return JsonMapper.nonDefaultMapper().fromJson(response, MatchesEntity.class);
    }

    @Override
    public List<Match> getLeagueAfter(int leagueId, long matchId) {
        MatchesEntity entity = getLeague(leagueId);
        MatchesHistory matchHistory = entity.getResult();
        List<Match> matches = matchHistory.getMatches();
        List<Match> matchList = matches.stream().filter(match -> match.getMatch_id() == matchId).collect(Collectors.toList());
        Match firstMatch = matchList.get(0);
        return matches.stream().filter(match -> match.getMatch_id() >= firstMatch.getMatch_id()).collect(Collectors.toList());
    }

    @Override
    public BanPickDetails getBanPick(int leagueId, long matchId) {
        BanPickDetails banPickDetails = new BanPickDetails();
        List<Match> matches = getLeagueAfter(leagueId, matchId);
        List<Long> matchIds = new ArrayList<>();
        matches.forEach(match -> matchIds.add(match.getMatch_id()));
        matchIds.forEach(matchId1 -> {
            MatchDetail matchDetail = matchServiceImpl.getMatchDetail(matchId1);
            List<BanPicks> banPickList = matchDetail.getPicks_bans();
            String isRadiantWin = matchDetail.getRadiant_win();
            if (StringUtils.equals(isRadiantWin, stringTrue)) {
                // team 0 win
                banPickWin(banPickDetails, banPickList, 0);
            } else {
                // team 1 win
                banPickWin(banPickDetails, banPickList, 1);
            }
        });
        return banPickDetails;
    }

    private void banPickWin(BanPickDetails banPickDetails, List<BanPicks> banPickList, int winTeamId) {
        banPickList.forEach(banPick -> {
            String isPick = banPick.getIs_pick();
            if (StringUtils.equals(isPick, stringTrue)) {
                banPickDetails.pickHero(banPick.getHero_id());
                if (banPick.getTeam() == winTeamId) {
                    banPickDetails.addWinCount(banPick.getHero_id());
                } else {
                    banPickDetails.addLoseCount(banPick.getHero_id());
                }
            } else {
                banPickDetails.banHero(banPick.getHero_id());
            }
        });
    }
}
