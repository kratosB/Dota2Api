package com.api;

import com.api.req.GetPlayerInfoReq;
import com.api.req.PlayerWinRateReq;
import com.api.vo.PlayerWinRateVo;
import com.config.Config;
import com.dao.entity.Player;
import com.service.local.IPlayerService;
import com.util.SteamIdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
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

    private Logger logger = LoggerFactory.getLogger(PlayerEndpoint.class);

    private IPlayerService playerServiceImpl;

    private Config config;

    public PlayerEndpoint(IPlayerService playerServiceImpl, Config config) {
        this.playerServiceImpl = playerServiceImpl;
        this.config = config;
    }

    @ApiOperation("从steam，根据steamId更新player的数据")
    @GetMapping(value = "/api/player/steam/updatePlayerDataBySteamId")
    public void updatePlayerDataBySteamId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        logger.info("开始从steam，根据steamId更新player的数据，steamId={}", steamId);
        if (steamId == null) {
            steamId = config.getAdminSteamId();
        } else {
            steamId = SteamIdConverter.defaultInstance().getId64(steamId);
        }
        playerServiceImpl.updatePlayerDataBySteamId(steamId);
        logger.info("结束从steam，根据steamId更新player的数据，steamId={}", steamId);
    }

    @ApiOperation("从steam，根据steamId更新player的friend数据")
    @GetMapping(value = "/api/player/steam/updateFriendDataBySteamId")
    public void updateFriendDataBySteamId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        logger.info("开始从steam，根据steamId更新player的friend数据，steamId={}", steamId);
        if (steamId == null) {
            steamId = config.getAdminSteamId();
        } else {
            steamId = SteamIdConverter.defaultInstance().getId64(steamId);
        }
        playerServiceImpl.updateFriendDataBySteamId(steamId);
        logger.info("结束从steam，根据steamId更新player的friend数据，steamId={}", steamId);
    }

    @ApiOperation("查找选手")
    @PostMapping(value = "/api/player/getPlayerInfo")
    public List<Player> getPlayerInfo(@RequestBody GetPlayerInfoReq getPlayerInfoReq) {
        logger.info("开始查找选手，getPlayerInfoReq = {}", getPlayerInfoReq);
        List<Player> playerList = playerServiceImpl.getPlayerInfo(getPlayerInfoReq);
        logger.info("结束查找选手，playerList.size = {}", playerList.size());
        return playerList;
    }

    @ApiOperation("获取玩家胜率")
    @PostMapping(value = "/api/player/getPlayerWinRate")
    public PlayerWinRateVo getPlayerWinRate(@RequestBody PlayerWinRateReq playerWinRateReq) {
        logger.info("开始获取玩家胜率，playerWinRateReq = {}", playerWinRateReq);
        PlayerWinRateVo playerWinRateVo = playerServiceImpl.getPlayerWinRate(playerWinRateReq);
        logger.info("结束获取玩家胜率，playerWinRateVo = {}", playerWinRateVo);
        return playerWinRateVo;
    }
}
