
package ru.wt23.worldtrick23.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IndividBattle {

    @SerializedName("battle_id")
    @Expose
    private String battleId;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("from_user_id")
    @Expose
    private String fromUserId;
    @SerializedName("from_user_login")
    @Expose
    private String fromUserLogin;
    @SerializedName("to_user_id")
    @Expose
    private String toUserId;
    @SerializedName("to_user_login")
    @Expose
    private String toUserLogin;
    @SerializedName("video_from")
    @Expose
    private String videoFrom;
    @SerializedName("video_to")
    @Expose
    private String videoTo;
    @SerializedName("winner")
    @Expose
    private String winner;

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserLogin() {
        return fromUserLogin;
    }

    public void setFromUserLogin(String fromUserLogin) {
        this.fromUserLogin = fromUserLogin;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserLogin() {
        return toUserLogin;
    }

    public void setToUserLogin(String toUserLogin) {
        this.toUserLogin = toUserLogin;
    }

    public String getVideoFrom() {
        return videoFrom;
    }

    public void setVideoFrom(String videoFrom) {
        this.videoFrom = videoFrom;
    }

    public String getVideoTo() {
        return videoTo;
    }

    public void setVideoTo(String videoTo) {
        this.videoTo = videoTo;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

}
