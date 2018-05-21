package com.service.local;

import java.util.List;

/**
 * Created on 2018/5/16.
 * 
 * @author zhiqiang bao
 */
public interface IMatchService {

    void updateMatchDetail(String steamId);

    /**
     * 从steam，根据heroId，steamId，获取某一个用户的，从某场比赛开始（往前，更老的）的，所有该英雄的比赛id
     * 举个例子，steamId是123456，heroId是1，那么获得到的就是123456这个人的所有盘数的敌法师的比赛id（超过500盘的部分获取不到）
     * 
     * @param steamId
     *            用户的steamId
     * @param heroId
     *            英雄id
     * @param startAtMatchId
     *            从哪一场比赛开始，null的时候视为从最近的一场比赛开始
     * @return 比赛Id列表
     */
    List<Long> getMatchIdBySteamIdAndHeroId(String steamId, int heroId, Long startAtMatchId);

    /**
     * 根据比赛id获取比赛数据，解析并写入本地数据库
     * 
     * @param matchId
     *            比赛id
     */
    void updateMatchDetailByMatchId(Long matchId);

}
