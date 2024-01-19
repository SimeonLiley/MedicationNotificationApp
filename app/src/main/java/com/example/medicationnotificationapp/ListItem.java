package com.example.medicationnotificationapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ListItem {
    // Constructor for list items of medications, dosages, times, and if alarm is set
    @PrimaryKey(autoGenerate = true)
    public int uid; // database id for each entry
    @ColumnInfo(name="medication_name")
    private String medicationName;
    @ColumnInfo(name="dosage")
    private String dosage;
    @ColumnInfo(name="time_set")
    private String time;
    @ColumnInfo(name="alarm_state")
    private Boolean alarmSet;

    // Constructor setting up each list entry
    public ListItem(String medicationName, String dosage, String time, Boolean alarmSet) {
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.time = time;
        this.alarmSet = alarmSet;
    }

    // Getting and setting data
    public int getUid() {
        return uid;
    }
    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) { this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getAlarmSet() {
        return alarmSet;
    }

    public void setAlarmSet(Boolean alarmSet) {
        this.alarmSet = alarmSet;
    }

    // Convert boolean state of alarm set state to more user friendly string
    public String alarmSetToString(Boolean state){
        String onStatement = "Alert On";
        String OffStatement = "Alert Off";
        if (state) { return onStatement; }
        else { return OffStatement; }
    }
    public Boolean alarmSetToBoolean(String state){
        return state == "Alert On";
    }
}
