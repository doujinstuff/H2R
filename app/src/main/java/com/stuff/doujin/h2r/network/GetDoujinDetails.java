package com.stuff.doujin.h2r.network;

import com.stuff.doujin.h2r.data.Chapter;
import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.viewmodels.DoujinViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GetDoujinDetails implements Callback {

    public interface DoujinDetailsLoaded {
        void doujinDetailsLoaded(Doujin doujin);
    }

    private Doujin doujin;
    private DoujinDetailsLoaded doujinDetailsLoaded;
    private DoujinViewModel doujinViewModel;
    private String baseUrl = "https://hentai2read.com";

    public GetDoujinDetails(DoujinViewModel doujinViewModel) {
        this.doujinViewModel = doujinViewModel;
    }

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


            String chapterDateUpload = document.select("ul.nav-chapters > li > div.media > a").first().select("div > small").text();
            Matcher match = Pattern.compile("about (\\d+\\s+\\w+\\s+ago)").matcher(chapterDateUpload);
            if(match.find()) {
                String[] dateWords = match.group(1).split(" ");
                if (dateWords.length == 3) {
                    int timeAgo = Integer.parseInt(dateWords[0]);
                    Calendar calendar = Calendar.getInstance();
                    switch (dateWords[1]) {
                        case "minute":
                            calendar.add(Calendar.MINUTE, -1*timeAgo);
                            break;
                        case "minutes":
                            calendar.add(Calendar.MINUTE, -1*timeAgo);
                            break;
                        case "hour":
                            calendar.add(Calendar.HOUR, -1*timeAgo);
                            break;
                        case "hours":
                            calendar.add(Calendar.HOUR, -1*timeAgo);
                            break;
                        case "day":
                            calendar.add(Calendar.DAY_OF_YEAR, -1*timeAgo);
                            break;
                        case "days":
                            calendar.add(Calendar.DAY_OF_YEAR, -1*timeAgo);
                            break;
                        case "week":
                            calendar.add(Calendar.WEEK_OF_YEAR, -1*timeAgo);
                            break;
                        case "weeks":
                            calendar.add(Calendar.WEEK_OF_YEAR, -1*timeAgo);
                            break;
                        case "month":
                            calendar.add(Calendar.MONTH, -1*timeAgo);
                            break;
                        case "months":
                            calendar.add(Calendar.MONTH, -1*timeAgo);
                            break;
                        case "year":
                            calendar.add(Calendar.YEAR, -1*timeAgo);
                            break;
                        case "years":
                            calendar.add(Calendar.YEAR, -1*timeAgo);
                            break;
                    }
                    doujinChapter.chapterDateUpload = calendar.getTimeInMillis();
                } else {
                    doujinChapter.chapterDateUpload = 0;
                }
            }
            doujin.doujinLastUpdated = Math.max(doujin.doujinLastUpdated, doujinChapter.chapterDateUpload);
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
            if(!doujin.doujinId.equals(imageId)) {
                Doujin dbDoujin = doujinViewModel.findDoujin(imageId);
                if(dbDoujin == null) {
                    doujin.relatedDoujinList.add(new Doujin(title, imageId, doujinUrl));
                } else if (dbDoujin.doujinBookmark != Doujin.Bookmark.BLACKLIST){
                    doujin.relatedDoujinList.add(dbDoujin);
                }
            }
        }

        Collections.reverse(chapterList);

        doujin.chapterList = chapterList;

        if(doujinDetailsLoaded != null) {
            doujinDetailsLoaded.doujinDetailsLoaded(doujin);
        }
    }
}
