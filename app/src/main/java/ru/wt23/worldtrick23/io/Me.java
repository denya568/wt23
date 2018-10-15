
package ru.wt23.worldtrick23.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Me {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("abouth")
    @Expose
    private String abouth;
    @SerializedName("date_old")
    @Expose
    private String dateOld;
    @SerializedName("date_reg")
    @Expose
    private String dateReg;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("patronymic")
    @Expose
    private String patronymic;
    @SerializedName("instagram")
    @Expose
    private String instagram;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("tricking")
    @Expose
    private String tricking;
    @SerializedName("parkour")
    @Expose
    private String parkour;
    @SerializedName("break")
    @Expose
    private String _break;
    @SerializedName("trampoline")
    @Expose
    private String trampoline;
    @SerializedName("post_mail")
    @Expose
    private String postMail;
    @SerializedName("rang")
    @Expose
    private int rang;
    @SerializedName("wins")
    @Expose
    private int wins;
    @SerializedName("fails")
    @Expose
    private int fails;
    @SerializedName("count_battles")
    @Expose
    private int countBattles;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getDateReg() {
        return dateReg;
    }

    public void setDateReg(String dateReg) {
        this.dateReg = dateReg;
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

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTricking() {
        return tricking;
    }

    public void setTricking(String tricking) {
        this.tricking = tricking;
    }

    public String getParkour() {
        return parkour;
    }

    public void setParkour(String parkour) {
        this.parkour = parkour;
    }

    public String getBreak() {
        return _break;
    }

    public void setBreak(String _break) {
        this._break = _break;
    }

    public String getTrampoline() {
        return trampoline;
    }

    public void setTrampoline(String trampoline) {
        this.trampoline = trampoline;
    }

    public String getPostMail() {
        return postMail;
    }

    public void setPostMail(String postMail) {
        this.postMail = postMail;
    }

    public int getRang() {
        return rang;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getFails() {
        return fails;
    }

    public void setFails(int fails) {
        this.fails = fails;
    }

    public int getCountBattles() {
        return countBattles;
    }

    public void setCountBattles(int countBattles) {
        this.countBattles = countBattles;
    }

}
