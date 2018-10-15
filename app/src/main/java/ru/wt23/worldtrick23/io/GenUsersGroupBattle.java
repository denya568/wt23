
package ru.wt23.worldtrick23.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenUsersGroupBattle {

    @SerializedName("raund")
    @Expose
    private String raund;
    @SerializedName("user1_id")
    @Expose
    private String user1Id;
    @SerializedName("user1_login")
    @Expose
    private String user1Login;
    @SerializedName("video1")
    @Expose
    private Object video1;
    @SerializedName("user2_id")
    @Expose
    private String user2Id;
    @SerializedName("user2_login")
    @Expose
    private String user2Login;
    @SerializedName("video2")
    @Expose
    private Object video2;
    @SerializedName("winner")
    @Expose
    private Object winner;

    public String getRaund() {
        return raund;
    }

    public void setRaund(String raund) {
        this.raund = raund;
    }

    public String getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(String user1Id) {
        this.user1Id = user1Id;
    }

    public String getUser1Login() {
        return user1Login;
    }

    public void setUser1Login(String user1Login) {
        this.user1Login = user1Login;
    }

    public Object getVideo1() {
        return video1;
    }

    public void setVideo1(Object video1) {
        this.video1 = video1;
    }

    public String getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(String user2Id) {
        this.user2Id = user2Id;
    }

    public String getUser2Login() {
        return user2Login;
    }

    public void setUser2Login(String user2Login) {
        this.user2Login = user2Login;
    }

    public Object getVideo2() {
        return video2;
    }

    public void setVideo2(Object video2) {
        this.video2 = video2;
    }

    public Object getWinner() {
        return winner;
    }

    public void setWinner(Object winner) {
        this.winner = winner;
    }

}
