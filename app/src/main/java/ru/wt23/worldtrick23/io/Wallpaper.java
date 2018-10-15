package ru.wt23.worldtrick23.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.wt23.worldtrick23.db.DBHelper;

public class Wallpaper {

    @SerializedName("file")
    @Expose
    private String file;

    public void setFile(String file) {
        this.file = file;
    }

    public String getLink() {
        return DBHelper.URL.replace("/api", "") + "images/oboi/" + file;
    }

    public String getFile() {
        return file;
    }

    public String getSmallImg() {
        return DBHelper.URL.replace("/api", "") + "images/oboi/resized/" + file;
    }

}
