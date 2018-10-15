package ru.wt23.worldtrick23.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyDuel {

    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("battle_id")
    @Expose
    private String battleId;
    @SerializedName("from_user")
    @Expose
    private String fromUser;
    @SerializedName("date_end")
    @Expose
    private String dateEnd;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

}
