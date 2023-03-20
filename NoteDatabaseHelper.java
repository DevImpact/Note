package com.devimpact.inote;



import android.content.Context;

import androidx.room.Database;

import androidx.room.Room;

import androidx.room.RoomDatabase;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabaseHelper extends RoomDatabase {

    private static volatile NoteDatabaseHelper instance;

    public abstract NoteDao noteDao();

    public static synchronized NoteDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabaseHelper.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}