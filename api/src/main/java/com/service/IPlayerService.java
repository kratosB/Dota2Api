package com.service;

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
     * @param steamId steamId64
     */
    public void updateFriendDataBySteamId(String steamId);

}
