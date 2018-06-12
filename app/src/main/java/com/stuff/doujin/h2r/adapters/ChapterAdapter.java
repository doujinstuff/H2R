package com.stuff.doujin.h2r.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stuff.doujin.h2r.R;
import com.stuff.doujin.h2r.data.Chapter;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Chapter> chapterList;

    public ChapterAdapter(@NonNull Context context, List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ChapterAdapter.ChapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chapters_item, parent, false);
        ChapterAdapter.ChapterViewHolder holder = new ChapterAdapter.ChapterViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ChapterAdapter.ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.titleView.setText(chapter.chapterName);
        holder.pageView.setText(chapter.chapterUrl);
    }


    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    class ChapterViewHolder extends RecyclerView.ViewHolder
    {

        TextView titleView;
        TextView pageView;

        public ChapterViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.chapter_title);
            pageView = itemView.findViewById(R.id.chapter_pages);
        }
    }
}
