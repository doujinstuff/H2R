package com.stuff.doujin.h2r.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "doujin_table")
public class Doujin implements Serializable {

    @IntDef({Bookmark.NONE, Bookmark.FAVORITE, Bookmark.ON_HOLD, Bookmark.PLAN_TO_READ, Bookmark.BLACKLIST})
    @Retention(RetentionPolicy.SOURCE)
    @interface Bookmark {
        int NONE = 0;
        int FAVORITE = 1;
        int ON_HOLD = 2;
        int PLAN_TO_READ = 3;
        int BLACKLIST = 4;
    }

    public String baseCoverUrl = "https://img1.hentaicdn.com/hentai/cover";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "doujin_id")
    public String imageId;

    @Ignore
    public String imageUrl;

    @ColumnInfo(name = "doujin_title")
    public String doujinTitle;

    @ColumnInfo(name = "doujin_url")
    public String doujinUrl;

    @ColumnInfo(name = "doujin_bookmark")
    @Bookmark public int doujinBookmark;


    @Ignore
    public String doujinAuthor;

    @Ignore
    public String doujinArtist;

    @Ignore
    public String doujinGenres;

    @Ignore
    public String doujinDescription;

    @Ignore
    public String doujinStatus;

    @Ignore
    public ArrayList<Chapter> chapterList;

    @Ignore
    public ArrayList<String> doujinPages = new ArrayList<>();

    @Ignore
    public ArrayList<Doujin> relatedDoujinList = new ArrayList<>();

    public Doujin(String doujinTitle, String imageId, String doujinUrl) {
        this.doujinTitle = doujinTitle;
        this.imageUrl = baseCoverUrl + "/_S" + imageId + ".jpg";
        this.imageId = imageId;
        this.doujinUrl = doujinUrl;
        this.doujinBookmark = Bookmark.NONE;
    }

    @Override
    public int hashCode() {
        return doujinUrl.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Doujin other = (Doujin) obj;
        if (doujinUrl != other.doujinUrl) {
            return false;
        }

        return true;
    }

}
