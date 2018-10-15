package ru.wt23.worldtrick23.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WT23User {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("abouth")
    @Expose
    private String abouth;
    @SerializedName("date_old")
    @Expose
    private String dateOld;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("instagram")
    @Expose
    private String instagram;
    @SerializedName("rang")
    @Expose
    private Integer rang;
    @SerializedName("wins")
    @Expose
    private Integer wins;
    @SerializedName("fails")
    @Expose
    private Integer fails;
    @SerializedName("categories")
    @Expose
    private String categories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAbouth() {
        return abouth;
    }

    public void setAbouth(String abouth) {
        this.abouth = abouth;
    }

    public String getDateOld() {
        return dateOld;
    }

    public void setDateOld(String dateOld) {
        this.dateOld = dateOld;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public Integer getRang() {
        return rang;
    }

    public void setRang(Integer rang) {
        this.rang = rang;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getFails() {
        return fails;
    }

    public void setFails(Integer fails) {
        this.fails = fails;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

}
