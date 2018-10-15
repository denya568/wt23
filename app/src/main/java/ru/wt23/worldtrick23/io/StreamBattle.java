
package ru.wt23.worldtrick23.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StreamBattle {

    @SerializedName("battle_id")
    @Expose
    private String battleId;
    @SerializedName("rtmp")
    @Expose
    private String rtmp;
    @SerializedName("name_battle")
    @Expose
    private String nameBattle;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("count_users")
    @Expose
    private String countUsers;
    @SerializedName("date_start")
    @Expose
    private String dateStart;
    @SerializedName("status")
    @Expose
    private String status;

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public String getRtmp() {
        return rtmp;
    }

    public void setRtmp(String rtmp) {
        this.rtmp = rtmp;
    }

    public String getNameBattle() {
        return nameBattle;
    }

    public void setNameBattle(String nameBattle) {
        this.nameBattle = nameBattle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountUsers() {
        return countUsers;
    }

    public void setCountUsers(String countUsers) {
        this.countUsers = countUsers;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
