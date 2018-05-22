package com.api;

import com.api.req.GetPlayerInfoReq;
import com.config.Configuration;
import com.dao.entity.Player;
import com.service.local.IPlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

/**
 * Created on 2018/3/28.
 *
 * @author zhiqiang bao
 */
@RestController
public class PlayerEndpoint {

    Logger logger = LoggerFactory.getLogger(PlayerEndpoint.class);

    private IPlayerService playerServiceImpl;

    private Configuration configuration;

    public PlayerEndpoint(IPlayerService playerServiceImpl, Configuration configuration) {
        this.playerServiceImpl = playerServiceImpl;
        this.configuration = configuration;
    }

    @ApiOperation("从steam，根据steamId更新player的数据")
    @PostMapping(value = "/api/player/steam/updatePlayerDataBySteamId")
    public void updatePlayerDataBySteamId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        logger.info("开始从steam，根据steamId更新player的数据，steamId={}",steamId);
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        playerServiceImpl.updatePlayerDataBySteamId(steamId);
        logger.info("结束从steam，根据steamId更新player的数据，steamId={}",steamId);
    }

    @ApiOperation("从steam，根据steamId更新player的friend数据")
    @PostMapping(value = "/api/player/steam/updateFriendDataBySteamId")
    public void updateFriendDataBySteamId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        logger.info("开始从steam，根据steamId更新player的friend数据，steamId={}",steamId);
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        playerServiceImpl.updateFriendDataBySteamId(steamId);
        logger.info("结束从steam，根据steamId更新player的friend数据，steamId={}",steamId);
    }

    @ApiOperation("查找选手")
    @PostMapping(value = "/api/player/getPlayerInfo")
    public List<Player> getPlayerInfo(@RequestBody GetPlayerInfoReq getPlayerInfoReq) {
        logger.info("开始查找选手，getPlayerInfoReq = {}", getPlayerInfoReq);
        List<Player> playerList = playerServiceImpl.getPlayerInfo(getPlayerInfoReq);
        logger.info("结束查找选手，playerList.size = {}", playerList.size());
        return playerList;
    }
}
