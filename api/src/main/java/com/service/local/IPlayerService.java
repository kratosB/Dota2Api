package com.service.local;

import com.api.req.GetPlayerInfoReq;
import com.api.req.PlayerWinRateReq;
import com.api.vo.PlayerWinRateVo;
import com.dao.entity.Player;

import java.util.List;

/**
 * Created on 2018/5/16.
 * 
 * @author zhiqiang bao
 */
public interface IPlayerService {

    /**
     * 根据steamId64，更新用户数据到本地数据库
     * 
     * @param steamId
     *            steamId64
     */
    public void updatePlayerDataBySteamId(String steamId);

    /**
     * 根据steamId64，更新用户的好友的数据到本地数据库
     * 
     * @param steamId
     *            steamId64
     */
    public void updateFriendDataBySteamId(String steamId);

    /**
     * 查找选手
     * 
     * @param getPlayerInfoReq
     *            查询参数
     * @return 选手信息
     */
    List<Player> getPlayerInfo(GetPlayerInfoReq getPlayerInfoReq);

    /**
     * 获取玩家胜率
     * 
     * @param playerWinRateReq
     *            获取玩家胜率参数
     * @return 玩家胜率
     */
    PlayerWinRateVo getPlayerWinRate(PlayerWinRateReq playerWinRateReq);
}
