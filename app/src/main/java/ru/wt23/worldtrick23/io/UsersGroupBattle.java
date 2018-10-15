package ru.wt23.worldtrick23.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsersGroupBattle {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_login")
    @Expose
    private String userLogin;
    @SerializedName("coins")
    @Expose
    private String coins;

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

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

}
