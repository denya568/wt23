package ru.wt23.worldtrick23.Utils;

import java.util.Map;

public class UserLog {
    private int phoneId, serverId;
    private Map<Object, Object> mapList;

    public UserLog() {
    }


    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getServerId() {
        return serverId;
    }


    public void setMapList(Map<Object, Object> mapList) {
        this.mapList = mapList;
    }

    public Map<Object, Object> getMapList() {
        return mapList;
    }
}
