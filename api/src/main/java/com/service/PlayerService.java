package com.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dao.PlayerDao;
import com.dao.entity.Player;
import com.fasterxml.jackson.databind.JsonNode;
import com.util.JsonMapper;

/**
 * Created on 2018/3/28.
 *
 * @author zhiqiang bao
 */
@Service
public class PlayerService {

    private PlayerDao playerDao;

    private String steamUrl = "http://api.steampowered.com/ISteamUser/";

    private String key = "?key=EFA1E81676FCC47157EA871A67741EF5";

    private String str2 = "&";

    // STEAMID64 - 76561197960265728 = STEAMID32
    // STEAMID32 + 76561197960265728 = STEAMID64

    @Autowired
    public PlayerService(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    public void updatePlayerDataBySteamId(String steamId) {
        updatePlayerData(steamId);
    }

    public void updateFriendDataBySteamId(String steamId) {
        String getFriendList = "GetFriendList/";
        String version = "v1/";
        String steamIdKey = "steamid=";
        String getFriendListUrl = steamUrl + getFriendList + version + key + str2 + steamIdKey + steamId;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getFriendListUrl, String.class);
        JsonNode jsonNodes = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        JsonNode friendNodes = jsonNodes.findPath("friends");
        StringBuilder steamIdsBuilder = new StringBuilder();
        friendNodes.forEach(friendNode -> {
            steamIdsBuilder.append(friendNode.findValue("steamid").asText()).append(",");
        });
        updatePlayerData(steamIdsBuilder.substring(0, steamIdsBuilder.length() - 1));
    }

    private void updatePlayerData(String steamIds) {
        // 获取steam的hero数据
        String getPlayerSummaries = "GetPlayerSummaries/";
        String version = "v0002/";
        String steamIdsKey = "steamids=";
        String getPlayerSummariesUrl = steamUrl + getPlayerSummaries + version + key + str2 + steamIdsKey + steamIds;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getPlayerSummariesUrl, String.class);
        JsonNode jsonNodes = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        JsonNode playerNodes = jsonNodes.findPath("players");
        List<Player> playerList = new ArrayList<>();
        playerNodes.forEach(playerNode -> {
            Player player = new Player();
            player.setSteamid(playerNode.findValue("steamid").asText());
            player.setCommunityvisibilitystate(playerNode.findValue("communityvisibilitystate").asInt());
            System.out.println(playerNode.findValue("steamid").asText());
            player.setProfilestate(
                    playerNode.findValue("profilestate") != null ? playerNode.findValue("profilestate").asInt() : null);
            player.setPersonaname(playerNode.findValue("personaname").asText());
            player.setLastlogoff(playerNode.findValue("lastlogoff").asLong());
            player.setProfileurl(playerNode.findValue("profileurl").asText());
            player.setAvatar(playerNode.findValue("avatar").asText());
            player.setAvatarmedium(playerNode.findValue("avatarmedium").asText());
            player.setAvatarfull(playerNode.findValue("avatarfull").asText());
            player.setPersonastate(playerNode.findValue("personastate").asInt());
            player.setPrimaryclanid(
                    playerNode.findValue("primaryclanid") != null ? playerNode.findValue("primaryclanid").asText() : null);
            player.setTimecreated(
                    playerNode.findValue("timecreated") != null ? playerNode.findValue("timecreated").asLong() : null);
            player.setPersonastateflags(
                    playerNode.findValue("personastateflags") != null ? playerNode.findValue("personastateflags").asInt() : null);
            player.setLoccountrycode(
                    playerNode.findValue("loccountrycode") != null ? playerNode.findValue("loccountrycode").asText() : null);
            playerList.add(player);
        });
        playerDao.save(playerList);
    }

}
