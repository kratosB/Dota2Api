package com.service.steam.impl;

import com.api.req.GetMatchHistoryReq;
import com.bean.match.MatchDetail;
import com.bean.match.MatchDetailEntity;
import com.config.Configuration;
import com.service.steam.ISteamMatchService;
import com.util.JsonMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created on 2018/5/21.
 * 
 * @author zhiqiang bao
 */
@Service
public class SteamMatchServiceImpl implements ISteamMatchService {

    private Configuration configuration;

    private RestTemplate restTemplate = new RestTemplate();

    public SteamMatchServiceImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getMatchHistory(GetMatchHistoryReq getMatchHistoryReq) {
        String getMatchHistory = "GetMatchHistory/";
        StringBuilder sb = new StringBuilder();
        sb.append(configuration.getIDota2Url()).append(getMatchHistory).append(configuration.getApiVersion())
                .append(configuration.getApiKey());
        if (getMatchHistoryReq.getAccountId() != null) {
            sb.append(configuration.getApiAnd()).append("account_id=").append(getMatchHistoryReq.getAccountId());
        }
        if (getMatchHistoryReq.getGameMode() != null) {
            sb.append(configuration.getApiAnd()).append("game_mode=").append(getMatchHistoryReq.getGameMode());
        }
        if (getMatchHistoryReq.getHeroId() != null) {
            sb.append(configuration.getApiAnd()).append("hero_id=").append(getMatchHistoryReq.getHeroId());
        }
        if (getMatchHistoryReq.getLeagueId() != null) {
            sb.append(configuration.getApiAnd()).append("league_id=").append(getMatchHistoryReq.getLeagueId());
        }
        if (getMatchHistoryReq.getMatchesRequested() != null) {
            sb.append(configuration.getApiAnd()).append("matches_requested=").append(getMatchHistoryReq.getMatchesRequested());
        }
        if (getMatchHistoryReq.getMinPlayers() != null) {
            sb.append(configuration.getApiAnd()).append("min_players=").append(getMatchHistoryReq.getMinPlayers());
        }
        if (getMatchHistoryReq.getSkill() != null) {
            sb.append(configuration.getApiAnd()).append("skill=").append(getMatchHistoryReq.getSkill());
        }
        if (getMatchHistoryReq.getStartAtMatchId() != null) {
            sb.append(configuration.getApiAnd()).append("start_at_match_id=").append(getMatchHistoryReq.getStartAtMatchId());
        }
        if (getMatchHistoryReq.getDateMax() != null) {
            sb.append(configuration.getApiAnd()).append("date_max=").append(getMatchHistoryReq.getDateMax());
        }
        if (getMatchHistoryReq.getDateMin() != null) {
            sb.append(configuration.getApiAnd()).append("date_min=").append(getMatchHistoryReq.getDateMin());
        }
        return restTemplate.getForObject(sb.toString(), String.class);
    }

    @Override
    public String getMatchDetailByMatchId(long matchId) {
        String getMatchDetails = "GetMatchDetails/";
        String matchIdStr = "match_id=" + matchId;
        String getHeroUrl = configuration.getIDota2Url() + getMatchDetails + configuration.getApiVersion()
                + configuration.getApiKey() + configuration.getApiAnd() + matchIdStr;
        return restTemplate.getForObject(getHeroUrl, String.class);
    }

    @Override
    public MatchDetail getMatchDetail(long matchId) {
        String getMatchDetails = "GetMatchDetails/";
        String matchIdStr = "match_id=" + matchId;
        String getHeroUrl = configuration.getIDota2Url() + getMatchDetails + configuration.getApiVersion()
                + configuration.getApiKey() + configuration.getApiAnd() + matchIdStr;
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        MatchDetailEntity matchDetailEntity = JsonMapper.nonDefaultMapper().fromJson(response, MatchDetailEntity.class);
        return matchDetailEntity.getResult();
    }
}
