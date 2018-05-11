package com.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.config.Configuration;
import com.dao.MatchDao;
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

    private HeroService heroService;

    private Configuration configuration;

    private MatchDao matchDao;

    @Autowired
    public MatchService(HeroService heroService, Configuration configuration, MatchDao matchDao) {
        this.heroService = heroService;
        this.configuration = configuration;
        this.matchDao = matchDao;
    }

    public LeaguesEntity listLeague() {
        String getLeague = "GetLeagueListing/";
        String language = "language=zh";
        String getHeroUrl = configuration.getDota2Url() + getLeague + configuration.getApiVersion() + configuration.getApiKey()
                + configuration.getApiAnd() + language;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        return JsonMapper.nonDefaultMapper().fromJson(response, LeaguesEntity.class);
    }

    public MatchesEntity getLeague(int leagueId) {
        String getMatchHistory = "GetMatchHistory/";
        String leagueIdStr = "league_id=" + leagueId;
        String getHeroUrl = configuration.getDota2Url() + getMatchHistory + configuration.getApiVersion()
                + configuration.getApiKey() + configuration.getApiAnd() + leagueIdStr;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        return JsonMapper.nonDefaultMapper().fromJson(response, MatchesEntity.class);
    }

    public String getMatchHistory(GetMatchHistoryReq getMatchHistoryReq) {
        String getMatchHistory = "GetMatchHistory/";
        StringBuilder sb = new StringBuilder();
        sb.append(configuration.getDota2Url()).append(getMatchHistory).append(configuration.getApiVersion())
                .append(configuration.getApiKey());
        if (getMatchHistoryReq.getAccountId() != null) {
            sb.append(configuration.getApiAnd()).append("account_id=").append(getMatchHistoryReq.getAccountId());
        }
        if (getMatchHistoryReq.getGameMode() != null) {
            sb.append(configuration.getApiAnd()).append("game_mode=").append(getMatchHistoryReq.getGameMode());
        }
        if (getMatchHistoryReq.getHeroId() != null) {
            sb.append(configuration.getApiAnd()).append("hero_id=").append(getMatchHistoryReq.getHeroId());
        }
        if (getMatchHistoryReq.getLeagueId() != null) {
            sb.append(configuration.getApiAnd()).append("league_id=").append(getMatchHistoryReq.getLeagueId());
        }
        if (getMatchHistoryReq.getMatchesRequested() != null) {
            sb.append(configuration.getApiAnd()).append("matches_requested=").append(getMatchHistoryReq.getMatchesRequested());
        }
        if (getMatchHistoryReq.getMinPlayers() != null) {
            sb.append(configuration.getApiAnd()).append("min_players=").append(getMatchHistoryReq.getMinPlayers());
        }
        if (getMatchHistoryReq.getSkill() != null) {
            sb.append(configuration.getApiAnd()).append("skill=").append(getMatchHistoryReq.getSkill());
        }
        if (getMatchHistoryReq.getStartAtMatchId() != null) {
            sb.append(configuration.getApiAnd()).append("start_at_match_id=").append(getMatchHistoryReq.getStartAtMatchId());
        }
        if (getMatchHistoryReq.getDateMax() != null) {
            sb.append(configuration.getApiAnd()).append("date_max=").append(getMatchHistoryReq.getDateMax());
        }
        if (getMatchHistoryReq.getDateMin() != null) {
            sb.append(configuration.getApiAnd()).append("date_min=").append(getMatchHistoryReq.getDateMin());
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
        String getHeroUrl = configuration.getDota2Url() + getMatchDetails + configuration.getApiVersion()
                + configuration.getApiKey() + configuration.getApiAnd() + matchIdStr;
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

    public void updateMatchDetail(Long matchId) {
        String getMatchDetails = "GetMatchDetails/";
        String matchIdStr = "match_id=" + matchId;
        String getHeroUrl = configuration.getDota2Url() + getMatchDetails + configuration.getApiVersion()
                + configuration.getApiKey() + configuration.getApiAnd() + matchIdStr;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        JsonNode jsonNode = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        com.dao.entity.Match match = new com.dao.entity.Match();
        match.setMatchId(jsonNode.findValue("match_id").asLong());
        match.setRadiantWin(StringUtils.equals(jsonNode.findValue("radiant_win").asText(), "true") ? 1 : 0);
        match.setDuration(jsonNode.findValue("duration").asInt());
        match.setPreGameDuration(jsonNode.findValue("pre_game_duration").asInt());
        match.setStartTime(jsonNode.findValue("start_time").asLong());
        match.setMatchSeqNum(jsonNode.findValue("match_seq_num").asLong());
        match.setTowerStatusRadiant(jsonNode.findValue("tower_status_radiant").asInt());
        match.setTowerStatusDire(jsonNode.findValue("tower_status_dire").asInt());
        match.setBarracksStatusRadiant(jsonNode.findValue("barracks_status_radiant").asInt());
        match.setBarracksStatusDire(jsonNode.findValue("barracks_status_dire").asInt());
        match.setCluster(jsonNode.findValue("cluster").asInt());
        match.setFirstBloodTime(jsonNode.findValue("first_blood_time").asInt());
        match.setLobbyType(jsonNode.findValue("lobby_type").asInt());
        match.setHumanPlayers(jsonNode.findValue("human_players").asInt());
        match.setLeagueId(jsonNode.findValue("leagueid").asInt());
        match.setPositiveVotes(jsonNode.findValue("positive_votes").asInt());
        match.setNegativeVotes(jsonNode.findValue("negative_votes").asInt());
        match.setGameMode(jsonNode.findValue("game_mode").asInt());
        match.setFlags(jsonNode.findValue("flags").asInt());
        match.setEngine(jsonNode.findValue("engine").asInt());
        match.setRadiantScore(jsonNode.findValue("radiant_score").asInt());
        match.setDireScore(jsonNode.findValue("dire_score").asInt());
        match.setRadiantTeamId(jsonNode.findValue("radiant_team_id").asInt());
        match.setRadiantName(jsonNode.findValue("radiant_name").asText());
        match.setRadiantLogo(jsonNode.findValue("radiant_logo").asLong());
        match.setRadiantTeamComplete(jsonNode.findValue("radiant_team_complete").asInt());
        match.setRadiantCaptain(jsonNode.findValue("radiant_captain").asLong());
        match.setDireTeamId(jsonNode.findValue("dire_team_id").asInt());
        match.setDireName(jsonNode.findValue("dire_name").asText());
        match.setDireLogo(jsonNode.findValue("dire_logo").asLong());
        match.setDireTeamComplete(jsonNode.findValue("dire_team_complete").asInt());
        match.setDireCaptain(jsonNode.findValue("dire_captain").asLong());
        match.setPicksBans(jsonNode.findValue("picks_bans").asText());
        matchDao.save(match);
    }
}
