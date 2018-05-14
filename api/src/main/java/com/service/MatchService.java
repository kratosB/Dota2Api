package com.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.config.Configuration;
import com.dao.MatchHistoryDao;
import com.dao.MatchPlayerDao;
import com.dao.entity.MatchHistory;
import com.dao.entity.MatchPlayer;
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

    private MatchHistoryDao matchHistoryDao;

    private MatchPlayerDao matchPlayerDao;

    @Autowired
    public MatchService(HeroService heroService, Configuration configuration, MatchHistoryDao matchHistoryDao,
            MatchPlayerDao matchPlayerDao) {
        this.heroService = heroService;
        this.configuration = configuration;
        this.matchHistoryDao = matchHistoryDao;
        this.matchPlayerDao = matchPlayerDao;
    }

    public LeaguesEntity listLeague() {
        String getLeague = "GetLeagueListing/";
        String language = "language=zh";
        String getHeroUrl = configuration.getIDota2Url() + getLeague + configuration.getApiVersion() + configuration.getApiKey()
                + configuration.getApiAnd() + language;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        return JsonMapper.nonDefaultMapper().fromJson(response, LeaguesEntity.class);
    }

    public MatchesEntity getLeague(int leagueId) {
        String getMatchHistory = "GetMatchHistory/";
        String leagueIdStr = "league_id=" + leagueId;
        String getHeroUrl = configuration.getIDota2Url() + getMatchHistory + configuration.getApiVersion()
                + configuration.getApiKey() + configuration.getApiAnd() + leagueIdStr;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        return JsonMapper.nonDefaultMapper().fromJson(response, MatchesEntity.class);
    }

    public String getMatchHistory(GetMatchHistoryReq getMatchHistoryReq) {
        String getMatchHistory = "GetMatchHistory/";
        StringBuilder sb = new StringBuilder();
        sb.append(configuration.getIDota2Url()).append(getMatchHistory).append(configuration.getApiVersion())
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
        String getHeroUrl = configuration.getIDota2Url() + getMatchDetails + configuration.getApiVersion()
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
        String getHeroUrl = configuration.getIDota2Url() + getMatchDetails + configuration.getApiVersion()
                + configuration.getApiKey() + configuration.getApiAnd() + matchIdStr;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        JsonNode jsonNode = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        MatchHistory matchHistory = convertMatchNodeToMatchHistory(jsonNode);
        matchHistoryDao.save(matchHistory);
        JsonNode playersNode = jsonNode.findPath("players");
        Iterator<JsonNode> playerNodeList = playersNode.iterator();
        List<MatchPlayer> matchPlayerList = new ArrayList<>();
        playerNodeList.forEachRemaining(node -> {
            MatchPlayer matchPlayer = convertMatchPlayerNodeToMatchPlayer(node);
            matchPlayer.setMatchId(matchId);
            matchPlayerList.add(matchPlayer);
        });
        matchPlayerDao.save(matchPlayerList);
    }

    private MatchHistory convertMatchNodeToMatchHistory(JsonNode matchNode) {
        MatchHistory matchHistory = new MatchHistory();
        matchHistory.setMatchId(matchNode.findValue("match_id").asLong());
        matchHistory.setRadiantWin(StringUtils.equals(matchNode.findValue("radiant_win").asText(), "true") ? 1 : 0);
        matchHistory.setDuration(matchNode.findValue("duration").asInt());
        matchHistory.setPreGameDuration(matchNode.findValue("pre_game_duration").asInt());
        matchHistory.setStartTime(matchNode.findValue("start_time").asLong());
        matchHistory.setMatchSeqNum(matchNode.findValue("match_seq_num").asLong());
        matchHistory.setTowerStatusRadiant(matchNode.findValue("tower_status_radiant").asInt());
        matchHistory.setTowerStatusDire(matchNode.findValue("tower_status_dire").asInt());
        matchHistory.setBarracksStatusRadiant(matchNode.findValue("barracks_status_radiant").asInt());
        matchHistory.setBarracksStatusDire(matchNode.findValue("barracks_status_dire").asInt());
        matchHistory.setCluster(matchNode.findValue("cluster").asInt());
        matchHistory.setFirstBloodTime(matchNode.findValue("first_blood_time").asInt());
        matchHistory.setLobbyType(matchNode.findValue("lobby_type").asInt());
        matchHistory.setHumanPlayers(matchNode.findValue("human_players").asInt());
        matchHistory.setLeagueId(matchNode.findValue("leagueid").asInt());
        matchHistory.setPositiveVotes(matchNode.findValue("positive_votes").asInt());
        matchHistory.setNegativeVotes(matchNode.findValue("negative_votes").asInt());
        matchHistory.setGameMode(matchNode.findValue("game_mode").asInt());
        matchHistory.setFlags(matchNode.findValue("flags").asInt());
        matchHistory.setEngine(matchNode.findValue("engine").asInt());
        matchHistory.setRadiantScore(matchNode.findValue("radiant_score").asInt());
        matchHistory.setDireScore(matchNode.findValue("dire_score").asInt());
        matchHistory.setRadiantTeamId(matchNode.findValue("radiant_team_id").asInt());
        matchHistory.setRadiantName(matchNode.findValue("radiant_name").asText());
        matchHistory.setRadiantLogo(matchNode.findValue("radiant_logo").asLong());
        matchHistory.setRadiantTeamComplete(matchNode.findValue("radiant_team_complete").asInt());
        matchHistory.setRadiantCaptain(matchNode.findValue("radiant_captain").asLong());
        matchHistory.setDireTeamId(matchNode.findValue("dire_team_id").asInt());
        matchHistory.setDireName(matchNode.findValue("dire_name").asText());
        matchHistory.setDireLogo(matchNode.findValue("dire_logo").asLong());
        matchHistory.setDireTeamComplete(matchNode.findValue("dire_team_complete").asInt());
        matchHistory.setDireCaptain(matchNode.findValue("dire_captain").asLong());
        matchHistory.setPicksBans(JsonMapper.nonDefaultMapper().toJson(matchNode.findValue("picks_bans")));
        return matchHistory;
    }

    private MatchPlayer convertMatchPlayerNodeToMatchPlayer(JsonNode matchPlayerNode) {
        MatchPlayer matchPlayer = new MatchPlayer();
        matchPlayer.setAccountId(matchPlayerNode.findValue("account_id").asLong());
        matchPlayer.setPlayerSlot(matchPlayerNode.findValue("player_slot").asInt());
        matchPlayer.setHeroId(matchPlayerNode.findValue("hero_id").asInt());
        matchPlayer.setItem0(matchPlayerNode.findValue("item_0").asInt());
        matchPlayer.setItem1(matchPlayerNode.findValue("item_1").asInt());
        matchPlayer.setItem2(matchPlayerNode.findValue("item_2").asInt());
        matchPlayer.setItem3(matchPlayerNode.findValue("item_3").asInt());
        matchPlayer.setItem4(matchPlayerNode.findValue("item_4").asInt());
        matchPlayer.setItem5(matchPlayerNode.findValue("item_5").asInt());
        matchPlayer.setBackpack0(matchPlayerNode.findValue("backpack_0").asInt());
        matchPlayer.setBackpack1(matchPlayerNode.findValue("backpack_1").asInt());
        matchPlayer.setBackpack2(matchPlayerNode.findValue("backpack_2").asInt());
        matchPlayer.setKills(matchPlayerNode.findValue("kills").asInt());
        matchPlayer.setDeaths(matchPlayerNode.findValue("deaths").asInt());
        matchPlayer.setAssists(matchPlayerNode.findValue("assists").asInt());
        matchPlayer.setLeaverStatus(matchPlayerNode.findValue("leaver_status").asInt());
        matchPlayer.setLastHits(matchPlayerNode.findValue("last_hits").asInt());
        matchPlayer.setDenies(matchPlayerNode.findValue("denies").asInt());
        matchPlayer.setGoldPerMin(matchPlayerNode.findValue("gold_per_min").asInt());
        matchPlayer.setXpPerMin(matchPlayerNode.findValue("xp_per_min").asInt());
        matchPlayer.setLevel(matchPlayerNode.findValue("level").asInt());
        matchPlayer.setHeroDamage(matchPlayerNode.findValue("hero_damage").asInt());
        matchPlayer.setTowerDamage(matchPlayerNode.findValue("tower_damage").asInt());
        matchPlayer.setHeroHealing(matchPlayerNode.findValue("hero_healing").asInt());
        matchPlayer.setGold(matchPlayerNode.findValue("gold").asInt());
        matchPlayer.setGoldSpent(matchPlayerNode.findValue("gold_spent").asInt());
        matchPlayer.setScaledHeroDamage(matchPlayerNode.findValue("scaled_hero_damage").asInt());
        matchPlayer.setScaledTowerDamage(matchPlayerNode.findValue("scaled_tower_damage").asInt());
        matchPlayer.setScaledHeroHealing(matchPlayerNode.findValue("scaled_hero_healing").asInt());
        matchPlayer.setAbilityUpgrades(JsonMapper.nonDefaultMapper().toJson(matchPlayerNode.findValue("ability_upgrades")));
        return matchPlayer;
    }

}
