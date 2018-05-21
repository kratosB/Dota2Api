package com.service.steam;

import com.api.req.GetMatchHistoryReq;
import com.bean.match.MatchDetail;

/**
 * Created on 2018/5/21.
 * 
 * @author zhiqiang bao
 */
public interface ISteamMatchService {

    /**
     * 从steam获取比赛历史
     * 
     * @param getMatchHistoryReq
     *            参数
     * @return json字符串
     */
    String getMatchHistory(GetMatchHistoryReq getMatchHistoryReq);

    /**
     * 根据比赛id，从steam获取比赛详情
     * 
     * @param matchId
     *            比赛id
     * @return 比赛详情json字符串
     */
    String getMatchDetailByMatchId(long matchId);

    MatchDetail getMatchDetail(long matchId);
}
