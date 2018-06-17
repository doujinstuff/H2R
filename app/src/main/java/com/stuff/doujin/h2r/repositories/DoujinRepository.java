package com.stuff.doujin.h2r.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.stuff.doujin.h2r.dao.DoujinDao;
import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.database.DoujinRoomDatabase;

import java.util.List;

public class DoujinRepository {

    private DoujinDao doujinDao;
    private LiveData<List<Doujin>> allDoujins;
    private LiveData<List<Doujin>> favoriteDoujins;
    private LiveData<List<Doujin>> onHoldDoujins;
    private LiveData<List<Doujin>> planToReadDoujins;

    public DoujinRepository(Application application) {
        DoujinRoomDatabase db = DoujinRoomDatabase.getDatabase(application);
        doujinDao = db.doujinDao();
        allDoujins = doujinDao.getAllDoujins();
        favoriteDoujins = doujinDao.getDoujinByBookmark(Doujin.Bookmark.FAVORITE);
        onHoldDoujins = doujinDao.getDoujinByBookmark(Doujin.Bookmark.ON_HOLD);
        planToReadDoujins = doujinDao.getDoujinByBookmark(Doujin.Bookmark.PLAN_TO_READ);
    }

    public LiveData<List<Doujin>> getAllDoujins() {
        return allDoujins;
    }

    public LiveData<List<Doujin>> getFavoriteDoujins() {
        return favoriteDoujins;
    }

    public LiveData<List<Doujin>> getOnHoldDoujins() {
        return onHoldDoujins;
    }

    public LiveData<List<Doujin>> getPlanToReadDoujins() {
        return planToReadDoujins;
    }

    public Doujin findDoujin (String doujinId) {
        return doujinDao.findDoujin(doujinId);
    }

    public void insert (Doujin doujin) {
        new insertAsyncTask(doujinDao).execute(doujin);
    }

    public void delete (Doujin doujin) { new deleteAsyncTask(doujinDao).execute(doujin);
    }

    private static class insertAsyncTask extends AsyncTask<Doujin, Void, Void> {

        private DoujinDao asyncTaskDao;

        insertAsyncTask(DoujinDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Doujin... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Doujin, Void, Void> {

        private DoujinDao asyncTaskDao;

        deleteAsyncTask(DoujinDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Doujin... params) {
            asyncTaskDao.deleteDoujin(params[0]);
            return null;
        }
    }

}
