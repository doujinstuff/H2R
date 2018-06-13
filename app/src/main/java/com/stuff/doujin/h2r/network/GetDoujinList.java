package com.stuff.doujin.h2r.network;

import android.content.Context;

import com.stuff.doujin.h2r.R;
import com.stuff.doujin.h2r.data.Doujin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public final class GetDoujinList implements Callback {

    public interface DoujinListLoaded {
        void doujinListLoaded(ArrayList<Doujin> doujinList, String nextPageUrl);
    }

    private ArrayList<Doujin> doujinList = new ArrayList<>();
    private DoujinListLoaded doujinListLoaded;
    private Context context;

    public GetDoujinList(Context context) {
        this.context = context;
    }

    public void loadDoujinList(DoujinListLoaded doujinListLoaded, String url) {
        this.doujinListLoaded = doujinListLoaded;
        OkHttpHandler.run(context.getResources().getString(R.string.base_url) + url, this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        call.cancel();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String myResponse = response.body().string();
        Document document = Jsoup.parse(myResponse);
        String nextPageUrl = null;
        if(!document.select("a#js-linkNext").isEmpty()) {
            nextPageUrl = document.select("a#js-linkNext").first().attr("href");
            try {
                URI uri = new URI(nextPageUrl);
                String out = uri.getPath();
                if (uri.getQuery() != null)
                    out += "?" + uri.getQuery();
                if (uri.getFragment() != null)
                    out += "#" + uri.getFragment();
                nextPageUrl = out;
            } catch (URISyntaxException exception) {
            }
        }
        Elements elements = document.select("ul.nav-users li.ribbon");
        if(elements.isEmpty()) {
            elements = document.select("div.img-container div.img-overlay a");
        }
        for(Element element : elements) {
            Element e;
            if(element.is("a")) {
                e = element.selectFirst("h2.mangaPopover");
            } else {
                e = element.selectFirst("a.mangaPopover");
            }
            String imageId = e.attr("data-mid");
            String title = e.attr("data-title").trim();
            String doujinUrl = e.attr("href");
            if(doujinUrl.isEmpty()) {
                doujinUrl = element.attr("href");
            }

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

            if(title.indexOf(" [") > 0) {
                title = title.substring(0, title.indexOf(" [")).trim();
            }
            doujinList.add(new Doujin(title, imageId, doujinUrl));
        }

        if(doujinListLoaded != null) {
            doujinListLoaded.doujinListLoaded(doujinList, nextPageUrl);
        }
    }
}
