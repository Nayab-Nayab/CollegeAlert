package com.example.collegealert;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    // TextViews — view mode
    TextView tvName, tvDate, tvVenue,
            tvOrganizer, tvCategory;

    // EditTexts — edit mode
    EditText etName, etDate, etVenue,
            etOrganizer, etCategory;

    // Buttons
    Button btnEdit, btnSave, btnCancel,
            btnAction, btnBack;

    FirebaseFirestore db;
    String role, eventId;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);


        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    android.graphics.Color.parseColor("#1A73E8"));
        }

        db = FirebaseFirestore.getInstance();

        // Role aur event data lo
        role = getIntent().getStringExtra("role");
        eventId = getIntent().getStringExtra("eventId");
        String name = getIntent()
                .getStringExtra("eventName");
        String date = getIntent()
                .getStringExtra("eventDate");
        String venue = getIntent()
                .getStringExtra("eventVenue");
        String organizer = getIntent()
                .getStringExtra("eventOrganizer");
        String category = getIntent()
                .getStringExtra("eventCategory");

        // Views initialize karo
        tvName     = findViewById(R.id.tvName);
        tvDate     = findViewById(R.id.tvDate);
        tvVenue    = findViewById(R.id.tvVenue);
        tvOrganizer= findViewById(R.id.tvOrganizer);
        tvCategory = findViewById(R.id.tvCategory);

        etName     = findViewById(R.id.etName);
        etDate     = findViewById(R.id.etDate);
        etVenue    = findViewById(R.id.etVenue);
        etOrganizer= findViewById(R.id.etOrganizer);
        etCategory = findViewById(R.id.etCategory);

        btnEdit   = findViewById(R.id.btnEdit);
        btnSave   = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnAction = findViewById(R.id.btnAction);
        btnBack   = findViewById(R.id.btnBack);

        // Data show karo
        tvName.setText(name);
        tvDate.setText(date);
        tvVenue.setText(venue);
        tvOrganizer.setText(organizer);
        tvCategory.setText(category);

        // Role ke hisab se buttons set karo
        if (role.equals("admin")) {
            // Admin ko Edit button dikhao
            btnEdit.setVisibility(View.VISIBLE);
            // Admin ko Notify button
            btnAction.setText("Send Notification");
            // Edit/Save/Cancel hidden initially
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            // EditTexts hidden initially
            hideEditTexts();

        } else {
            // User ko Edit nahi
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            // User ko Calendar button
            btnAction.setText("Add to Calendar");
            hideEditTexts();
        }

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Edit button — Admin only
        btnEdit.setOnClickListener(v -> {
            isEditMode = true;
            showEditMode(name, date, venue,
                    organizer, category);
        });

        // Save button
        btnSave.setOnClickListener(v -> {
            saveChanges();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            isEditMode = false;
            showViewMode();
        });

        // Action button
        btnAction.setOnClickListener(v -> {
            if (role.equals("admin")) {
                sendNotification(name, date, venue);
            } else {
                addToCalendar(name, date, venue);
            }
        });
    }

    // Edit mode show karo
    private void showEditMode(String name, String date,
                              String venue, String organizer, String category) {

        // TextViews hide karo
        tvName.setVisibility(View.GONE);
        tvDate.setVisibility(View.GONE);
        tvVenue.setVisibility(View.GONE);
        tvOrganizer.setVisibility(View.GONE);
        tvCategory.setVisibility(View.GONE);

        // EditTexts show karo
        etName.setVisibility(View.VISIBLE);
        etDate.setVisibility(View.VISIBLE);
        etVenue.setVisibility(View.VISIBLE);
        etOrganizer.setVisibility(View.VISIBLE);
        etCategory.setVisibility(View.VISIBLE);

        // Data fill karo
        etName.setText(name);
        etDate.setText(date);
        etVenue.setText(venue);
        etOrganizer.setText(organizer);
        etCategory.setText(category);

        // Buttons
        btnEdit.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        btnAction.setVisibility(View.GONE);
    }

    // View mode wapas
    private void showViewMode() {
        // TextViews show karo
        tvName.setVisibility(View.VISIBLE);
        tvDate.setVisibility(View.VISIBLE);
        tvVenue.setVisibility(View.VISIBLE);
        tvOrganizer.setVisibility(View.VISIBLE);
        tvCategory.setVisibility(View.VISIBLE);

        // EditTexts hide karo
        hideEditTexts();

        // Buttons
        btnEdit.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        btnAction.setVisibility(View.VISIBLE);
    }

    // EditTexts hide karo
    private void hideEditTexts() {
        etName.setVisibility(View.GONE);
        etDate.setVisibility(View.GONE);
        etVenue.setVisibility(View.GONE);
        etOrganizer.setVisibility(View.GONE);
        etCategory.setVisibility(View.GONE);
    }

    // Firebase mein save karo
    private void saveChanges() {
        String newName = etName.getText()
                .toString().trim();
        String newDate = etDate.getText()
                .toString().trim();
        String newVenue = etVenue.getText()
                .toString().trim();
        String newOrganizer = etOrganizer.getText()
                .toString().trim();
        String newCategory = etCategory.getText()
                .toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this,
                    "Enter event name !",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("date", newDate);
        updates.put("venue", newVenue);
        updates.put("organizedBy", newOrganizer);
        updates.put("category", newCategory);

        db.collection("events")
                .document(eventId)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "Event updated successfully! ✅",
                            Toast.LENGTH_SHORT).show();

                    // TextViews update karo
                    tvName.setText(newName);
                    tvDate.setText(newDate);
                    tvVenue.setText(newVenue);
                    tvOrganizer.setText(newOrganizer);
                    tvCategory.setText(newCategory);

                    // View mode wapas
                    showViewMode();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // Notification send karo
    private void sendNotification(String name,
                                  String date, String venue) {
        // Pehle check karo duplicate toh nahi
        db.collection("notifications")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Toast.makeText(this,
                                "Notification of this Event has already sended✅",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> notif = new HashMap<>();
                        notif.put("title", name);
                        notif.put("body", venue + " · " + date);
                        notif.put("eventId", eventId);
                        notif.put("timestamp",
                                System.currentTimeMillis());

                        db.collection("notifications")
                                .add(notif)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this,
                                            "Notification  sended 🔔",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this,
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    // Calendar mein add karo
    private void addToCalendar(String name,
                               String date, String venue) {
        Intent intent = new Intent(
                Intent.ACTION_INSERT);
        intent.setData(
                CalendarContract.Events.CONTENT_URI);
        intent.putExtra(
                CalendarContract.Events.TITLE, name);
        intent.putExtra(
                CalendarContract.Events.EVENT_LOCATION,
                venue);
        intent.putExtra(
                CalendarContract.Events.DESCRIPTION,
                "Date: " + date);
        startActivity(intent);
    }
}