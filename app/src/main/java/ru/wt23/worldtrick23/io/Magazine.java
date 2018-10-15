
package ru.wt23.worldtrick23.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.wt23.worldtrick23.db.DBHelper;

public class Magazine {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("foto")
    @Expose
    private String foto;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("orders")
    @Expose
    private Object orders;
    @SerializedName("count")
    @Expose
    private String count;
    @SerializedName("price")
    @Expose
    private String price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoto() {
        return DBHelper.URL.replace("/api", "") + "images/magazine/resized/" + foto;
    }

    public String getFullFoto() {
        return DBHelper.URL.replace("/api", "") + "images/magazine/" + foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Object getOrders() {
        return orders;
    }

    public void setOrders(Object orders) {
        this.orders = orders;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
