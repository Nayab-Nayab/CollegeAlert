/*package com.example.collegealert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends
        RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    List<Event> eventList;
    OnEventClickListener listener;
    String role;


    public interface OnEventClickListener {
        void onEventClick(Event event);
        void onEventLongClick(Event event);
    }

    public EventAdapter(List<Event> eventList,
                        OnEventClickListener listener, String role) {
        this.eventList = eventList;
        this.listener = listener;
        this.role = role;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.tvName.setText(event.getName());
        holder.tvDate.setText(event.getDate());
        holder.tvCategory.setText(event.getCategory());

        // Admin ko edit tag dikhao
        if (role.equals("admin")) {
            holder.tvEdit.setVisibility(View.VISIBLE);
        } else {
            holder.tvEdit.setVisibility(View.GONE);
        }

        // Single click
        holder.itemView.setOnClickListener(v ->
                listener.onEventClick(event));

        // Long press — admin only delete
        holder.itemView.setOnLongClickListener(v -> {
            listener.onEventLongClick(event);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // Filter method
    public void filterList(List<Event> filtered) {
        eventList = filtered;
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends
            RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvEdit,tvCategory;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEventName);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvEdit = itemView.findViewById(R.id.tvArrow);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}*/

package com.example.collegealert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends
        RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    List<Event> eventList;
    OnEventClickListener listener;
    String role;
    boolean isSelectionMode = false;
    List<Event> selectedEvents = new ArrayList<>();

    public interface OnEventClickListener {
        void onEventClick(Event event);
        void onEventLongClick(Event event);
        void onSelectionChanged(int count);
    }

    public EventAdapter(List<Event> eventList,
                        OnEventClickListener listener,
                        String role) {
        this.eventList = eventList;
        this.listener = listener;
        this.role = role;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.tvName.setText(event.getName());
        holder.tvDate.setText(event.getDate());
        holder.tvCategory.setText(event.getCategory());

        if (role.equals("admin")) {
            holder.tvEdit.setVisibility(View.VISIBLE);
        } else {
            holder.tvEdit.setVisibility(View.GONE);
        }

        // Selected item ka background blue karo
        if (isSelectionMode && selectedEvents.contains(event)) {
            holder.itemView.setBackgroundColor(0xFFBBDEFB);
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF);
        }

        // Click
        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode && role.equals("admin")) {
                if (selectedEvents.contains(event)) {
                    selectedEvents.remove(event);
                } else {
                    selectedEvents.add(event);
                }
                if (selectedEvents.isEmpty()) {
                    isSelectionMode = false;
                }
                notifyDataSetChanged();
                listener.onSelectionChanged(
                        selectedEvents.size());
            } else {
                listener.onEventClick(event);
            }
        });

        // Long press — admin only selection mode
        holder.itemView.setOnLongClickListener(v -> {
            if (role.equals("admin")) {
                if (!isSelectionMode) {
                    isSelectionMode = true;
                    selectedEvents.clear();
                    selectedEvents.add(event);
                    notifyDataSetChanged();
                    listener.onSelectionChanged(
                            selectedEvents.size());
                }
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void filterList(List<Event> filtered) {
        eventList = filtered;
        notifyDataSetChanged();
    }

    public List<Event> getSelectedEvents() {
        return new ArrayList<>(selectedEvents);
    }

    public void clearSelection() {
        isSelectionMode = false;
        selectedEvents.clear();
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends
            RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvEdit, tvCategory;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEventName);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvEdit = itemView.findViewById(R.id.tvArrow);
            tvCategory = itemView.findViewById(
                    R.id.tvCategory);
        }
    }
}