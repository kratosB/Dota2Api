package com.service.local;

import com.bean.match.BanPickDetails;
import com.bean.match.LeaguesEntity;
import com.bean.match.Match;
import com.bean.match.MatchesEntity;

import java.util.List;

/**
 * Created on 2018/5/17.
 *
 * @author zhiqiang bao
 */
public interface ILeagueService {

    LeaguesEntity listLeague();

    MatchesEntity getLeague(int leagueId);

    List<Match> getLeagueAfter(int leagueId, long matchId);

    BanPickDetails getBanPick(int leagueId, long matchId);
}
