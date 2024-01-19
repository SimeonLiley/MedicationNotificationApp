package com.example.medicationnotificationapp;

import static android.provider.Settings.System.getString;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    // Initialize db in onReceive
    AppDatabase db;
    // register the reciever in the manifest <receiver android:name=".AlarmReceiver" />
    @Override
    public void onReceive(Context context, Intent intent) {
        // Initialise database
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "database-name").allowMainThreadQueries().build();

        // Log statements for debugging
        // Log.d("AlarmReceiver", "Received alarm broadcast");
        // Retrieve UID from the intent
        int uid = intent.getIntExtra("UID", 0);
        // Handle the alarm event, and create/display the notification
        createNotification(context, uid);
    }

    private void createNotification(Context context, int uid) {
        // Code to create and display the notification
        ListItem listItem = db.listItemDAO().findAListItemByUid(uid);
        CharSequence textTitle = context.getString(R.string.default_notification_title);
        CharSequence textContent = context.getString(R.string.default_notification_description);

        if (listItem != null) {
            // Log statement for debugging
            // Log.d("AlarmReceiver", "Medication found in database. UID: " + uid);


            textTitle = listItem.getMedicationName();  // Use medication name as title
            textContent = context.getString(R.string.dosage_label) + ": " + listItem.getDosage() + " - Time: " + listItem.getTime();
        }

        String channelID = context.getString(R.string.mainid);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_medication_notification)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Intent to trigger when notification selected
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID,
                    "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel Description");
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(uid, builder.build());
        }
    }


