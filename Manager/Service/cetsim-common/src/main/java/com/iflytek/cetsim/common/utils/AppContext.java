package com.iflytek.cetsim.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/31.
 */
public class AppContext {

    private static AppContext instance = null;

    public String serverBasePath = "";

    private Map<String, Boolean> roomMap = new HashMap<String, Boolean>();

    public  AppContext(){

    }

    public static AppContext getInstance()
    {
        if(instance == null)
        {
            instance = new AppContext();
        }

        return instance;
    }

    public void removeRoomStatus(String roomCode)
    {
        roomMap.remove(roomCode);
    }

    public Map<String, Boolean> getRoomMap() {
        return roomMap;
    }

    public void setRoomMap(Map<String, Boolean> roomMap) {
        this.roomMap = roomMap;
    }
}
