package com.service.local.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import com.util.MyJsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.api.req.GetMatchHistoryReq;
import com.dao.entity.Hero;
import com.fasterxml.jackson.databind.JsonNode;
import com.util.JsonMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    public void updateMatchId(String steamId) {
        List<Long> matchIdList = new ArrayList<>(50);
        List<Hero> heroList = heroServiceImpl.listAll();
        List<Integer> heroIdList = heroList.stream().map(Hero::getId).collect(Collectors.toList());
        heroIdList.forEach(heroId -> {
            List<Long> heroMatchIdList = getMatchIdBySteamIdAndHeroId(steamId, heroId, null);
            matchIdList.addAll(heroMatchIdList);
            try {
                Thread.sleep(4000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        List<MatchHistory> existedMatchHistoryList = matchHistoryDao.findAll(matchIdList);
        List<Long> existedMatchIdList = existedMatchHistoryList.stream().map(MatchHistory::getMatchId).collect(Collectors.toList());
        matchIdList.removeAll(existedMatchIdList);
        List<MatchHistory> matchHistoryList = new ArrayList<>(100);
        matchIdList.forEach(matchId -> {
            MatchHistory matchHistory = new MatchHistory();
            matchHistory.setMatchId(matchId);
            matchHistory.setCreatedTime(new Date());
            matchHistoryList.add(matchHistory);
        });
        // 数据量太多的时候（几千条），一次性save怕出错
        int loopSize = 100;
        while (matchHistoryList.size() > loopSize) {
            List<MatchHistory> saveList = new ArrayList<>(100);
            for (int i = 0; i < loopSize && i < matchHistoryList.size(); i++) {
                saveList.add(matchHistoryList.get(i));
            }
            matchHistoryDao.save(saveList);
            matchHistoryList.removeAll(saveList);
        }
        matchHistoryDao.save(matchHistoryList);
    }

    @Override
    public void updateMatchDetailJob() {
        Pageable pageable = new PageRequest(0, 1, null);
        Page<MatchHistory> matchHistoryPage = matchHistoryDao.findByUpdatedTimeIsNullOrderByMatchIdDesc(pageable);
        List<MatchHistory> matchHistoryList = matchHistoryPage.getContent();
        matchHistoryList.forEach(matchHistory -> updateMatchDetailByMatchId(matchHistory.getMatchId()));
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateMatchDetailByMatchId(Long matchId) {
        String response = steamMatchServiceImpl.getMatchDetailByMatchId(matchId);
        JsonNode jsonNode = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        // 解析并保存比赛结果
        MyJsonNode myMatchNode = new MyJsonNode(jsonNode);
        MatchHistory matchHistory = convertMatchNodeToMatchHistory(myMatchNode);
        matchHistoryDao.save(matchHistory);
        // 查找matchPlayer表，看是否已经有相关数据存在，有的话更新，没有就新增
        List<MatchPlayer> matchPlayerList = matchPlayerDao.findByMatchId(matchId);
        Map<Long, Long> map = new HashMap<>(10);
        if (matchPlayerList.size() != 0) {
            int size = 10;
            if (matchPlayerList.stream().map(MatchPlayer::getAccountId).distinct().collect(Collectors.toList()).size() < size) {
                // 说明其中有account_重复，应该是匿名玩家 或 合作对抗AI
                matchPlayerDao.delete(matchPlayerList);
            } else {
                map.putAll(matchPlayerList.stream().collect(Collectors.toMap(MatchPlayer::getAccountId, MatchPlayer::getId)));
            }
        }
        JsonNode playersNode = jsonNode.findPath("players");
        Iterator<JsonNode> playerNodeList = playersNode.iterator();
        playerNodeList.forEachRemaining(node -> {
            MyJsonNode myPlayerNode = new MyJsonNode(node);
            MatchPlayer matchPlayer = convertMatchPlayerNodeToMatchPlayer(myPlayerNode);
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

    private MatchHistory convertMatchNodeToMatchHistory(MyJsonNode matchNode) {
        MatchHistory matchHistory = new MatchHistory();
        matchHistory.setMatchId(matchNode.findValueAsLong("match_id"));
        matchHistory.setRadiantWin(StringUtils.equals(matchNode.findValueAsString("radiant_win"), "true") ? 1 : 0);
        matchHistory.setDuration(matchNode.findValueAsInt("duration"));
        matchHistory.setPreGameDuration(matchNode.findValueAsInt("pre_game_duration"));
        matchHistory.setStartTime(new Date(matchNode.findValueAsLong("start_time") * 1000));
        matchHistory.setMatchSeqNum(matchNode.findValueAsLong("match_seq_num"));
        matchHistory.setTowerStatusRadiant(matchNode.findValueAsInt("tower_status_radiant"));
        matchHistory.setTowerStatusDire(matchNode.findValueAsInt("tower_status_dire"));
        matchHistory.setBarracksStatusRadiant(matchNode.findValueAsInt("barracks_status_radiant"));
        matchHistory.setBarracksStatusDire(matchNode.findValueAsInt("barracks_status_dire"));
        matchHistory.setCluster(matchNode.findValueAsInt("cluster"));
        matchHistory.setFirstBloodTime(matchNode.findValueAsInt("first_blood_time"));
        matchHistory.setLobbyType(matchNode.findValueAsInt("lobby_type"));
        matchHistory.setHumanPlayers(matchNode.findValueAsInt("human_players"));
        matchHistory.setLeagueId(matchNode.findValueAsInt("leagueid"));
        matchHistory.setPositiveVotes(matchNode.findValueAsInt("positive_votes"));
        matchHistory.setNegativeVotes(matchNode.findValueAsInt("negative_votes"));
        matchHistory.setGameMode(matchNode.findValueAsInt("game_mode"));
        matchHistory.setFlags(matchNode.findValueAsInt("flags"));
        matchHistory.setEngine(matchNode.findValueAsInt("engine"));
        matchHistory.setRadiantScore(matchNode.findValueAsInt("radiant_score"));
        matchHistory.setDireScore(matchNode.findValueAsInt("dire_score"));
        String radiantTeamId = "radiant_team_id";
        if (matchNode.getJsonNode().findValue(radiantTeamId) != null) {
            matchHistory.setRadiantTeamId(matchNode.findValueAsInt("radiant_team_id"));
            matchHistory.setRadiantName(matchNode.findValueAsString("radiant_name"));
            matchHistory.setRadiantLogo(matchNode.findValueAsLong("radiant_logo"));
            matchHistory.setRadiantTeamComplete(matchNode.findValueAsInt("radiant_team_complete"));
            matchHistory.setRadiantCaptain(matchNode.findValueAsLong("radiant_captain"));
        }
        String direTeamId = "dire_team_id";
        if (matchNode.getJsonNode().findValue(direTeamId) != null) {
            matchHistory.setDireTeamId(matchNode.findValueAsInt("dire_team_id"));
            matchHistory.setDireName(matchNode.findValueAsString("dire_name"));
            matchHistory.setDireLogo(matchNode.findValueAsLong("dire_logo"));
            matchHistory.setDireTeamComplete(matchNode.findValueAsInt("dire_team_complete"));
            matchHistory.setDireCaptain(matchNode.findValueAsLong("dire_captain"));
        }
        matchHistory.setPicksBans(matchNode.getJsonNode().findValue("picks_bans") == null ? null
                : JsonMapper.nonDefaultMapper()
                        .toJson(matchNode.getJsonNode().findValue("picks_bans") != null
                                ? matchNode.getJsonNode().findValue("picks_bans")
                                : ""));
        if (matchHistory.getCreatedTime() == null) {
            matchHistory.setCreatedTime(new Date());
        }
        matchHistory.setUpdatedTime(new Date());
        return matchHistory;
    }

    private MatchPlayer convertMatchPlayerNodeToMatchPlayer(MyJsonNode matchPlayerNode) {
        MatchPlayer matchPlayer = new MatchPlayer();
        // 当联机打ai的时候，对面ai玩家没有account_id的
        matchPlayer.setAccountId(matchPlayerNode.findValueAsLong("account_id"));
        matchPlayer.setPlayerSlot(matchPlayerNode.findValueAsInt("player_slot"));
        matchPlayer.setHeroId(matchPlayerNode.findValueAsInt("hero_id"));
        matchPlayer.setItem0(matchPlayerNode.findValueAsInt("item_0"));
        matchPlayer.setItem1(matchPlayerNode.findValueAsInt("item_1"));
        matchPlayer.setItem2(matchPlayerNode.findValueAsInt("item_2"));
        matchPlayer.setItem3(matchPlayerNode.findValueAsInt("item_3"));
        matchPlayer.setItem4(matchPlayerNode.findValueAsInt("item_4"));
        matchPlayer.setItem5(matchPlayerNode.findValueAsInt("item_5"));
        matchPlayer.setBackpack0(matchPlayerNode.findValueAsInt("backpack_0"));
        matchPlayer.setBackpack1(matchPlayerNode.findValueAsInt("backpack_1"));
        matchPlayer.setBackpack2(matchPlayerNode.findValueAsInt("backpack_2"));
        matchPlayer.setKills(matchPlayerNode.findValueAsInt("kills"));
        matchPlayer.setDeaths(matchPlayerNode.findValueAsInt("deaths"));
        matchPlayer.setAssists(matchPlayerNode.findValueAsInt("assists"));
        matchPlayer.setLeaverStatus(matchPlayerNode.findValueAsInt("leaver_status"));
        matchPlayer.setLastHits(matchPlayerNode.findValueAsInt("last_hits"));
        matchPlayer.setDenies(matchPlayerNode.findValueAsInt("denies"));
        matchPlayer.setGoldPerMin(matchPlayerNode.findValueAsInt("gold_per_min"));
        matchPlayer.setXpPerMin(matchPlayerNode.findValueAsInt("xp_per_min"));
        matchPlayer.setLevel(matchPlayerNode.findValueAsInt("level"));
        matchPlayer.setHeroDamage(matchPlayerNode.findValueAsInt("hero_damage"));
        matchPlayer.setTowerDamage(matchPlayerNode.findValueAsInt("tower_damage"));
        matchPlayer.setHeroHealing(matchPlayerNode.findValueAsInt("hero_healing"));
        matchPlayer.setGold(matchPlayerNode.findValueAsInt("gold"));
        matchPlayer.setGoldSpent(matchPlayerNode.findValueAsInt("gold_spent"));
        matchPlayer.setScaledHeroDamage(matchPlayerNode.findValueAsInt("scaled_hero_damage"));
        matchPlayer.setScaledTowerDamage(matchPlayerNode.findValueAsInt("scaled_tower_damage"));
        matchPlayer.setScaledHeroHealing(matchPlayerNode.findValueAsInt("scaled_hero_healing"));
        matchPlayer.setAbilityUpgrades(JsonMapper.nonDefaultMapper()
                .toJson(matchPlayerNode.getJsonNode().findValue("ability_upgrades") != null
                        ? matchPlayerNode.getJsonNode().findValue("ability_upgrades")
                        : ""));
        return matchPlayer;
    }

}
