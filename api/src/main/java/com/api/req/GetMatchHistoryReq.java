package com.api.req;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("这里的accountId可以是steamId，也可以是dota2Id")
    private String accountId;

    private String leagueId;

    private Long startAtMatchId;

    private String matchesRequested;

    private Long dateMin;

    private Long dateMax;
}
