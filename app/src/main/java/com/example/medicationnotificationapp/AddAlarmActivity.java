package com.example.medicationnotificationapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.example.medicationnotificationapp.databinding.ActivityAddAlarmBinding;

import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity {

    EditText time, medicationNameEditText, dosageEditText;
    Button backButton, saveButton, deleteButton;
    SwitchCompat switchCompat;
    String originalMedicationName, originalDosage, originalTimeSet;
    Boolean originalAlarmState;
    int uid;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        time = (EditText) findViewById(R.id.alarm_time);
        medicationNameEditText = (EditText) findViewById(R.id.medication_name);
        dosageEditText = (EditText) findViewById(R.id.dosage);
        backButton = (Button) findViewById(R.id.back);
        deleteButton = (Button) findViewById(R.id.delete);
        saveButton = (Button) findViewById(R.id.save);
        switchCompat = (SwitchCompat) findViewById(R.id.switchcompat);

        // Set up Instance of database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").allowMainThreadQueries().build();

        populateFieldsFromIntent();

        // perform click event listener on time edit text for time selection
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddAlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time.setText(checkDigit(selectedHour) + ":" + checkDigit(selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.select_time_label));
                mTimePicker.show();

            }
        });
        // perform click event listener on back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAlarmActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // perform click event listener on save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEntry();
            }
        });


        // perform click event listener on delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry();
            }
        });
        //activate test notification
        //testAlarm(this);
    }
    public void deleteEntry() {
        if (originalMedicationName == null) {
            Intent intent = new Intent(AddAlarmActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            String medicationName = medicationNameEditText.getText().toString();
            String dosage = dosageEditText.getText().toString();
            String timeSet = time.getText().toString();

            ListItem listItem = db.listItemDAO().findAListItem(medicationName, dosage, timeSet);
            db.listItemDAO().delete(listItem);

            Intent intent = new Intent(AddAlarmActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
    public void saveEntry() {
        String medicationName = medicationNameEditText.getText().toString();
        String dosage = dosageEditText.getText().toString();
        String timeSet = time.getText().toString();
        Boolean alarmState = switchCompat.isChecked();

        // Only will save if all fields completed
        if (medicationName.isEmpty() || dosage.isEmpty() || timeSet.isEmpty()){
            Toast.makeText(this, R.string.complete_all_fields_to_save_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        if (originalMedicationName == null) {
            // save new medication to list and get new uid for entry
            ListItem newListItem = new ListItem(medicationName, dosage, timeSet, alarmState);
            long newUid = db.listItemDAO().insert(newListItem);


            if (alarmState) {
                // Setting alarm for new medication listing
                String[] hoursMinutes = timeSet.split(":");
                int hours = 0;
                int minutes = 0;
                try {
                    hours = Integer.parseInt(hoursMinutes[0]);
                    minutes = Integer.parseInt(hoursMinutes[1]);

                } catch (NumberFormatException e){
                    Toast.makeText(this, R.string.invalid_time_format, Toast.LENGTH_SHORT).show();
                }
                scheduleAlarm(this, (int) newUid, hours, minutes);
                Toast.makeText(this, getString(R.string.alert_set_for_toast_text) + timeSet, Toast.LENGTH_SHORT).show();
            }
        } else {
            // update existing contact
            uid = getIntent().getIntExtra("uid", 0);
            ListItem oldListItem = db.listItemDAO().findAListItem(originalMedicationName, originalDosage, originalTimeSet);
            oldListItem.setMedicationName(medicationName);
            oldListItem.setDosage(dosage);
            oldListItem.setTime(timeSet);
            oldListItem.setAlarmSet(alarmState);
            db.listItemDAO().updateListItem(oldListItem);
            if (alarmState) {
                // Setting alarm on updating medication listing
                String[] hoursMinutes = timeSet.split(":");
                int hours = 0;
                int minutes = 0;
                try {
                    hours = Integer.parseInt(hoursMinutes[0]);
                    minutes = Integer.parseInt(hoursMinutes[1]);

                } catch (NumberFormatException e){
                    Toast.makeText(this, R.string.invalid_time_format, Toast.LENGTH_SHORT).show();
                }
                scheduleAlarm(this, uid, hours, minutes);
                Toast.makeText(this, getString(R.string.alert_set_for_toast_text) + timeSet, Toast.LENGTH_SHORT).show();
            }
        }

        Intent intent = new Intent(AddAlarmActivity.this, MainActivity.class);
        startActivity(intent);
    }
    private void populateFieldsFromIntent() {
        originalMedicationName = getIntent().getStringExtra("medication_name");
        originalDosage = getIntent().getStringExtra("dosage");
        originalTimeSet = getIntent().getStringExtra("alarm_time");
        originalAlarmState = getIntent().getBooleanExtra("switchcompat", false);
        uid = getIntent().getIntExtra("uid", 0);

        // if clicked to edit previous entry details will populate, else will be blank form to add new
        // if clicked to edit previous entry, if this entry has a set alarm cancel that alarm
        if (originalMedicationName == null) { return; }

        medicationNameEditText.setText(originalMedicationName);
        dosageEditText.setText(originalDosage);
        time.setText(originalTimeSet);
        switchCompat.setChecked(originalAlarmState);


        if (originalAlarmState) { cancelAlarm(this, uid);
            Toast.makeText(this, R.string.alarm_cancelled_save_to_set_alarm, Toast.LENGTH_SHORT).show();}
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private void scheduleAlarm(Context context, int alarmId, int hourOfDay, int minute) {
        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Create an Intent that will be broadcast when the alarm is triggered
        Intent intent = new Intent(context, AlarmReceiver.class);
        // Add UID as an extra to the intent
        intent.putExtra("UID", uid);

        // Create a PendingIntent with the broadcast Intent
        // This PendingIntent will be used to uniquely identify the alarm
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Set the desired hour and minute of the alarm
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        // Ensure the second is set to 0 to avoid potential timing issues
        calendar.set(Calendar.SECOND, 0);

        // Log statements for debugging
        // Log.d("AlarmDebug", "Scheduled Alarm Id: " + alarmId);
        // Log.d("AlarmDebug", "Scheduled Alarm Time: " + calendar.getTime() + " or " + hourOfDay + minute);

        // Use AlarmManager to set a repeating alarm
        // - RTC_WAKEUP: The alarm is in real-time clock time and wakes up the device.
        // - calendar.getTimeInMillis(): The time at which the alarm should first go off.
        // - AlarmManager.INTERVAL_DAY: The interval at which the alarm repeats (in this case, every day).
        // - pendingIntent: The PendingIntent to be sent when the alarm is triggered.
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    // Cancel the previously scheduled alarm
    private void cancelAlarm(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel the alarm using the unique PendingIntent
        alarmManager.cancel(pendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the alarm when the activity is destroyed
        cancelAlarm(this, uid);
    }

    /*// Method to test the alarmManager and alarmReciever are working
    public void testAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        long triggerTime = SystemClock.elapsedRealtime() + 10000; // Trigger after 10 seconds

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
        Log.d("AlarmTestDebug", "Test alarm set");

    }*/
}