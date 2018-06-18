package com.stuff.doujin.h2r.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.stuff.doujin.h2r.dao.DoujinDao;
import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.database.DoujinRoomDatabase;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class DoujinRepository {

    private DoujinDao doujinDao;
    private LiveData<List<Doujin>> allDoujins;
    private LiveData<List<Doujin>> favoriteDoujins;
    private LiveData<List<Doujin>> onHoldDoujins;
    private LiveData<List<Doujin>> planToReadDoujins;
    private LiveData<List<Doujin>> completedDoujins;
    private LiveData<List<Doujin>> blacklistDoujins;

    public DoujinRepository(Application application) {
        DoujinRoomDatabase db = DoujinRoomDatabase.getDatabase(application);
        doujinDao = db.doujinDao();
        allDoujins = doujinDao.getAllDoujins();
        favoriteDoujins = doujinDao.getDoujinByBookmark(Doujin.Bookmark.FAVORITE);
        onHoldDoujins = doujinDao.getDoujinByBookmark(Doujin.Bookmark.ON_HOLD);
        planToReadDoujins = doujinDao.getDoujinByBookmark(Doujin.Bookmark.PLAN_TO_READ);
        completedDoujins = doujinDao.getDoujinByBookmark(Doujin.Bookmark.COMPLETED);
        blacklistDoujins = doujinDao.getDoujinByBookmark(Doujin.Bookmark.BLACKLIST);
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

    public LiveData<List<Doujin>> getCompletedDoujins() {
        return completedDoujins;
    }

    public LiveData<List<Doujin>> getBlacklistDoujins() {
        return blacklistDoujins;
    }

    public Doujin findDoujin (String doujinId) {
        return doujinDao.findDoujin(doujinId);
    }

    public void getDoujinsForExport (Context context) {
        new exportAsyncTask(doujinDao, context).execute();
    }

    public void insert (Doujin doujin) {
        new insertAsyncTask(doujinDao).execute(doujin);
    }

    public void delete (Doujin doujin) { new deleteAsyncTask(doujinDao).execute(doujin); }

    public void deleteAll() { new deleteAllAsyncTask(doujinDao).execute(); }

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

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private DoujinDao asyncTaskDao;

        deleteAllAsyncTask(DoujinDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            asyncTaskDao.deleteAll();
            return null;
        }
    }


    private static class exportAsyncTask extends AsyncTask<Void, Void, List<Doujin>> {

        private DoujinDao asyncTaskDao;
        private Context context;

        exportAsyncTask(DoujinDao dao, Context context) {
            asyncTaskDao = dao;
            this.context = context;
        }

        @Override
        protected List<Doujin> doInBackground(Void... voids) {
            return asyncTaskDao.getAllDoujinsForExport();
        }

        @Override
        protected void onPostExecute(List<Doujin> exportDoujinList) {
            File file = new File(context.getExternalFilesDir(null),"H2RExport.json");
            Writer output = null;
            try {
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    file.delete();
                    file.createNewFile();
                }
                JSONArray jsonArray = new JSONArray();
                for(Doujin doujin : exportDoujinList) {
                    jsonArray.put(doujin.getJsonObject());
                }
                output = new BufferedWriter(new FileWriter(file));
                output.write(jsonArray.toString());
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error Exporting", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(context, "Exporting Complete", Toast.LENGTH_LONG).show();
        }
    }


}
