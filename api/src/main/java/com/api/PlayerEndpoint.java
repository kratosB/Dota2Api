package com.api;

import com.config.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.service.PlayerService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Created on 2018/3/28.
 *
 * @author zhiqiang bao
 */
@RestController
public class PlayerEndpoint {

    private PlayerService playerService;

    private Configuration configuration;

    public PlayerEndpoint(PlayerService playerService, Configuration configuration) {
        this.playerService = playerService;
        this.configuration = configuration;
    }

    @ApiOperation("根据steamId更新player的数据")
    @PostMapping(value = "/api/player/updatePlayerDataBySteamId")
    public void updatePlayerDataBySteamId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        playerService.updatePlayerDataBySteamId(steamId);
    }

    @ApiOperation("根据steamId更新player的friend数据")
    @PostMapping(value = "/api/player/updateFriendDataBySteamId")
    public void updateFriendDataBySteamId(
            @ApiParam(name = "steamId") @RequestParam(name = "steamId", required = false) String steamId) {
        if (steamId == null) {
            steamId = configuration.getAdminSteamId();
        }
        playerService.updateFriendDataBySteamId(steamId);
    }
}
