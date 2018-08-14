package com.service.local.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.api.req.GetPlayerInfoReq;
import com.api.req.PlayerWinRateReq;
import com.api.vo.PlayerWinRateVo;
import com.config.Config;
import com.dao.MatchPlayerDao;
import com.dao.PlayerDao;
import com.dao.RelationDao;
import com.dao.entity.MatchHistory;
import com.dao.entity.MatchPlayer;
import com.dao.entity.Player;
import com.dao.entity.Relation;
import com.fasterxml.jackson.databind.JsonNode;
import com.service.local.IPlayerService;
import com.util.Gateway;
import com.util.JsonMapper;
import com.util.SteamIdConverter;

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
        String response = gateway.getForString(getFriendListUrl);
        JsonNode jsonNodes = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        JsonNode friendNodes = jsonNodes.findPath("friends");
        StringBuilder steamIdsBuilder = new StringBuilder();
        List<String> friendIdList = new ArrayList<>();
        friendNodes.forEach(friendNode -> {
            steamIdsBuilder.append(friendNode.findValue("steamid").asText()).append(",");
            friendIdList.add(friendNode.findValue("steamid").asText());
        });
        updatePlayerData(steamIdsBuilder.substring(0, steamIdsBuilder.length() - 1));
        updateRelation(steamId, friendIdList);
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
                return cb.and(predicates.toArray(new Predicate[0]));
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
        String dotaAccountId = player.getDotaAccountId();
        // 判断是不是单排，0表示无所谓单排组排。1是单排，2是组排。
        int individual = 1;
        int group = 2;
        int teamStatus = playerWinRateReq.getTeamStatus();
        List<Long> groupMatchIdList = new ArrayList<>();
        if (teamStatus == individual || teamStatus == group) {
            // TODO 这个地方，稍后楼可以换成relation。现在的sql语句是：SELECT DISTINCT p1.match_id FROM
            // match_player p1, match_player p2, player pl1 WHERE p1.match_id = p2.match_id
            // AND p2.account_id = '127990273' AND p1.account_id = pl1.dota_account_id AND
            // pl1.dota_account_id <> '127990273';
            List<String> friendIdList = playerDao.findAll().stream()
                    .filter(thisPlayer -> !StringUtils.equals(thisPlayer.getDotaAccountId(), dotaAccountId))
                    .map(Player::getDotaAccountId).collect(Collectors.toList());
            List<MatchPlayer> groupMatchList = matchPlayerDao.findAll(((root, query, cb) -> {
                Root<MatchPlayer> matchPlayerRoot = query.from(MatchPlayer.class);
                return cb.and(cb.equal(root.get("matchId"), matchPlayerRoot.get("matchId")),
                        cb.equal(matchPlayerRoot.get("accountId"), dotaAccountId), root.get("accountId").in(friendIdList));
            }));
            groupMatchIdList.addAll(groupMatchList.stream().map(MatchPlayer::getMatchId).distinct().collect(Collectors.toList()));
        }
        // 根据选手信息，查询数据
        Specification<MatchPlayer> specification = (root, query, cb) -> {
            Root<MatchHistory> matchHistoryRoot = query.from(MatchHistory.class);
            Predicate predicate = cb.and(cb.equal(root.get("matchId"), matchHistoryRoot.get("matchId")),
                    cb.equal(root.get("accountId"), dotaAccountId));
            // 是不是天梯
            if (playerWinRateReq.isRanked()) {
                int ranked = 7;
                predicate = cb.and(predicate, cb.equal(matchHistoryRoot.get("lobbyType"), ranked));
            }
            // 时间，最近多少天
            if (playerWinRateReq.getDuration() != 0) {
                Long startTimeLong = System.currentTimeMillis() - playerWinRateReq.getDuration() * 86400 * 1000;
                Date startDate = new Date(startTimeLong);
                predicate = cb.and(predicate, cb.greaterThan(matchHistoryRoot.get("startTime"), startDate));
            }
            // subQuery是组排的matchIdList，1是单排，所以是notIn，2是组排，所以是in
            if (teamStatus == individual) {
                predicate = cb.and(predicate, cb.not(root.get("matchId").in(groupMatchIdList)));
            } else if (teamStatus == group) {
                predicate = cb.and(predicate, root.get("matchId").in(groupMatchIdList));
            }
            query.orderBy(cb.desc(matchHistoryRoot.get("matchId")));
            return predicate;
        };
        List<MatchPlayer> matchPlayerList;
        if (playerWinRateReq.getSize() > 0) {
            matchPlayerList = matchPlayerDao.findAll(specification, new PageRequest(0, playerWinRateReq.getSize())).getContent();
        } else {
            matchPlayerList = matchPlayerDao.findAll(specification);
        }
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

    private void updateRelation(String steamId, List<String> friendIdList) {
        List<Relation> newRelationList = new ArrayList<>();
        List<Relation> relationList = relationDao.findBySteamId(steamId);
        List<String> existedFriendIdList = relationList.stream().map(Relation::getFriendId).collect(Collectors.toList());
        friendIdList.stream().filter(friendId -> !existedFriendIdList.contains(friendId)).forEach(friendId -> {
            Relation relation1 = new Relation();
            relation1.setSteamId(steamId);
            relation1.setFriendId(friendId);
            newRelationList.add(relation1);
            Relation relation2 = new Relation();
            relation2.setSteamId(friendId);
            relation2.setFriendId(steamId);
            newRelationList.add(relation2);
        });
        relationDao.save(newRelationList);
    }

    private void updatePlayerData(String steamIds) {
        // 获取steam的hero数据
        String getPlayerSummaries = "GetPlayerSummaries/";
        String version = "v0002/?";
        String steamIdsKey = "steamids=";
        String getPlayerSummariesUrl = config.getSteamUserUrl() + getPlayerSummaries + version + steamIdsKey + steamIds
                + config.getApiAnd();
        String response = gateway.getForString(getPlayerSummariesUrl);
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
