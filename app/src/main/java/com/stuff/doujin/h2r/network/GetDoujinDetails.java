package com.stuff.doujin.h2r.network;

import android.content.Context;

import com.stuff.doujin.h2r.R;
import com.stuff.doujin.h2r.data.Chapter;
import com.stuff.doujin.h2r.data.Doujin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
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
    private String baseUrl = "https://hentai2read.com";

    public void loadDoujinDetails(DoujinDetailsLoaded doujinDetailsLoaded, Doujin doujin) {
        this.doujin = doujin;
        this.doujinDetailsLoaded = doujinDetailsLoaded;
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
        doujin.doujinDescription = infoElement.select("li:contains(Storyline) > p").text();
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

        Elements chapters = document.select("ul.nav-chapters > li > div.media > a");
        ArrayList<Chapter> chapterList = new ArrayList<>();
        for(Element chapter : chapters) {
            Chapter doujinChapter = new Chapter();
            doujinChapter.chapterName = chapter.ownText().trim();
            try {
                URI uri = new URI(chapter.attr("href"));
                String out = uri.getPath();
                if (uri.getQuery() != null)
                    out += "?" + uri.getQuery();
                if (uri.getFragment() != null)
                    out += "#" + uri.getFragment();
                doujinChapter.chapterUrl = out;
            } catch (URISyntaxException e) {
                doujinChapter.chapterUrl = chapter.attr("href");
            }


            doujinChapter.chapterDateUpload = document.select("ul.nav-chapters > li > div.media > a").first().select("div > small").text();
            chapterList.add(doujinChapter);
        }

        Elements relatedDoujins = document.select("div.col-xs-12 > div.block-content > ul.nav-users > li");
        for(Element relatedDoujin : relatedDoujins) {
            Element e = relatedDoujin.selectFirst("a");
            String imageId = e.attr("data-mid");
            String title = e.attr("data-title");
            if(title.indexOf(" [") > 0) {
                title = title.substring(0, title.indexOf(" [")).trim();
            }
            String doujinUrl = e.attr("href");
            try {
                URI uri = new URI(doujinUrl);
                String out = uri.getPath();
                if (uri.getQuery() != null)
                    out += "?" + uri.getQuery();
                if (uri.getFragment() != null)
                    out += "#" + uri.getFragment();
                doujinUrl = out;
            } catch (URISyntaxException exception) {
            }
            if(!doujin.imageId.equals(imageId)) {
                doujin.relatedDoujinList.add(new Doujin(title, imageId, doujinUrl));
            }
        }

        Collections.reverse(chapterList);

        doujin.chapterList = chapterList;

        if(doujinDetailsLoaded != null) {
            doujinDetailsLoaded.doujinDetailsLoaded(doujin);
        }
    }
}
