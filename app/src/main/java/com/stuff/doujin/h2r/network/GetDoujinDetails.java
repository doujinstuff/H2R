package com.stuff.doujin.h2r.network;

import com.stuff.doujin.h2r.data.Chapter;
import com.stuff.doujin.h2r.data.Doujin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GetDoujinDetails implements Callback {

    public interface DoujinDetailsLoaded {
        void doujinDetailsLoaded(Doujin doujin);
    }

    private Doujin doujin;
    private DoujinDetailsLoaded doujinDetailsLoaded;

    public void loadDoujinDetails(DoujinDetailsLoaded doujinDetailsLoaded, Doujin doujin) {
        this.doujin = doujin;
        this.doujinDetailsLoaded = doujinDetailsLoaded;
        OkHttpHandler.run(doujin.doujinUrl, this);
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
        doujin.doujinDescription = infoElement.select("li:contains(Storyline) > p").text();
        doujin.doujinStatus = infoElement.select("li:contains(Status) > a").text();

        Elements genres = infoElement.select("li:contains(Category) > a, li:contains(Content) > a");
        for(Element genre : genres) {
            if(doujin.doujinGenres == null) {
                doujin.doujinGenres = genre.text();
            } else {
                doujin.doujinGenres += ", " + genre.text();
            }
        }

        Elements chapters = document.select("ul.nav-chapters > li > div.media > a");
        List<Chapter> chapterList = new ArrayList<>();
        for(Element chapter : chapters) {
            Chapter doujinChapter = new Chapter();
            doujinChapter.chapterName = chapter.ownText().trim();
            doujinChapter.chapterUrl = chapter.attr("href");
            doujinChapter.chapterDateUpload = document.select("ul.nav-chapters > li > div.media > a").first().select("div > small").text();
            chapterList.add(doujinChapter);
        }
        doujin.chapterList = chapterList;

        if(doujinDetailsLoaded != null) {
            doujinDetailsLoaded.doujinDetailsLoaded(doujin);
        }
    }
}
