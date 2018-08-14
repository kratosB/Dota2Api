package com.service.steam.impl;

import org.springframework.stereotype.Service;

import com.api.req.GetMatchHistoryReq;
import com.bean.match.MatchDetail;
import com.bean.match.MatchDetailEntity;
import com.config.Config;
import com.service.steam.ISteamMatchService;
import com.util.Gateway;
import com.util.JsonMapper;

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
        sb.append(config.getIDota2Url()).append(getMatchHistory).append(config.getApiVersion());
        if (getMatchHistoryReq.getAccountId() != null) {
            sb.append("account_id=").append(getMatchHistoryReq.getAccountId()).append(config.getApiAnd());
        }
        if (getMatchHistoryReq.getGameMode() != null) {
            sb.append("game_mode=").append(getMatchHistoryReq.getGameMode()).append(config.getApiAnd());
        }
        if (getMatchHistoryReq.getHeroId() != null) {
            sb.append("hero_id=").append(getMatchHistoryReq.getHeroId()).append(config.getApiAnd());
        }
        if (getMatchHistoryReq.getLeagueId() != null) {
            sb.append("league_id=").append(getMatchHistoryReq.getLeagueId()).append(config.getApiAnd());
        }
        if (getMatchHistoryReq.getMatchesRequested() != null) {
            sb.append("matches_requested=").append(getMatchHistoryReq.getMatchesRequested()).append(config.getApiAnd());
        }
        if (getMatchHistoryReq.getMinPlayers() != null) {
            sb.append("min_players=").append(getMatchHistoryReq.getMinPlayers()).append(config.getApiAnd());
        }
        if (getMatchHistoryReq.getSkill() != null) {
            sb.append("skill=").append(getMatchHistoryReq.getSkill()).append(config.getApiAnd());
        }
        if (getMatchHistoryReq.getStartAtMatchId() != null) {
            sb.append("start_at_match_id=").append(getMatchHistoryReq.getStartAtMatchId()).append(config.getApiAnd());
        }
        if (getMatchHistoryReq.getDateMax() != null) {
            sb.append("date_max=").append(getMatchHistoryReq.getDateMax()).append(config.getApiAnd());
        }
        if (getMatchHistoryReq.getDateMin() != null) {
            sb.append("date_min=").append(getMatchHistoryReq.getDateMin()).append(config.getApiAnd());
        }
        return gateway.getForString(sb.toString());
    }

    @Override
    public String getMatchDetailByMatchId(long matchId) {
        String getMatchDetails = "GetMatchDetails/";
        String matchIdStr = "match_id=" + matchId;
        String getHeroUrl = config.getIDota2Url() + getMatchDetails + config.getApiVersion() + matchIdStr + config.getApiAnd();
        return gateway.getForString(getHeroUrl);
    }

    @Override
    public MatchDetail getMatchDetail(long matchId) {
        String getMatchDetails = "GetMatchDetails/";
        String matchIdStr = "match_id=" + matchId;
        String getHeroUrl = config.getIDota2Url() + getMatchDetails + config.getApiVersion() + matchIdStr + config.getApiAnd();
        String response = gateway.getForString(getHeroUrl);
        MatchDetailEntity matchDetailEntity = JsonMapper.nonDefaultMapper().fromJson(response, MatchDetailEntity.class);
        return matchDetailEntity.getResult();
    }
}
