package com.api.vo;

import lombok.Data;

/**
 * Created on 2018/5/23.
 * @author zhiqiang bao
 */
@Data
public class PlayerWinRateVo {

    private String playerName;

    private int winCount;

    private int loseCount;

    private double winRate;
}
