package com.stuff.doujin.h2r.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stuff.doujin.h2r.R;
import com.stuff.doujin.h2r.adapters.ChapterAdapter;
import com.stuff.doujin.h2r.adapters.DoujinAdapter;
import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.network.GetPageList;

import java.util.List;

import me.gujun.android.taggroup.TagGroup;

public class DoujinDetailsFragment extends Fragment implements DoujinAdapter.DoujinAdapterListener {

    private Doujin doujin;
    private DoujinAdapter doujinAdapter;
    private DoujinListFragment.DoujinListListener relatedDoujinListListener;

    public void setDoujinListListener(DoujinListFragment.DoujinListListener doujinListListener) {
        this.relatedDoujinListListener = doujinListListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doujin = (Doujin) getArguments().getSerializable("doujin");
        doujinAdapter = new DoujinAdapter(getContext(), doujin.relatedDoujinList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doujin_details, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String unknownText = getResources().getString(R.string.unknown);

        if(doujin.doujinTitle != null && !doujin.doujinTitle.isEmpty()) {
            ((TextView) view.findViewById(R.id.manga_full_title)).setText(doujin.doujinTitle);
        } else {
            ((TextView) view.findViewById(R.id.manga_full_title)).setText(unknownText);
        }

        if(doujin.doujinAuthor != null && !doujin.doujinAuthor.isEmpty()) {
            ((TextView) view.findViewById(R.id.manga_author)).setText(doujin.doujinAuthor);
        } else {
            ((TextView) view.findViewById(R.id.manga_author)).setText(unknownText);
        }

        if(doujin.doujinArtist != null && !doujin.doujinArtist.isEmpty()) {
            ((TextView) view.findViewById(R.id.manga_artist)).setText(doujin.doujinArtist);
        } else {
            ((TextView) view.findViewById(R.id.manga_artist)).setText(unknownText);
        }

        if(doujin.doujinStatus != null && !doujin.doujinStatus.isEmpty()) {
            ((TextView) view.findViewById(R.id.manga_status)).setText(doujin.doujinStatus);
        } else {
            ((TextView) view.findViewById(R.id.manga_status)).setText(unknownText);
        }

        if(doujin.doujinPages != null && !doujin.doujinPages.isEmpty()) {
            ((TextView) view.findViewById(R.id.manga_pages)).setText(String.valueOf(doujin.doujinPages.size()));
        } else {
            ((TextView) view.findViewById(R.id.manga_pages)).setText("0");
        }

        if(doujin.imageUrl != null && !doujin.imageUrl.isEmpty()) {
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            ImageView doujinCover = view.findViewById(R.id.manga_cover);
            ImageView doujinBackdrop = view.findViewById(R.id.backdrop);


            Glide.with(view.getContext()).clear(doujinCover);
            Glide.with(view.getContext()).load(doujin.imageUrl).apply(options).into(doujinCover);

            doujinCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), doujin.chapterList.get(0).chapterName, Toast.LENGTH_SHORT).show();
                }
            });

            Glide.with(view.getContext()).clear(doujinBackdrop);
            Glide.with(view.getContext()).load(doujin.imageUrl).apply(options).into(doujinBackdrop);
        }

        if(doujin.doujinGenres != null && !doujin.doujinGenres.isEmpty()) {
            ((TagGroup) view.findViewById(R.id.manga_genres_tags)).setTags(doujin.doujinGenres.split(", "));
        }

        RecyclerView recyclerView = view.findViewById(R.id.doujin_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 4));
        doujinAdapter.setClickListener(this);
        recyclerView.setAdapter(doujinAdapter);
    }

    @Override
    public void onDoujinClick(Doujin doujin) {
        if(relatedDoujinListListener != null) {
            relatedDoujinListListener.onDoujinSelected(doujin);
        }
    }

    @Override
    public void onBottomReached() {
    }
}
