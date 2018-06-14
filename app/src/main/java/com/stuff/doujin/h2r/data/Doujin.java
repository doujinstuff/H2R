package com.stuff.doujin.h2r.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Doujin implements Serializable {
    public String baseCoverUrl = "https://img1.hentaicdn.com/hentai/cover/";
    public String imageId;
    public String imageUrl;
    public String doujinTitle;
    public String doujinUrl;
    public String doujinAuthor;
    public String doujinArtist;
    public String doujinGenres;
    public String doujinDescription;
    public String doujinStatus;
    public List<Chapter> chapterList;
    public List<String> doujinPages = new ArrayList<>();

    public Doujin(String doujinTitle, String imageId, String doujinUrl) {
        this.doujinTitle = doujinTitle;
        this.imageUrl = baseCoverUrl + "_S" + imageId + ".jpg";
        this.imageId = imageId;
        this.doujinUrl = doujinUrl;
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
