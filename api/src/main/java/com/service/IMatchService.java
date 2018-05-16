package com.service;

import com.api.req.GetMatchHistoryReq;
import com.bean.match.BanPickDetails;
import com.bean.match.LeaguesEntity;
import com.bean.match.Match;
import com.bean.match.MatchDetail;
import com.bean.match.MatchesEntity;

import java.util.List;

/**
 * Created on 2018/5/16.
 * 
 * @author zhiqiang bao
 */
public interface IMatchService {

    public LeaguesEntity listLeague();

    public MatchesEntity getLeague(int leagueId);

    public String getMatchHistory(GetMatchHistoryReq getMatchHistoryReq);

    public List<Match> getLeagueAfter(int leagueId, long matchId);

    public MatchDetail getMatchDetail(long matchId);

    public String getMatchHistoryByAllHero(String steamId);

    public BanPickDetails getBanPick(int leagueId, long matchId);

    public void updateMatchDetail(String steamId, int heroId);

    public List<Long> getMatchIdBySteamIdAndHeroId(String steamId, int heroId, Long startAtMatchId);

    public void updateMatchDetailByMatchId(Long matchId);

}
