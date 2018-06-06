package com.stuff.doujin.h2r.network;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkHttpHandler {

    public static void run(String url, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64)");
        Request request = new Request.Builder()
                .url(url)
                .headers(headerBuilder.build())
                .build();

        client.newCall(request).enqueue(callback);
    }
}
