package com.stuff.doujin.h2r.network;

import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.viewmodels.DoujinViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class GoThroughQueue implements Callback {

    public interface DoujinQueue {
        void doujinUpdated(Doujin doujin);
    }

    private Doujin doujin;
    private DoujinQueue doujinQueue;
    private String baseUrl = "https://hentai2read.com";

    public GoThroughQueue(DoujinQueue doujinQueue) {
        this.doujinQueue = doujinQueue;
    }

    public void loadDoujinDetails(Doujin doujin) {
        this.doujin = doujin;
        OkHttpHandler.run(baseUrl + doujin.doujinUrl, this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        call.cancel();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String myResponse = response.body().string();
        Document document = Jsoup.parse(myResponse);
        Element infoElement = document.select("ul.list-simple-mini").first();
        doujin.doujinAuthor = infoElement.select("li:contains(Author) > a").text();
        doujin.doujinArtist = infoElement.select("li:contains(Artist) > a").text();
        doujin.doujinStatus = infoElement.select("li:contains(Status) > a").text();
        doujin.relatedDoujinList.clear();

        Elements genres = infoElement.select("li:contains(Category) > a, li:contains(Content) > a");
        for(Element genre : genres) {
            if(doujin.doujinGenres == null) {
                doujin.doujinGenres = genre.text();
            } else {
                doujin.doujinGenres += ", " + genre.text();
            }
        }

        if(doujinQueue != null) {
            doujinQueue.doujinUpdated(doujin);
        }

    }


}
