package com.api.req;

import lombok.Data;

/**
 * Created on 2018/5/22.
 * @author zhiqiang bao
 */
@Data
public class GetPlayerInfoReq {

    private String playerName;

    private String steamId;

    private String dotaId;


}
