package com.stuff.doujin.h2r;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.fragments.DoujinDetailsFragment;
import com.stuff.doujin.h2r.fragments.DoujinListFragment;
import com.stuff.doujin.h2r.fragments.LoadingFragment;
import com.stuff.doujin.h2r.network.GetDoujinDetails;
import com.stuff.doujin.h2r.network.GetDoujinList;
import com.stuff.doujin.h2r.network.GetPageList;
import com.stuff.doujin.h2r.network.GoThroughQueue;
import com.stuff.doujin.h2r.viewmodels.DoujinViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DoujinListFragment.DoujinListListener, GetDoujinList.DoujinListLoaded, GetDoujinDetails.DoujinDetailsLoaded, GetPageList.ChapterPagesLoaded, SearchView.OnQueryTextListener, DoujinDetailsFragment.SearchDetailsListener, GoThroughQueue.DoujinQueue {

    String url;
    GetDoujinList getDoujinList;
    GetDoujinDetails getDoujinDetails;
    GetPageList getPageList;
    GoThroughQueue goThroughQueue;
    DoujinViewModel doujinViewModel;
    private List<Doujin> data;
    Set<String> blackListTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        doujinViewModel = ViewModelProviders.of(this).get(DoujinViewModel.class);
        getDoujinList = new GetDoujinList(doujinViewModel);
        getDoujinDetails = new GetDoujinDetails(doujinViewModel);
        getPageList = new GetPageList();
        goThroughQueue = new GoThroughQueue(this);
        blackListTags = new HashSet<>();
        blackListTags.addAll(Arrays.asList(getResources().getStringArray(R.array.blacklist_tags_array)));


        if (savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            LoadingFragment fragment = new LoadingFragment();

            getSupportFragmentManager().beginTransaction().add(R.id.flContainer, fragment).commit();

            url = getResources().getString(R.string.latest_start_url);
            getDoujinList.loadDoujinList(this, url, true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_show_search).getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_export_json) {
            doujinViewModel.getDoujinsForExport(getBaseContext());
        } else if(id == R.id.action_import_json) {
            importJson();
        } else if(id == R.id.action_view_completed) {
            startLoadingFragment();
            doujinViewModel.getCompletedDoujins().observe(this, new Observer<List<Doujin>>() {
                DoujinListFragment fragment;
                @Override
                public void onChanged(@Nullable List<Doujin> doujinList) {
                    if(fragment == null) {
                        fragment = doujinListLoaded(doujinList, null);
                    } else {
                        fragment.notifyDoujinSetChanged(doujinList);
                    }
                }
            });
        } else if(id == R.id.action_view_blacklist) {
            startLoadingFragment();
            doujinViewModel.getBlacklistDoujins().observe(this, new Observer<List<Doujin>>() {
                DoujinListFragment fragment;
                @Override
                public void onChanged(@Nullable List<Doujin> doujinList) {
                    if(fragment == null) {
                        fragment = doujinListLoaded(doujinList, null);
                    } else {
                        fragment.notifyDoujinSetChanged(doujinList);
                    }
                }
            });
        } else if(id == R.id.action_delete_all) {
            doujinViewModel.deleteAll();
        } else if (id == R.id.action_update_queue) {
            startLoadingFragment();
            doujinViewModel.getQueuedDoujins().observe(this, new Observer<List<Doujin>>() {
                DoujinListFragment fragment;
                @Override
                public void onChanged(@Nullable List<Doujin> doujinList) {
                    if(fragment == null) {
                        fragment = doujinListLoaded(doujinList, null);
                    } else {
                        fragment.notifyDoujinSetChanged(doujinList);
                    }
                    if(!doujinList.isEmpty()) {
                        goThroughQueue.loadDoujinDetails(doujinList.get(0));
                    }
                }
            });
        } else if (id == R.id.action_update_plan_to_read) {
            startLoadingFragment();
            doujinViewModel.getPlanToReadDoujins().observe(this, new Observer<List<Doujin>>() {
                DoujinListFragment fragment;
                @Override
                public void onChanged(@Nullable List<Doujin> doujinList) {
                    if(fragment == null) {
                        fragment = doujinListLoaded(doujinList, null);
                    } else {
                        fragment.notifyDoujinSetChanged(doujinList);
                    }
                    if(!doujinList.isEmpty() && !DateUtils.isToday(doujinList.get(0).doujinBookmarkDate)) {
                        goThroughQueue.loadDoujinDetails(doujinList.get(0));
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_latest) {
            startLoadingFragment();
            url = getResources().getString(R.string.latest_start_url);
            getDoujinList.loadDoujinList(this, url, true);
        } else if (id == R.id.nav_popular) {
            startLoadingFragment();
            url = getResources().getString(R.string.popular_start_url);
            getDoujinList.loadDoujinList(this, url, true);
        } else if (id == R.id.nav_favorite) {
            startLoadingFragment();
            doujinViewModel.getFavoriteDoujins().observe(this, new Observer<List<Doujin>>() {
                DoujinListFragment fragment;
                @Override
                public void onChanged(@Nullable List<Doujin> doujinList) {
                    if(fragment == null) {
                        fragment = doujinListLoaded(doujinList, null);
                    } else {
                        fragment.notifyDoujinSetChanged(doujinList);
                    }
                }
            });
        } else if (id == R.id.nav_on_hold) {
            startLoadingFragment();
            doujinViewModel.getOnHoldDoujins().observe(this, new Observer<List<Doujin>>() {
                DoujinListFragment fragment;
                @Override
                public void onChanged(@Nullable List<Doujin> doujinList) {
                    if(fragment == null) {
                        fragment = doujinListLoaded(doujinList, null);
                    } else {
                        fragment.notifyDoujinSetChanged(doujinList);
                    }
                }
            });
        } else if (id == R.id.nav_plan_to_read) {
            startLoadingFragment();
            doujinViewModel.getPlanToReadDoujins().observe(this, new Observer<List<Doujin>>() {
                DoujinListFragment fragment;
                @Override
                public void onChanged(@Nullable List<Doujin> doujinList) {
                    if(fragment == null) {
                        fragment = doujinListLoaded(doujinList, null);
                    } else {
                        fragment.notifyDoujinSetChanged(doujinList);
                    }
                }
            });
        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDoujinSelected(Doujin doujin) {
        Toast.makeText(this, doujin.doujinTitle, Toast.LENGTH_SHORT).show();
        getDoujinDetails.loadDoujinDetails(this, doujin);

        LoadingFragment fragment = new LoadingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBottomReached(DoujinListFragment doujinListFragment) {
        getDoujinList.loadDoujinList(doujinListFragment, doujinListFragment.getNextPageUrl(), false);
    }

    @Override
    public void onRefresh() {
        if(url != null) {
            startLoadingFragment();
            getDoujinList.loadDoujinList(this, url, true);
        }
    }

    @Override
    public DoujinListFragment doujinListLoaded(List<Doujin> doujinList, String nextPageUrl) {
        Bundle bundle = new Bundle();
//        bundle.putSerializable("doujins", (Serializable) doujinList);
        bundle.putString("nextPageUrl", nextPageUrl);
        data = doujinList;
        DoujinListFragment fragment = new DoujinListFragment();
        fragment.setDoujinListListener(this);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
        return fragment;
    }

    private void startLoadingFragment() {
        LoadingFragment fragment = new LoadingFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();

    }

    @Override
    public void doujinDetailsLoaded(Doujin doujin) {
        if(doujin.doujinPages.isEmpty()) {
            getPageList.loadPageList(doujin, 0, this);
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("doujin", doujin);

            DoujinDetailsFragment fragment = new DoujinDetailsFragment();
            fragment.setDoujinListListener(this);
            fragment.setSearchDetailsListener(this);
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
    }

    @Override
    public void chapterPagesLoaded(Doujin doujin, int index, List<String> pages) {
        int chapterIndex = index + 1;
        if(chapterIndex < doujin.chapterList.size()) {
            getPageList.loadPageList(doujin, chapterIndex, this);
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("doujin", doujin);

            DoujinDetailsFragment fragment = new DoujinDetailsFragment();
            fragment.setDoujinListListener(this);
            fragment.setSearchDetailsListener(this);
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String searchQuery = "/hentai-list/search/" + query + "/all/name-az/1/";
        startLoadingFragment();
        getDoujinList.loadDoujinList(this, searchQuery, true);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.isEmpty()) {
            startLoadingFragment();
            getDoujinList.loadDoujinList(this, url, true);
        }
        return false;
    }

    @Override
    public void onAuthorSearch(String author) {
        String searchUrl = "/hentai-list/author/" + author;
        startLoadingFragment();
        getDoujinList.loadDoujinList(this, searchUrl.replaceAll(" ", "-"), true);
    }

    @Override
    public void onArtistSearch(String artist) {
        String searchUrl = "/hentai-list/artist/" + artist;
        startLoadingFragment();
        getDoujinList.loadDoujinList(this, searchUrl.replaceAll(" ", "-"), true);
    }

    @Override
    public void onCategorySearch(String category) {
        String searchUrl = "/hentai-list/category/" + category;
        startLoadingFragment();
        getDoujinList.loadDoujinList(this, searchUrl.replaceAll(" ", "%20"), true);
    }

    private void importJson() {
        File file = new File(getBaseContext().getExternalFilesDir(null),"H2RExport.json");
        String builder = "";
        try {
            if (!file.exists()) {
                Toast.makeText(getBaseContext(), "Import File Does Not Exist", Toast.LENGTH_LONG).show();
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                builder += line;
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error Importing", Toast.LENGTH_LONG).show();
            return;
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(builder);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                doujinViewModel.insert(new Doujin(jsonObj.getString("title"), jsonObj.getString("id"), jsonObj.getString("url"), jsonObj.getInt("bookmark"), jsonObj.getLong("bookmark_date")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error Importing", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(getBaseContext(), "Importing Complete", Toast.LENGTH_LONG).show();
    }

    public List<Doujin> getData() {
        return data;
    }

    @Override
    public void doujinUpdated(Doujin doujin) {
        doujin.doujinBookmarkDate = System.currentTimeMillis();
        if(doujin.doujinStatus.equals("Completed")) {
            doujin.doujinBookmark = 2;
        } else {
            doujin.doujinBookmark = 3;
        }
        for(String tag : doujin.doujinGenres.split(", ")) {
            if(blackListTags.contains(tag)) {
                doujin.doujinBookmark = 5;
                doujinViewModel.insert(doujin);
                return;
            }
        }
        doujinViewModel.insert(doujin);
    }
}
