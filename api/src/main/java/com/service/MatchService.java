package com.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.api.req.GetMatchHistoryReq;
import com.bean.match.BanPickDetails;
import com.bean.match.BanPicks;
import com.bean.match.LeaguesEntity;
import com.bean.match.Match;
import com.bean.match.MatchDetail;
import com.bean.match.MatchDetailEntity;
import com.bean.match.MatchesEntity;
import com.bean.match.MatchesHistory;
import com.dao.entity.Hero;
import com.fasterxml.jackson.databind.JsonNode;
import com.util.JsonMapper;

/**
 * Created on 2017/06/15.
 * 
 * @author zhiqiang bao
 */
@Service
public class MatchService {

    private String steamUrl = "http://api.steampowered.com/IDOTA2Match_570/";

    private String version = "v1/";

    private String key = "?key=EFA1E81676FCC47157EA871A67741EF5";

    private String str2 = "&";

    private HeroService heroService;

    @Autowired
    public MatchService(HeroService heroService) {
        this.heroService = heroService;
    }

    public LeaguesEntity listLeague() {
        String getLeague = "GetLeagueListing/";
        String language = "language=zh";
        String getHeroUrl = steamUrl + getLeague + version + key + str2 + language;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        return JsonMapper.nonDefaultMapper().fromJson(response, LeaguesEntity.class);
    }

    public MatchesEntity getLeague(int leagueId) {
        String getMatchHistory = "GetMatchHistory/";
        String leagueIdStr = "league_id=" + leagueId;
        String getHeroUrl = steamUrl + getMatchHistory + version + key + str2 + leagueIdStr;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        return JsonMapper.nonDefaultMapper().fromJson(response, MatchesEntity.class);
    }

    public String getMatchHistory(GetMatchHistoryReq getMatchHistoryReq) {
        String getMatchHistory = "GetMatchHistory/";
        StringBuilder sb = new StringBuilder();
        sb.append(steamUrl).append(getMatchHistory).append(version).append(key);
        if (getMatchHistoryReq.getAccountId() != null) {
            sb.append(str2).append("account_id=").append(getMatchHistoryReq.getAccountId());
        }
        if (getMatchHistoryReq.getGameMode() != null) {
            sb.append(str2).append("game_mode=").append(getMatchHistoryReq.getGameMode());
        }
        if (getMatchHistoryReq.getHeroId() != null) {
            sb.append(str2).append("hero_id=").append(getMatchHistoryReq.getHeroId());
        }
        if (getMatchHistoryReq.getLeagueId() != null) {
            sb.append(str2).append("league_id=").append(getMatchHistoryReq.getLeagueId());
        }
        if (getMatchHistoryReq.getMatchesRequested() != null) {
            sb.append(str2).append("matches_requested=").append(getMatchHistoryReq.getMatchesRequested());
        }
        if (getMatchHistoryReq.getMinPlayers() != null) {
            sb.append(str2).append("min_players=").append(getMatchHistoryReq.getMinPlayers());
        }
        if (getMatchHistoryReq.getSkill() != null) {
            sb.append(str2).append("skill=").append(getMatchHistoryReq.getSkill());
        }
        if (getMatchHistoryReq.getStartAtMatchId() != null) {
            sb.append(str2).append("start_at_match_id=").append(getMatchHistoryReq.getStartAtMatchId());
        }
        if (getMatchHistoryReq.getDateMax() != null) {
            sb.append(str2).append("date_max=").append(getMatchHistoryReq.getDateMax());
        }
        if (getMatchHistoryReq.getDateMin() != null) {
            sb.append(str2).append("date_min=").append(getMatchHistoryReq.getDateMin());
        }
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(sb.toString(), String.class);
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
        return matchDetailEntity.getResult();
    }

    public String getMatchHistoryByAllHero(String steamId) {
        StringBuilder sb = new StringBuilder();
        List<Hero> heroList = heroService.listAll();
        List<Integer> heroIdList = heroList.stream().map(Hero::getId).collect(Collectors.toList());
        heroIdList.forEach(heroId -> {
            GetMatchHistoryReq getMatchHistoryReq = new GetMatchHistoryReq();
            getMatchHistoryReq.setAccountId(steamId);
            getMatchHistoryReq.setHeroId(Long.valueOf(heroId));
            String result = getMatchHistory(getMatchHistoryReq);
            JsonNode resultNode = JsonMapper.nonDefaultMapper().fromJson(result, JsonNode.class);
            for (JsonNode node : resultNode.findPath("matches")) {
                sb.append(node.findValuesAsText("match_id")).append(",");
            }
        });
        return sb.toString();
    }

    public BanPickDetails getBanPick(int leagueId, long matchId) {
        BanPickDetails banPickDetails = new BanPickDetails();
        List<Match> matches = getLeagueAfter(leagueId, matchId);
        List<Long> matchIds = new ArrayList<>();
        matches.forEach(match -> matchIds.add(match.getMatch_id()));
        matchIds.forEach(matchId1 -> {
            MatchDetail matchDetail = getMatchDetail(matchId1);
            List<BanPicks> banPickList = matchDetail.getPicks_bans();
            String isRadiantWin = matchDetail.getRadiant_win();
            if (StringUtils.equals(isRadiantWin, "true")) {
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
            if (StringUtils.equals(isPick, "true")) {
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
