package com.stuff.doujin.h2r.network;

import com.stuff.doujin.h2r.data.Doujin;

import java.util.ArrayList;

public interface DoujinListLoaded {
    void doujinListLoaded(ArrayList<Doujin> doujinList, String nextPageUrl);
}
