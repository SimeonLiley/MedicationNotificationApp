package com.example.medicationnotificationapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ListItemDAO {

    @Query("SELECT * FROM listItem")
    List<ListItem> getAllListItems();

    @Query("SELECT * FROM listItem WHERE medication_name LIKE :medication AND dosage LIKE :dosage AND time_set LIKE :time LIMIT 1")
    ListItem findAListItem(String medication, String dosage, String time);

    @Query("SELECT * FROM listItem WHERE uid LIKE :uid LIMIT 1")
    ListItem findAListItemByUid(int uid);
    @Insert()
    long insert(ListItem listItem);

    @Delete()
    void delete(ListItem... listItem);

    @Update()
    void updateListItem(ListItem listItem);
}
