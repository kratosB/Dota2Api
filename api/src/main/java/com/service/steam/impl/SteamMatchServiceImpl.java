package com.service.steam.impl;

import com.api.req.GetMatchHistoryReq;
import com.bean.match.MatchDetail;
import com.bean.match.MatchDetailEntity;
import com.config.Config;
import com.service.steam.ISteamMatchService;
import com.util.Gateway;
import com.util.JsonMapper;
import org.springframework.stereotype.Service;

/**
 * Created on 2018/5/21.
 * 
 * @author zhiqiang bao
 */
@Service
public class SteamMatchServiceImpl implements ISteamMatchService {

    private Config config;

    private Gateway gateway;

    public SteamMatchServiceImpl(Config config, Gateway gateway) {
        this.config = config;
        this.gateway = gateway;
    }

    @Override
    public String getMatchHistory(GetMatchHistoryReq getMatchHistoryReq) {
        String getMatchHistory = "GetMatchHistory/";
        StringBuilder sb = new StringBuilder();
        sb.append(config.getIDota2Url()).append(getMatchHistory).append(config.getApiVersion()).append(config.getApiKeyFirst());
        if (getMatchHistoryReq.getAccountId() != null) {
            sb.append(config.getApiAnd()).append("account_id=").append(getMatchHistoryReq.getAccountId());
        }
        if (getMatchHistoryReq.getGameMode() != null) {
            sb.append(config.getApiAnd()).append("game_mode=").append(getMatchHistoryReq.getGameMode());
        }
        if (getMatchHistoryReq.getHeroId() != null) {
            sb.append(config.getApiAnd()).append("hero_id=").append(getMatchHistoryReq.getHeroId());
        }
        if (getMatchHistoryReq.getLeagueId() != null) {
            sb.append(config.getApiAnd()).append("league_id=").append(getMatchHistoryReq.getLeagueId());
        }
        if (getMatchHistoryReq.getMatchesRequested() != null) {
            sb.append(config.getApiAnd()).append("matches_requested=").append(getMatchHistoryReq.getMatchesRequested());
        }
        if (getMatchHistoryReq.getMinPlayers() != null) {
            sb.append(config.getApiAnd()).append("min_players=").append(getMatchHistoryReq.getMinPlayers());
        }
        if (getMatchHistoryReq.getSkill() != null) {
            sb.append(config.getApiAnd()).append("skill=").append(getMatchHistoryReq.getSkill());
        }
        if (getMatchHistoryReq.getStartAtMatchId() != null) {
            sb.append(config.getApiAnd()).append("start_at_match_id=").append(getMatchHistoryReq.getStartAtMatchId());
        }
        if (getMatchHistoryReq.getDateMax() != null) {
            sb.append(config.getApiAnd()).append("date_max=").append(getMatchHistoryReq.getDateMax());
        }
        if (getMatchHistoryReq.getDateMin() != null) {
            sb.append(config.getApiAnd()).append("date_min=").append(getMatchHistoryReq.getDateMin());
        }
        return gateway.getForObject(sb.toString());
    }

    @Override
    public String getMatchDetailByMatchId(long matchId) {
        String getMatchDetails = "GetMatchDetails/";
        String matchIdStr = "match_id=" + matchId;
        String getHeroUrl = config.getIDota2Url() + getMatchDetails + config.getApiVersion() + config.getApiKeyFirst()
                + config.getApiAnd() + matchIdStr;
        return gateway.getForObject(getHeroUrl);
    }

    @Override
    public MatchDetail getMatchDetail(long matchId) {
        String getMatchDetails = "GetMatchDetails/";
        String matchIdStr = "match_id=" + matchId;
        String getHeroUrl = config.getIDota2Url() + getMatchDetails + config.getApiVersion() + config.getApiKeyFirst()
                + config.getApiAnd() + matchIdStr;
        String response = gateway.getForObject(getHeroUrl);
        MatchDetailEntity matchDetailEntity = JsonMapper.nonDefaultMapper().fromJson(response, MatchDetailEntity.class);
        return matchDetailEntity.getResult();
    }
}
