package com.stuff.doujin.h2r;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.fragments.DoujinDetailsFragment;
import com.stuff.doujin.h2r.fragments.DoujinListFragment;
import com.stuff.doujin.h2r.fragments.LoadingFragment;
import com.stuff.doujin.h2r.network.GetDoujinDetails;
import com.stuff.doujin.h2r.network.GetDoujinList;
import com.stuff.doujin.h2r.network.GetPageList;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DoujinListFragment.DoujinListListener, GetDoujinList.DoujinListLoaded, GetDoujinDetails.DoujinDetailsLoaded, GetPageList.ChapterPagesLoaded, SearchView.OnQueryTextListener {

    String url;
    GetDoujinList getDoujinList;
    GetDoujinDetails getDoujinDetails;
    GetPageList getPageList;

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

        getDoujinList = new GetDoujinList();
        getDoujinDetails = new GetDoujinDetails();
        getPageList = new GetPageList();

        if (savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            LoadingFragment fragment = new LoadingFragment();

            getSupportFragmentManager().beginTransaction().add(R.id.flContainer, fragment).commit();

            url = getResources().getString(R.string.latest_start_url);
            getDoujinList.loadDoujinList(this, url);
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
            getDoujinList.loadDoujinList(this, url);
        } else if (id == R.id.nav_popular) {
            startLoadingFragment();
            url = getResources().getString(R.string.popular_start_url);
            getDoujinList.loadDoujinList(this, url);
        } else if (id == R.id.nav_favorite) {

        } else if (id == R.id.nav_on_hold) {

        } else if (id == R.id.nav_plan_to_read) {

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
        getDoujinList.loadDoujinList(doujinListFragment, doujinListFragment.getNextPageUrl());
    }

    @Override
    public void onRefresh() {
        if(url != null) {
            startLoadingFragment();
            getDoujinList.loadDoujinList(this, url);
        }
    }

    @Override
    public void doujinListLoaded(ArrayList<Doujin> doujinList, String nextPageUrl) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("doujins", doujinList);
        bundle.putString("nextPageUrl", nextPageUrl);

        DoujinListFragment fragment = new DoujinListFragment();
        fragment.setDoujinListListener(this);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
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
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String searchQuery = "/hentai-list/search/" + query + "/all/name-az/1/";
        startLoadingFragment();
        getDoujinList.loadDoujinList(this, searchQuery);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
