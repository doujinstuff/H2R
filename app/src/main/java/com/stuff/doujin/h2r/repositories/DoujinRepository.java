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

    public DoujinRepository(Application application) {
        DoujinRoomDatabase db = DoujinRoomDatabase.getDatabase(application);
        doujinDao = db.doujinDao();
        allDoujins = doujinDao.getAllDoujins();
    }

    public LiveData<List<Doujin>> getAllDoujins() {
        return allDoujins;
    }

    public Doujin findDoujin (String doujinId) {
        return doujinDao.findDoujin(doujinId);
    }

    public void insert (Doujin doujin) {
        new insertAsyncTask(doujinDao).execute(doujin);
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
}
