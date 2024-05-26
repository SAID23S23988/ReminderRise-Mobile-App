package com.example.myapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<Event> events;
    private Context context;
    private DatabaseHelper databaseHelper;

    public EventsAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(holder.getAdapterPosition());
        holder.nameTextView.setText(event.getName());
        holder.dateTextView.setText(event.getDate());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView dateTextView;
        public Button deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.eventNameTextView);
            dateTextView = itemView.findViewById(R.id.eventDateTextView);
            deleteButton = itemView.findViewById(R.id.deleteEventButton);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Event event = events.get(position);
                        new AlertDialog.Builder(context)
                                .setTitle("Delete Confirmation")
                                .setMessage("Are you sure you want to delete this event?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (databaseHelper.deleteEvent(event.getId())) {
                                            events.remove(position);
                                            notifyItemRemoved(position);
                                            Toast.makeText(context, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Failed to delete event", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                }
            });
        }
    }
}





