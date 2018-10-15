package ru.wt23.worldtrick23.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryIndividBattle {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_login")
    @Expose
    private String userLogin;
    @SerializedName("battle_id")
    @Expose
    private String battleId;
    @SerializedName("accept")
    @Expose
    private String accept;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

}
