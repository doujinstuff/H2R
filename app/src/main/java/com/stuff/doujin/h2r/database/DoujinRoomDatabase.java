package com.stuff.doujin.h2r.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.stuff.doujin.h2r.dao.DoujinDao;
import com.stuff.doujin.h2r.data.Doujin;

@Database(entities = {Doujin.class}, version = 1)
public abstract class DoujinRoomDatabase extends RoomDatabase {

    public abstract DoujinDao doujinDao();

    private static DoujinRoomDatabase INSTANCE;

    public static DoujinRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DoujinRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DoujinRoomDatabase.class, "doujin_database")
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}