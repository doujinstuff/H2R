package com.stuff.doujin.h2r.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stuff.doujin.h2r.DoujinReaderActivity;
import com.stuff.doujin.h2r.R;
import com.stuff.doujin.h2r.adapters.DoujinAdapter;
import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.viewmodels.DoujinViewModel;

import java.text.DateFormat;

import me.gujun.android.taggroup.TagGroup;

import static android.app.Activity.RESULT_OK;

public class DoujinDetailsFragment extends Fragment implements DoujinAdapter.DoujinAdapterListener, View.OnClickListener, TagGroup.OnTagClickListener, AdapterView.OnItemSelectedListener, View.OnLongClickListener {

    public interface SearchDetailsListener {
        void onAuthorSearch(String author);

        void onArtistSearch(String artist);

        void onCategorySearch(String category);
    }

    private Doujin doujin;
    private DoujinAdapter doujinAdapter;
    private DoujinListFragment.DoujinListListener relatedDoujinListListener;
    private SearchDetailsListener searchDetailsListenerListener;
    private Spinner bookmarkSpinner;
    private TextView currentPageView;
    private TextView bookmarkDateView;
    private TextView bookmarkDateLabelView;
    private String baseUrl = "https://hentai2read.com";

    public void setDoujinListListener(DoujinListFragment.DoujinListListener doujinListListener) {
        this.relatedDoujinListListener = doujinListListener;
    }

    public void setSearchDetailsListener(SearchDetailsListener searchDetailsListener) {
        this.searchDetailsListenerListener = searchDetailsListener;
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

        if (doujin.doujinTitle != null && !doujin.doujinTitle.isEmpty()) {
            ((TextView) view.findViewById(R.id.manga_full_title)).setText(doujin.doujinTitle);
        } else {
            ((TextView) view.findViewById(R.id.manga_full_title)).setText(unknownText);
        }

        if (doujin.doujinAuthor != null && !doujin.doujinAuthor.isEmpty()) {
            ((TextView) view.findViewById(R.id.manga_author)).setText(doujin.doujinAuthor);
            view.findViewById(R.id.manga_author).setOnClickListener(this);
        } else {
            ((TextView) view.findViewById(R.id.manga_author)).setText(unknownText);
        }

        if (doujin.doujinArtist != null && !doujin.doujinArtist.isEmpty()) {
            ((TextView) view.findViewById(R.id.manga_artist)).setText(doujin.doujinArtist);
            view.findViewById(R.id.manga_artist).setOnClickListener(this);
        } else {
            ((TextView) view.findViewById(R.id.manga_artist)).setText(unknownText);
        }

        if (doujin.doujinLastUpdated != 0L) {
            ((TextView) view.findViewById(R.id.manga_last_update)).setText(DateFormat.getDateInstance(DateFormat.SHORT).format(doujin.doujinLastUpdated));
        } else {
            ((TextView) view.findViewById(R.id.manga_last_update)).setText("Unknown");
        }

        if (doujin.doujinStatus != null && !doujin.doujinStatus.isEmpty()) {
            ((TextView) view.findViewById(R.id.manga_status)).setText(doujin.doujinStatus);
        } else {
            ((TextView) view.findViewById(R.id.manga_status)).setText(unknownText);
        }

        currentPageView = view.findViewById(R.id.manga_current_page);
        currentPageView.setText(String.valueOf(doujin.doujinPage));

        if (doujin.doujinPages != null && !doujin.doujinPages.isEmpty()) {
            ((TextView) view.findViewById(R.id.manga_pages)).setText(String.valueOf(doujin.doujinPages.size()));
        } else {
            ((TextView) view.findViewById(R.id.manga_pages)).setText("0");
        }

        bookmarkDateView = view.findViewById(R.id.manga_bookmark_date);
        bookmarkDateLabelView = view.findViewById(R.id.manga_bookmark_date_label);
        if (doujin.doujinBookmarkDate != 0L) {
            bookmarkDateView.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(doujin.doujinBookmarkDate));
            bookmarkDateView.setVisibility(View.VISIBLE);
            bookmarkDateLabelView.setVisibility(View.VISIBLE);
        } else {
            bookmarkDateView.setVisibility(View.GONE);
            bookmarkDateLabelView.setVisibility(View.GONE);
        }

        if (doujin.imageUrl != null && !doujin.imageUrl.isEmpty()) {
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            ImageView doujinCover = view.findViewById(R.id.manga_cover);
            ImageView doujinBackdrop = view.findViewById(R.id.backdrop);


            Glide.with(view.getContext()).clear(doujinCover);
            Glide.with(view.getContext()).load(doujin.imageUrl).apply(options).into(doujinCover);

            doujinCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DoujinReaderActivity.class);
                    intent.putExtra("PAGE LIST", doujin.doujinPages);
                    intent.putExtra("CURRENT PAGE", doujin.doujinPage);
                    startActivityForResult(intent, 1);

                }
            });

            doujinCover.setOnLongClickListener(this);

            Glide.with(view.getContext()).clear(doujinBackdrop);
            Glide.with(view.getContext()).load(doujin.imageUrl).apply(options).into(doujinBackdrop);
        }

        if (doujin.doujinGenres != null && !doujin.doujinGenres.isEmpty()) {
            ((TagGroup) view.findViewById(R.id.manga_genres_tags)).setTags(doujin.doujinGenres.split(", "));
            ((TagGroup) view.findViewById(R.id.manga_genres_tags)).setOnTagClickListener(this);
        }

        bookmarkSpinner = view.findViewById(R.id.bookmark_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.bookmark_array, R.layout.spinner_text_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookmarkSpinner.setAdapter(adapter);
        bookmarkSpinner.setSelection(doujin.doujinBookmark);
        bookmarkSpinner.setOnItemSelectedListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.doujin_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 4));
        doujinAdapter.setClickListener(this);
        recyclerView.setAdapter(doujinAdapter);
    }

    @Override
    public void onDoujinClick(Doujin doujin) {
        if (relatedDoujinListListener != null) {
            relatedDoujinListListener.onDoujinSelected(doujin);
        }
    }

    @Override
    public void onBottomReached() {
    }

    @Override
    public void onDoujinLongClick(Doujin doujin) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.manga_artist) {
            searchDetailsListenerListener.onArtistSearch(((TextView) v).getText().toString());
        } else if (v.getId() == R.id.manga_author) {
            searchDetailsListenerListener.onAuthorSearch(((TextView) v).getText().toString());
        }
    }

    @Override
    public void onTagClick(String tag) {
        searchDetailsListenerListener.onCategorySearch(tag);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        DoujinViewModel doujinViewModel = ViewModelProviders.of(this).get(DoujinViewModel.class);
        if(position == doujin.doujinBookmark) {
            return;
        }
        if (position != 0) {
            doujin.doujinBookmark = position;
            doujin.doujinBookmarkDate = System.currentTimeMillis();
            doujinViewModel.insert(doujin);
            bookmarkDateView.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(doujin.doujinBookmarkDate));
            bookmarkDateView.setVisibility(View.VISIBLE);
            bookmarkDateLabelView.setVisibility(View.VISIBLE);
        } else {
            doujinViewModel.delete(doujin);
            bookmarkDateView.setVisibility(View.GONE);
            bookmarkDateLabelView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int currentPage = data.getIntExtra("CURRENT PAGE", 0);
                int bookmark = data.getIntExtra("BOOKMARK", -1);
                DoujinViewModel doujinViewModel = ViewModelProviders.of(this).get(DoujinViewModel.class);
                doujin.doujinPage = currentPage;
                currentPageView.setText(String.valueOf(doujin.doujinPage));
                if(bookmark > 0) {
                    doujin.doujinBookmark = bookmark;
                    bookmarkSpinner.setSelection(doujin.doujinBookmark);
                }
                if (doujin.doujinBookmark != 0) {
                    doujin.doujinBookmarkDate = System.currentTimeMillis();
                    doujinViewModel.insert(doujin);
                }
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + doujin.doujinUrl));
        startActivity(browserIntent);
        return false;
    }
}
