package com.stuff.doujin.h2r.network;

import android.content.Context;

import com.stuff.doujin.h2r.R;
import com.stuff.doujin.h2r.data.Doujin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GetPageList implements Callback {

    public interface ChapterPagesLoaded {
        void chapterPagesLoaded(Doujin doujin, int index, List<String> pages);
    }

    private ChapterPagesLoaded chapterPagesLoaded;
    private int chapterIndex;
    private Doujin doujin;
    private String baseUrl = "https://hentai2read.com";

    public void loadPageList(Doujin doujin, int chapterIndex, ChapterPagesLoaded chapterPagesLoaded) {
        this.chapterPagesLoaded = chapterPagesLoaded;
        this.chapterIndex = chapterIndex;
        this.doujin = doujin;
        String url = doujin.chapterList.get(chapterIndex).chapterUrl;
        OkHttpHandler.run(baseUrl + url, this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        call.cancel();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String myResponse = response.body().string();
        Document document = Jsoup.parse(myResponse);
        String data = document.select("section > div > script").first().dataNodes().get(0).getWholeData();
        data = data.substring(data.indexOf("images"));
        data = data.substring(data.indexOf("[") + 1, data.indexOf("]"));
        data = data.trim().replace("\\/", "/").replace("\"", "");
        List<String> pages = new ArrayList(Arrays.asList(data.split(",")));

        if(chapterPagesLoaded != null) {
            doujin.chapterList.get(chapterIndex).pages = pages;
            doujin.doujinPages.addAll(pages);
            chapterPagesLoaded.chapterPagesLoaded(doujin, chapterIndex, pages);
        }
    }
}
