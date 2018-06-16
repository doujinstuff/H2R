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

    public DoujinViewModel (Application application) {
        super(application);
        repository = new DoujinRepository(application);
        allDoujins = repository.getAllDoujins();
    }

    public LiveData<List<Doujin>> getAllDoujins() { return allDoujins; }

    public Doujin findDoujin(String doujinId) { return repository.findDoujin(doujinId); }


    public void insert(Doujin doujin) { repository.insert(doujin); }
}