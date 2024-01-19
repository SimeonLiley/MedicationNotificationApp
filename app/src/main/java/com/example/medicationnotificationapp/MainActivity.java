package com.example.medicationnotificationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListAdapter.OnItemSelectedListener {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ListItem> medicationList;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vibrate permission request in later build versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.VIBRATE) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.VIBRATE}, 101);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                startActivity(intent);
            }
        });

        // Set up Instance of database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").allowMainThreadQueries().build();

        fetchListItem();
    }
    private void fetchListItem() {
        List<ListItem> listItems = db.listItemDAO().getAllListItems();
        medicationList = new ArrayList<>(listItems);

        setUpRecyclerView();
    }
    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.list_recycler_view);
        recyclerView.setHasFixedSize(true);

        // set up layoutManager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ListAdapter(medicationList, this);
        recyclerView.setAdapter(adapter);
    }
    public void onItemSelected(int position) {
        // If existing entry is selected navigate to AddAlarmActivity with data fields populated matching selected entry
        ListItem item = medicationList.get(position);
        Intent intent = new Intent(this, AddAlarmActivity.class);
        intent.putExtra("medication_name", item.getMedicationName());
        intent.putExtra("dosage", item.getDosage());
        intent.putExtra("alarm_time", item.getTime());
        intent.putExtra("switchcompat", item.getAlarmSet());
        intent.putExtra("uid", item.getUid());
        startActivity(intent);
    }

}