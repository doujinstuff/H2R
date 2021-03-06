package com.stuff.doujin.h2r.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stuff.doujin.h2r.MainActivity;
import com.stuff.doujin.h2r.R;
import com.stuff.doujin.h2r.adapters.DoujinAdapter;
import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.network.GetDoujinList;
import com.stuff.doujin.h2r.viewmodels.DoujinViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

public class DoujinListFragment extends Fragment implements DoujinAdapter.DoujinAdapterListener, GetDoujinList.DoujinListLoaded, SwipeRefreshLayout.OnRefreshListener {

    public void notifyDoujinSetChanged(List<Doujin> doujinList) {
        this.doujinList.clear();
        this.doujinList.addAll(doujinList);
        doujinAdapter.notifyDataSetChanged();
    }

    public interface DoujinListListener {
        void onDoujinSelected(Doujin doujin);

        void onBottomReached(DoujinListFragment doujinListFragment);

        void onRefresh();
    }

    DoujinListListener doujinListListener;
    DoujinAdapter doujinAdapter;
    List<Doujin> doujinList;
    Set<Doujin> doujinSet = new HashSet<>();
    String nextPageUrl;

    public void setDoujinListListener(DoujinListListener doujinListListener) {
        this.doujinListListener = doujinListListener;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        doujinList = activity.getData();

        doujinSet.clear();
        doujinSet.addAll(doujinList);
        nextPageUrl = getArguments().getString("nextPageUrl");
        doujinAdapter = new DoujinAdapter(getContext(), doujinList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doujin_list, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((SwipeRefreshLayout) view).setOnRefreshListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.doujin_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 4));
        doujinAdapter.setClickListener(this);
        recyclerView.setAdapter(doujinAdapter);
    }

    @Override
    public void onDoujinClick(Doujin doujin) {
        if(doujinListListener != null) {
            doujinListListener.onDoujinSelected(doujin);
        }
    }

    @Override
    public void onBottomReached() {
        if(doujinListListener != null) {
            doujinListListener.onBottomReached(this);
        }
    }

    @Override
    public void onDoujinLongClick(final Doujin doujin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final DoujinViewModel doujinViewModel = ViewModelProviders.of(this).get(DoujinViewModel.class);
        builder.setTitle("Bookmark")
                .setItems(R.array.bookmark_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != 0) {
                            if(which == 5) {
                                doujinList.remove(doujin);
                            }
                            doujin.doujinBookmark = which;
                            doujin.doujinBookmarkDate = System.currentTimeMillis();
                            doujinViewModel.insert(doujin);
                        } else {
                            doujin.doujinBookmark = which;
                            doujinViewModel.delete(doujin);
                        }
                        doujinAdapter.notifyDataSetChanged();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onRefresh() {
        if(doujinListListener != null) {
            doujinListListener.onRefresh();
        }
    }

    @Override
    public DoujinListFragment doujinListLoaded(List<Doujin> doujinList, String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
        List<Doujin> tempDoujinList = new ArrayList<>();
        tempDoujinList.addAll(doujinList);
        for(Doujin doujin : tempDoujinList) {
            if(!doujinSet.contains(doujin)) {
                doujinSet.add(doujin);
                this.doujinList.add(doujin);
            }
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                doujinAdapter.notifyDataSetChanged();
            }
        };
        mainHandler.post(myRunnable);
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

            }
        }
    }
}
