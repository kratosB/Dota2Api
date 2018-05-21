package com.service.steam;

/**
 * Created on 2018/5/21.
 * 
 * @author zhiqiang bao
 */
public interface ISteamHeroItemService {

    /**
     * 调用steam接口，更新item表的数据
     */
    void updateItemData();

    /**
     * 调用steam接口，更新hero表的数据
     */
    void updateHeroData();
}
