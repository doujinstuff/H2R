package com.stuff.doujin.h2r.network;

import android.content.Context;

import com.stuff.doujin.h2r.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GetPageList implements Callback {

    private Context context;

    public GetPageList(Context context) {
        this.context = context;
    }

    public void loadPageList(String url) {
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
        String data = document.select("section > div > script").first().dataNodes().get(0).getWholeData();
        data = data.substring(data.indexOf("images"));
        data = data.substring(data.indexOf("[") + 1, data.indexOf("]"));
        List<String> pages = Arrays.asList(data.split(","));
    }
}
