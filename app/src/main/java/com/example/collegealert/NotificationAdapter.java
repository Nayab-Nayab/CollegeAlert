package com.example.collegealert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends
        RecyclerView.Adapter<NotificationAdapter
                .NotifViewHolder> {

    List<NotificationItem> notifList;

    public NotificationAdapter(
            List<NotificationItem> notifList) {
        this.notifList = notifList;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_notification,
                        parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull NotifViewHolder holder, int position) {
        NotificationItem item = notifList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvBody.setText(item.getBody());
    }

    @Override
    public int getItemCount() {
        return notifList.size();
    }

    public static class NotifViewHolder extends
            RecyclerView.ViewHolder {
        TextView tvTitle, tvBody;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(
                    R.id.tvNotifTitle);
            tvBody = itemView.findViewById(
                    R.id.tvNotifBody);
        }
    }
}
