package com.example.medicationnotificationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    // Pass in data to each list item and use the adapter to display in recyclerView

    private ArrayList<ListItem> medicationsList;
    private OnItemSelectedListener onItemSelectedListener;

    // Generator passes in Database list
    public ListAdapter(ArrayList<ListItem> medicationsList, OnItemSelectedListener onItemSelectedListener) {
        this.medicationsList = medicationsList;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // access items in the MedicationListLayout
        TextView medicationTextView, dosageTextView, timeTextView, alarmStateTextView;
        Boolean alarm_set;
        OnItemSelectedListener onItemSelectedListener;

        LinearLayout layoutRefernece;

        // Send list item from list to layout
        private ListViewHolder(LinearLayout layout, OnItemSelectedListener onItemSelectedListener) {
            super(layout);
            this.medicationTextView = layout.findViewById(R.id.medication_name_text_view);
            this.dosageTextView = layout.findViewById(R.id.dosage_text_view);
            this.timeTextView = layout.findViewById(R.id.alarm_time_text_view);
            this.alarmStateTextView = layout.findViewById(R.id.alarm_state);
            this.onItemSelectedListener = onItemSelectedListener;
            this.layoutRefernece = layout.findViewById(R.id.recycler_view_list_item_layout);

            layout.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            onItemSelectedListener.onItemSelected(getAdapterPosition());
        }
    }


    // inflating new layout ready to have data added
    @NonNull
    @Override
    public ListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.medication_list_layout, viewGroup, false
        );
        return new ListViewHolder(layout, (OnItemSelectedListener) onItemSelectedListener);
    }

    // Bind data from TextView/DB to the list view
    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ListViewHolder holder, int position) {
        final ListItem medicationList = medicationsList.get(position); // get the current list item and bind the data to the list view
        holder.medicationTextView.setText(medicationList.getMedicationName());
        holder.dosageTextView.setText(medicationList.getDosage());
        holder.timeTextView.setText(medicationList.getTime());
        holder.alarmStateTextView.setText(medicationList.alarmSetToString(medicationList.getAlarmSet()));

        // Updating the background color according to the odd/even positions in list.
        if (position % 2 == 0) {
            holder.layoutRefernece.setBackgroundColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.RedDark
                    )
            );
        } else {
            holder.layoutRefernece.setBackgroundColor(ContextCompat.getColor(
                    holder.itemView.getContext(), R.color.RedLight));
        }

    }

    @Override
    public int getItemCount() {
        return medicationsList.size();
    }
    public interface OnItemSelectedListener  {
        void onItemSelected(int position);
    }
}
