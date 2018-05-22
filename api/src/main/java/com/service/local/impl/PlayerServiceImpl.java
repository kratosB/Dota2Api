package com.service.local.impl;

import java.util.ArrayList;
import java.util.List;

import com.api.req.GetPlayerInfoReq;
import com.config.Configuration;
import com.dao.RelationDao;
import com.dao.entity.Relation;
import com.service.local.IPlayerService;
import com.util.SteamIdConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dao.PlayerDao;
import com.dao.entity.Player;
import com.fasterxml.jackson.databind.JsonNode;
import com.util.JsonMapper;

import javax.persistence.criteria.Predicate;

/**
 * Created on 2018/3/28.
 *
 * @author zhiqiang bao
 */
@Service
public class PlayerServiceImpl implements IPlayerService {

    private PlayerDao playerDao;

    private RelationDao relationDao;

    private Configuration configuration;

    @Autowired
    public PlayerServiceImpl(PlayerDao playerDao, Configuration configuration, RelationDao relationDao) {
        this.playerDao = playerDao;
        this.configuration = configuration;
        this.relationDao = relationDao;
    }

    @Override
    public void updatePlayerDataBySteamId(String steamId) {
        updatePlayerData(steamId);
    }

    @Override
    public void updateFriendDataBySteamId(String steamId) {
        String getFriendList = "GetFriendList/";
        String steamIdKey = "steamid=";
        String getFriendListUrl = configuration.getSteamUserUrl() + getFriendList + configuration.getApiVersion()
                + configuration.getApiKey() + configuration.getApiAnd() + steamIdKey + steamId;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getFriendListUrl, String.class);
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

    public void updateRelation(String steamId, List<String> friendIdList) {
        List<Relation> relationList = relationDao.findBySteamIdAndAndFriendIdIn(steamId, friendIdList);
        // TODO
    }

    private void updatePlayerData(String steamIds) {
        // 获取steam的hero数据
        String getPlayerSummaries = "GetPlayerSummaries/";
        String version = "v0002/";
        String steamIdsKey = "steamids=";
        String getPlayerSummariesUrl = configuration.getSteamUserUrl() + getPlayerSummaries + version + configuration.getApiKey()
                + configuration.getApiAnd() + steamIdsKey + steamIds;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getPlayerSummariesUrl, String.class);
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
