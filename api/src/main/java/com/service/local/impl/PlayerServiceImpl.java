package com.service.local.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.api.req.GetPlayerInfoReq;
import com.api.req.PlayerWinRateReq;
import com.api.vo.PlayerWinRateVo;
import com.config.Config;
import com.dao.MatchPlayerDao;
import com.dao.RelationDao;
import com.dao.entity.MatchHistory;
import com.dao.entity.MatchPlayer;
import com.dao.entity.Relation;
import com.service.local.IPlayerService;
import com.util.Gateway;
import com.util.SteamIdConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.PlayerDao;
import com.dao.entity.Player;
import com.fasterxml.jackson.databind.JsonNode;
import com.util.JsonMapper;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created on 2018/3/28.
 *
 * @author zhiqiang bao
 */
@Service
public class PlayerServiceImpl implements IPlayerService {

    private PlayerDao playerDao;

    private RelationDao relationDao;

    private MatchPlayerDao matchPlayerDao;

    private Config config;

    private Gateway gateway;

    @Autowired
    public PlayerServiceImpl(PlayerDao playerDao, Config config, RelationDao relationDao, MatchPlayerDao matchPlayerDao,
            Gateway gateway) {
        this.playerDao = playerDao;
        this.config = config;
        this.relationDao = relationDao;
        this.matchPlayerDao = matchPlayerDao;
        this.gateway = gateway;
    }

    @Override
    public void updatePlayerDataBySteamId(String steamId) {
        updatePlayerData(steamId);
    }

    @Override
    public void updateFriendDataBySteamId(String steamId) {
        String getFriendList = "GetFriendList/";
        String steamIdKey = "steamid=";
        String getFriendListUrl = config.getSteamUserUrl() + getFriendList + config.getApiVersion() + steamIdKey + steamId
                + config.getApiAnd();
        String response = gateway.getForObject(getFriendListUrl);
        JsonNode jsonNodes = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        JsonNode friendNodes = jsonNodes.findPath("friends");
        StringBuilder steamIdsBuilder = new StringBuilder();
        friendNodes.forEach(friendNode -> steamIdsBuilder.append(friendNode.findValue("steamid").asText()).append(","));
        updatePlayerData(steamIdsBuilder.substring(0, steamIdsBuilder.length() - 1));
    }

    @Override
    public List<Player> getPlayerInfo(GetPlayerInfoReq getPlayerInfoReq) {
        String steamId = getPlayerInfoReq.getSteamId();
        String dotaId = getPlayerInfoReq.getDotaId();
        String playerName = getPlayerInfoReq.getPlayerName();
        List<Player> playerList;
        if (StringUtils.isBlank(steamId) && StringUtils.isBlank(dotaId) && StringUtils.isBlank(playerName)) {
            playerList = playerDao.findAll();
        } else {
            playerList = playerDao.findAll(((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>(3);
                if (StringUtils.isNotBlank(steamId)) {
                    predicates.add(cb.equal(root.get("steamid"), steamId));
                }
                if (StringUtils.isNotBlank(dotaId)) {
                    predicates.add(cb.equal(root.get("dotaAccountId"), dotaId));
                }
                if (StringUtils.isNotBlank(playerName)) {
                    predicates.add(cb.like(root.get("personaname"), "%" + playerName + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }));
        }
        return playerList;
    }

    @Override
    public PlayerWinRateVo getPlayerWinRate(PlayerWinRateReq playerWinRateReq) {
        // 根据参数获取选手信息
        String dota2Id = playerWinRateReq.getDota2Id();
        String steamId = playerWinRateReq.getSteamId();
        String name = playerWinRateReq.getName();
        List<Player> playerList;
        if (StringUtils.isNotBlank(dota2Id)) {
            GetPlayerInfoReq getPlayerInfoReq = new GetPlayerInfoReq();
            getPlayerInfoReq.setDotaId(dota2Id);
            playerList = getPlayerInfo(getPlayerInfoReq);
        } else if (StringUtils.isNotBlank(steamId)) {
            GetPlayerInfoReq getPlayerInfoReq = new GetPlayerInfoReq();
            getPlayerInfoReq.setSteamId(steamId);
            playerList = getPlayerInfo(getPlayerInfoReq);
        } else if (StringUtils.isNotBlank(name)) {
            GetPlayerInfoReq getPlayerInfoReq = new GetPlayerInfoReq();
            getPlayerInfoReq.setPlayerName(name);
            playerList = getPlayerInfo(getPlayerInfoReq);
        } else {
            return null;
        }
        Player player;
        if (playerList.size() == 0) {
            return null;
        } else {
            player = playerList.get(0);
        }
        // 根据选手信息，查询数据
        List<MatchPlayer> matchPlayerList = matchPlayerDao.findAll(((root, query, cb) -> {
            Root<MatchHistory> matchHistoryRoot = query.from(MatchHistory.class);
            Predicate predicate = cb.and(cb.equal(root.get("matchId"), matchHistoryRoot.get("matchId")),
                    cb.equal(root.get("accountId"), player.getDotaAccountId()));
            int ranked = 7;
            if (playerWinRateReq.isRanked()) {
                predicate = cb.and(predicate, cb.equal(matchHistoryRoot.get("lobbyType"), ranked));
            }
            query.orderBy(cb.desc(matchHistoryRoot.get("matchId")));
            return predicate;
        }));
        if (playerWinRateReq.getSize() != 0) {
            matchPlayerList = matchPlayerList.subList(0, playerWinRateReq.getSize());
        }
        // else if (playerWinRateReq.getDuration()!=0) {
        // Date date = new Date();
        // Long newdddd = date.getTime() - playerWinRateReq.getDuration() * 86400 *1000;
        // Date newDate = new Date(newdddd);
        // matchPlayerList.stream().filter(matchPlayer -> matchPlayer)
        // }
        PlayerWinRateVo playerWinRateVo = new PlayerWinRateVo();
        playerWinRateVo.setPlayerName(player.getPersonaname());
        int winCount = matchPlayerList.stream().filter(matchPlayer -> matchPlayer.getWin() == 1).collect(Collectors.toList())
                .size();
        int loseCount = matchPlayerList.stream().filter(matchPlayer -> matchPlayer.getWin() == 0).collect(Collectors.toList())
                .size();
        playerWinRateVo.setWinCount(winCount);
        playerWinRateVo.setLoseCount(loseCount);
        double winRate = ((double) winCount) / ((double) winCount + (double) loseCount);
        playerWinRateVo.setWinRate(winRate);
        return playerWinRateVo;
    }

    public void updateRelation(String steamId, List<String> friendIdList) {
        List<Relation> relationList = relationDao.findBySteamIdAndAndFriendIdIn(steamId, friendIdList);
        // TODO
    }

    private void updatePlayerData(String steamIds) {
        // 获取steam的hero数据
        String getPlayerSummaries = "GetPlayerSummaries/";
        String version = "v0002/?";
        String steamIdsKey = "steamids=";
        String getPlayerSummariesUrl = config.getSteamUserUrl() + getPlayerSummaries + version + steamIdsKey + steamIds
                + config.getApiAnd();
        String response = gateway.getForObject(getPlayerSummariesUrl);
        JsonNode jsonNodes = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        JsonNode playerNodes = jsonNodes.findPath("players");
        List<Player> playerList = new ArrayList<>();
        playerNodes.forEach(playerNode -> {
            Player player = new Player();
            player.setSteamid(playerNode.findValue("steamid").asText());
            player.setDotaAccountId(SteamIdConverter.defaultInstance().getId32(playerNode.findValue("steamid").asText()));
            player.setCommunityvisibilitystate(playerNode.findValue("communityvisibilitystate").asInt());
            player.setProfilestate(playerNode.findValue("profilestate") != null ? playerNode.findValue("profilestate").asInt() : 0);
            player.setPersonaname(playerNode.findValue("personaname").asText());
            player.setLastlogoff(playerNode.findValue("lastlogoff").asLong());
            player.setProfileurl(playerNode.findValue("profileurl").asText());
            player.setAvatar(playerNode.findValue("avatar").asText());
            player.setAvatarmedium(playerNode.findValue("avatarmedium").asText());
            player.setAvatarfull(playerNode.findValue("avatarfull").asText());
            player.setPersonastate(playerNode.findValue("personastate").asInt());
            player.setPrimaryclanid(
                    playerNode.findValue("primaryclanid") != null ? playerNode.findValue("primaryclanid").asText() : null);
            player.setTimecreated(playerNode.findValue("timecreated") != null ? playerNode.findValue("timecreated").asLong() : 0L);
            player.setPersonastateflags(
                    playerNode.findValue("personastateflags") != null ? playerNode.findValue("personastateflags").asInt() : 0);
            player.setLoccountrycode(
                    playerNode.findValue("loccountrycode") != null ? playerNode.findValue("loccountrycode").asText() : null);
            playerList.add(player);
        });
        playerDao.save(playerList);
    }

}
