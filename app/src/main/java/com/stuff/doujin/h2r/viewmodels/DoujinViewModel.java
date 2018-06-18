package com.stuff.doujin.h2r.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.repositories.DoujinRepository;

import java.util.List;

public class DoujinViewModel extends AndroidViewModel {

    private DoujinRepository repository;

    private LiveData<List<Doujin>> allDoujins;
    private LiveData<List<Doujin>> favoriteDoujin;
    private LiveData<List<Doujin>> onHoldDoujins;
    private LiveData<List<Doujin>> planToReadDoujins;
    private LiveData<List<Doujin>> completedDoujin;
    private LiveData<List<Doujin>> blacklistDoujin;

    public DoujinViewModel (Application application) {
        super(application);
        repository = new DoujinRepository(application);
        allDoujins = repository.getAllDoujins();
        favoriteDoujin = repository.getFavoriteDoujins();
        onHoldDoujins = repository.getOnHoldDoujins();
        planToReadDoujins = repository.getPlanToReadDoujins();
        completedDoujin = repository.getCompletedDoujins();
        blacklistDoujin = repository.getBlacklistDoujins();
    }

    public LiveData<List<Doujin>> getAllDoujins() { return allDoujins; }

    public LiveData<List<Doujin>> getFavoriteDoujins() { return favoriteDoujin; }

    public LiveData<List<Doujin>> getOnHoldDoujins() { return onHoldDoujins; }

    public LiveData<List<Doujin>> getPlanToReadDoujins() { return planToReadDoujins; }

    public LiveData<List<Doujin>> getCompletedDoujins() { return completedDoujin; }

    public LiveData<List<Doujin>> getBlacklistDoujins() { return blacklistDoujin; }

    public Doujin findDoujin(String doujinId) { return repository.findDoujin(doujinId); }

    public void getDoujinsForExport(Context context) {  repository.getDoujinsForExport(context); }

    public void insert(Doujin doujin) { repository.insert(doujin); }

    public void delete(Doujin doujin) { repository.delete(doujin); }

    public void deleteAll() { repository.deleteAll(); }
}