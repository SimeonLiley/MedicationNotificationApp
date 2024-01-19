package com.example.medicationnotificationapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ListItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    // Create DAO
    public abstract ListItemDAO listItemDAO();

}
