package com.example.collegealert;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class Notifications extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btnBack;
    FirebaseFirestore db;
    List<NotificationItem> notifList;
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    android.graphics.Color.parseColor("#1A73E8"));
        }

        db = FirebaseFirestore.getInstance();
        notifList = new ArrayList<>();

        recyclerView = findViewById(
                R.id.recyclerNotifications);
        btnBack = findViewById(R.id.btnBack);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Adapter setup
        adapter = new NotificationAdapter(notifList);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        loadNotifications();
    }

    private void loadNotifications() {
        db.collection("notifications")
                .orderBy("timestamp",
                        com.google.firebase.firestore
                                .Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    notifList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        NotificationItem item =
                                new NotificationItem();
                        item.setTitle(doc.getString("title"));
                        item.setBody(doc.getString("body"));
                        item.setEventId(doc.getString("eventId")); // yeh add kiya
                        notifList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}