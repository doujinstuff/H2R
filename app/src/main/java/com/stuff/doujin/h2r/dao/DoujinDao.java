package com.stuff.doujin.h2r.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.stuff.doujin.h2r.data.Doujin;

import java.util.List;

@Dao
public interface DoujinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Doujin doujin);

    @Query("DELETE FROM doujin_table")
    void deleteAll();

    @Query("SELECT * from doujin_table ORDER BY doujin_id ASC")
    LiveData<List<Doujin>> getAllDoujins();

    @Query("SELECT * FROM doujin_table WHERE doujin_id = :doujinId LIMIT 1")
    Doujin findDoujin(String doujinId);
}
