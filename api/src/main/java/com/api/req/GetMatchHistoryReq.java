package com.api.req;

import lombok.Data;

/**
 * Created on 2018/3/5.
 *
 * @author zhiqiang bao
 */
@Data
public class GetMatchHistoryReq {

    private Long heroId;

    private Integer gameMode;

    private Integer skill;

    private String minPlayers;

    private String accountId;

    private String leagueId;

    private Long startAtMatchId;

    private String matchesRequested;

    private Long dateMin;

    private Long dateMax;
}
