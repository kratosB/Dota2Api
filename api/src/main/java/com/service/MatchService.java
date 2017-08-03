package com.service;

import com.bean.match.*;
import com.util.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2017/06/15.
 */
@Service
public class MatchService {

    private String steamUrl = "http://api.steampowered.com/IDOTA2Match_570/";

    private String version = "v1/";

    private String key = "?key=EFA1E81676FCC47157EA871A67741EF5";

    private String str2 = "&";

    public LeaguesEntity listLeague() {
        String getLeague = "GetLeagueListing/";
        String language = "language=zh";
        String getHeroUrl = steamUrl + getLeague + version + key + str2 + language;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        LeaguesEntity leaguesEntity = JsonMapper.nonDefaultMapper().fromJson(response, LeaguesEntity.class);
        return leaguesEntity;
    }

    public MatchesEntity getLeague(int leagueId) {
        String getMatchHistory = "GetMatchHistory/";
        String leagueIdStr = "league_id=" + leagueId;
        String getHeroUrl = steamUrl + getMatchHistory + version + key + str2 + leagueIdStr;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        MatchesEntity matchesEntity = JsonMapper.nonDefaultMapper().fromJson(response, MatchesEntity.class);
        return matchesEntity;
    }

    public List<Match> getLeagueAfter(int leagueId, long matchId) {
        MatchesEntity entity = getLeague(leagueId);
        MatchesHistory matchHistory = entity.getResult();
        List<Match> matches = matchHistory.getMatches();
        List<Match> matchList = matches.stream().filter(match -> match.getMatch_id() == matchId).collect(Collectors.toList());
        Match firstMatch = matchList.get(0);
        return matches.stream().filter(match -> match.getMatch_id() >= firstMatch.getMatch_id()).collect(Collectors.toList());
    }

    public MatchDetail getMatchDetail(long matchId) {
        String getMatchDetails = "GetMatchDetails/";
        String matchIdStr = "match_id=" + matchId;
        String getHeroUrl = steamUrl + getMatchDetails + version + key + str2 + matchIdStr;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        MatchDetailEntity matchDetailEntity = JsonMapper.nonDefaultMapper().fromJson(response, MatchDetailEntity.class);
        MatchDetail matchDetail = matchDetailEntity.getResult();
        return matchDetail;
    }

    public BanPickDetails getBanPick(int leagueId, long matchId) {
        BanPickDetails banPickDetails = new BanPickDetails();
        List<Match> matches = getLeagueAfter(leagueId, matchId);
        List<Long> matchIds = new ArrayList<>();
        matches.stream().forEach(match -> {
            matchIds.add(match.getMatch_id());
        });
        matchIds.stream().forEach(match_Id -> {
            MatchDetail matchDetail = getMatchDetail(match_Id);
            List<BanPicks> banPickList = matchDetail.getPicks_bans();
            banPickList.stream().forEach(banPick -> {
                String isPick = banPick.getIs_pick();
                if (StringUtils.equals(isPick, "true")) {
                    banPickDetails.pickHero(banPick.getHero_id());
                } else {
                    banPickDetails.banHero(banPick.getHero_id());
                }
            });
        });
        return banPickDetails;
    }

}
