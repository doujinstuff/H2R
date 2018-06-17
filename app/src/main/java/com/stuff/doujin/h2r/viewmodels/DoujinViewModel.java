package com.stuff.doujin.h2r.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.stuff.doujin.h2r.data.Doujin;
import com.stuff.doujin.h2r.repositories.DoujinRepository;

import java.util.List;

public class DoujinViewModel extends AndroidViewModel {

    private DoujinRepository repository;

    private LiveData<List<Doujin>> allDoujins;
    private LiveData<List<Doujin>> favoriteDoujin;
    private LiveData<List<Doujin>> onHoldDoujins;
    private LiveData<List<Doujin>> planToReadDoujins;

    public DoujinViewModel (Application application) {
        super(application);
        repository = new DoujinRepository(application);
        allDoujins = repository.getAllDoujins();
        favoriteDoujin = repository.getFavoriteDoujins();
        onHoldDoujins = repository.getOnHoldDoujins();
        planToReadDoujins = repository.getPlanToReadDoujins();
    }

    public LiveData<List<Doujin>> getAllDoujins() { return allDoujins; }

    public LiveData<List<Doujin>> getFavoriteDoujins() { return favoriteDoujin; }

    public LiveData<List<Doujin>> getOnHoldDoujins() { return onHoldDoujins; }

    public LiveData<List<Doujin>> getPlanToReadDoujins() { return planToReadDoujins; }


    public Doujin findDoujin(String doujinId) { return repository.findDoujin(doujinId); }


    public void insert(Doujin doujin) { repository.insert(doujin); }

    public void delete(Doujin doujin) { repository.delete(doujin); }
}