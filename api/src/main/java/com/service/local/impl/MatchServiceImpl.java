package com.service.local.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.dao.MatchHistoryDao;
import com.dao.MatchPlayerDao;
import com.dao.entity.MatchHistory;
import com.dao.entity.MatchPlayer;
import com.service.local.IHeroService;
import com.service.local.IMatchService;
import com.service.steam.ISteamMatchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.req.GetMatchHistoryReq;
import com.dao.entity.Hero;
import com.fasterxml.jackson.databind.JsonNode;
import com.util.JsonMapper;

/**
 * Created on 2017/06/15.
 * 
 * @author zhiqiang bao
 */
@Service
public class MatchServiceImpl implements IMatchService {

    private IHeroService heroServiceImpl;

    private MatchHistoryDao matchHistoryDao;

    private MatchPlayerDao matchPlayerDao;

    private ISteamMatchService steamMatchServiceImpl;

    @Autowired
    public MatchServiceImpl(IHeroService heroServiceImpl, MatchHistoryDao matchHistoryDao, MatchPlayerDao matchPlayerDao,
            ISteamMatchService steamMatchServiceImpl) {
        this.heroServiceImpl = heroServiceImpl;
        this.matchHistoryDao = matchHistoryDao;
        this.matchPlayerDao = matchPlayerDao;
        this.steamMatchServiceImpl = steamMatchServiceImpl;
    }

    @Override
    public void updateMatchDetail(String steamId) {
        List<Long> matchIdList = new ArrayList<>(50);
        List<Hero> heroList = heroServiceImpl.listAll();
        List<Integer> heroIdList = heroList.stream().map(Hero::getId).collect(Collectors.toList());
        heroIdList.forEach(heroId -> {
            List<Long> heroMatchIdList = getMatchIdBySteamIdAndHeroId(steamId, heroId, null);
            matchIdList.addAll(heroMatchIdList);
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        List<MatchHistory> existedMatchHistoryList = matchHistoryDao.findAll(matchIdList);
        List<Long> existedMatchIdList = existedMatchHistoryList.stream().map(MatchHistory::getMatchId).collect(Collectors.toList());
        matchIdList.removeAll(existedMatchIdList);
        System.out.println(JsonMapper.nonDefaultMapper().toJson(matchIdList));
    }

    @Override
    public List<Long> getMatchIdBySteamIdAndHeroId(String steamId, int heroId, Long startAtMatchId) {
        List<Long> matchIdList = new ArrayList<>();
        GetMatchHistoryReq getMatchHistoryReq = new GetMatchHistoryReq();
        getMatchHistoryReq.setAccountId(steamId);
        getMatchHistoryReq.setHeroId((long) heroId);
        getMatchHistoryReq.setStartAtMatchId(startAtMatchId);
        String result = steamMatchServiceImpl.getMatchHistory(getMatchHistoryReq);
        /*
         * 如果用户没有开放权限，是没法拉到用户比赛数据的
         */
        String cannotGetMatchHistoryForAUserThatHasNotAllowedIt = "Cannot get match history for a user that hasn't allowed it";
        // 如果用户没有开放权限，是没法拉到用户比赛数据的
        if (result.contains(cannotGetMatchHistoryForAUserThatHasNotAllowedIt)) {
            return new ArrayList<>();
        }
        JsonNode resultNode = JsonMapper.nonDefaultMapper().fromJson(result, JsonNode.class);
        String matchesPath = "matches";
        for (JsonNode node : resultNode.findPath(matchesPath)) {
            matchIdList.add(node.findValue("match_id").asLong());
        }
        int remainingCount = resultNode.findValue("results_remaining").asInt();
        if (remainingCount > 0) {
            // 给matchIdList排序，取最早的一个id，新的list从这个开始（包含这个，需要distinct）
            Collections.sort(matchIdList);
            Long newStartAtMatchId = matchIdList.get(0) > matchIdList.get(matchIdList.size() - 1)
                    ? matchIdList.get(matchIdList.size() - 1)
                    : matchIdList.get(0);
            if (!Objects.equals(newStartAtMatchId, startAtMatchId)) {
                List<Long> childMatchIdList = getMatchIdBySteamIdAndHeroId(steamId, heroId, newStartAtMatchId);
                matchIdList.addAll(childMatchIdList);
            }
        }
        return matchIdList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public void updateMatchDetailByMatchId(Long matchId) {
        String response = steamMatchServiceImpl.getMatchDetailByMatchId(matchId);
        JsonNode jsonNode = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        // 解析并保存比赛结果
        MatchHistory matchHistory = convertMatchNodeToMatchHistory(jsonNode);
        matchHistoryDao.save(matchHistory);
        // 查找matchPlayer表，看是否已经有相关数据存在，有的话更新，没有就新增
        List<MatchPlayer> matchPlayerList = matchPlayerDao.findByMatchId(matchId);
        Map<Long, Long> map = new HashMap<>(10);
        if (matchPlayerList.size() != 0) {
            map.putAll(matchPlayerList.stream().collect(Collectors.toMap(MatchPlayer::getAccountId, MatchPlayer::getId)));
        }
        JsonNode playersNode = jsonNode.findPath("players");
        Iterator<JsonNode> playerNodeList = playersNode.iterator();
        playerNodeList.forEachRemaining(node -> {
            MatchPlayer matchPlayer = convertMatchPlayerNodeToMatchPlayer(node);
            // 如果radiantWin是1（true），则playerSlot<10的选手（近卫）赢了，如果radiantWin是0（false），则playerSlot>10的选手（天灾）赢了，其余都是输
            boolean bool1 = matchHistory.getRadiantWin() == 1 && matchPlayer.getPlayerSlot() < 10;
            boolean bool2 = matchHistory.getRadiantWin() == 0 && matchPlayer.getPlayerSlot() > 10;
            matchPlayer.setWin(bool1 | bool2 ? 1 : 0);
            if (!map.isEmpty()) {
                matchPlayer.setId(map.get(matchPlayer.getAccountId()));
            }
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
        String radiantTeamId = "radiant_team_id";
        if (matchNode.findValue(radiantTeamId) != null) {
            matchHistory.setRadiantTeamId(matchNode.findValue("radiant_team_id").asInt());
            matchHistory.setRadiantName(matchNode.findValue("radiant_name").asText());
            matchHistory.setRadiantLogo(matchNode.findValue("radiant_logo").asLong());
            matchHistory.setRadiantTeamComplete(matchNode.findValue("radiant_team_complete").asInt());
            matchHistory.setRadiantCaptain(matchNode.findValue("radiant_captain").asLong());
        }
        String direTeamId = "dire_team_id";
        if (matchNode.findValue(direTeamId) != null) {
            matchHistory.setDireTeamId(matchNode.findValue("dire_team_id").asInt());
            matchHistory.setDireName(matchNode.findValue("dire_name").asText());
            matchHistory.setDireLogo(matchNode.findValue("dire_logo").asLong());
            matchHistory.setDireTeamComplete(matchNode.findValue("dire_team_complete").asInt());
            matchHistory.setDireCaptain(matchNode.findValue("dire_captain").asLong());
        }
        matchHistory.setPicksBans(matchNode.findValue("picks_bans") == null ? null
                : JsonMapper.nonDefaultMapper().toJson(matchNode.findValue("picks_bans")));
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
