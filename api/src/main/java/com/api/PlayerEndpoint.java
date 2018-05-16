package com.api;

import com.config.Configuration;
import com.service.IPlayerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Created on 2018/3/28.
 *
 * @author zhiqiang bao
 */
@RestController
public class PlayerEndpoint {

    private IPlayerService playerServiceImpl;

    private Configuration configuration;

    public PlayerEndpoint(IPlayerService playerServiceImpl, Configuration configuration) {
        this.playerServiceImpl = playerServiceImpl;
        this.configuration = configuration;
    }

    @ApiOperation("根据steamId更新player的数据")
    @PostMapping(value = "/api/player/updatePlayerDataBySteamId")
    public void updatePlayerDataBySteamId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        playerServiceImpl.updatePlayerDataBySteamId(steamId);
    }

    @ApiOperation("根据steamId更新player的friend数据")
    @PostMapping(value = "/api/player/updateFriendDataBySteamId")
    public void updateFriendDataBySteamId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        playerServiceImpl.updateFriendDataBySteamId(steamId);
    }
}
