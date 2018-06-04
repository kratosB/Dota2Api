package com.api.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created on 2018/5/23.
 * 
 * @author zhiqiang bao
 */
@Data
public class PlayerWinRateReq {

    @ApiModelProperty("优先级，dota2Id > steamId > name，都没有则查询失败")
    private String steamId;

    @ApiModelProperty("优先级，dota2Id > steamId > name，都没有则查询失败")
    private String dota2Id;

    @ApiModelProperty("优先级，dota2Id > steamId > name，都没有则查询失败")
    private String name;

    @ApiModelProperty("样本容量，例如，最近100盘，最近200盘，优先级大于duration")
    private int size;

    @ApiModelProperty("时间跨度，最近30天，最近100天")
    private int duration;

    @ApiModelProperty("是不是天梯比赛")
    private boolean ranked;

    @ApiModelProperty("组队状态，0-不考虑，1-单排，2-组排")
    private int teamStatus = 0;
}
