package com.stuff.doujin.h2r.data;

import java.io.Serializable;
import java.util.List;

public class Chapter implements Serializable {
    public String chapterUrl;
    public String chapterName;
    public long chapterDateUpload;
    public List<String> pages;
}
