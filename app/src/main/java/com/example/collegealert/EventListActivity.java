package com.example.collegealert;

import android.app.AlertDialog;
import android.content.Intent;

import com.example.collegealert.Event;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListActivity extends AppCompatActivity
        implements EventAdapter.OnEventClickListener {

    RecyclerView recyclerView;
    Button btnAdd,btnBell;
    EventAdapter adapter;
    List<Event> eventList;
    FirebaseFirestore db;
    String role;

    LinearLayout topBar, searchBar;
    EditText etSearch;
    Button btnSearchBack, btnSearch;
    android.widget.LinearLayout deleteBar;
    android.widget.TextView tvSelectedCount;
    Button btnDeleteSelected, btnCancelSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);



        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    android.graphics.Color.parseColor("#1A73E8"));
        }

        role = getIntent().getStringExtra("role");
        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);


        if (role.equals("admin")) {
            btnAdd.setVisibility(View.VISIBLE);
        } else {
            btnAdd.setVisibility(View.GONE);
        }

        // RecyclerView setup
        adapter = new EventAdapter(eventList, this, role);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // load events from firebase
        loadEvents();

        // Delete bar views
        deleteBar = findViewById(R.id.deleteBar);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected);
        btnCancelSelection = findViewById(R.id.btnCancelSelection);

// Cancel selection
        btnCancelSelection.setOnClickListener(v -> {
            adapter.clearSelection();
            deleteBar.setVisibility(View.GONE);
            topBar.setVisibility(View.VISIBLE);  // yeh add karo
        });



        btnDeleteSelected.setOnClickListener(v -> {
            List<Event> toDelete = adapter.getSelectedEvents();
            if (toDelete.isEmpty()) return;

            new android.app.AlertDialog.Builder(this)
                    .setTitle("Delete Events?")
                    .setMessage(toDelete.size() +
                            " Events will be deleted!")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        for (Event e : toDelete) {
                            //  delete event
                            db.collection("events")
                                    .document(e.getId())
                                    .delete();

                            // delete notification of that event aas w
                            String eventId = e.getId();
                            db.collection("notifications")
                                    .whereEqualTo("eventId", eventId)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        for (com.google.firebase.firestore
                                                .QueryDocumentSnapshot doc
                                                : querySnapshot) {
                                            doc.getReference().delete();
                                        }
                                    });
                        }
                        adapter.clearSelection();
                        deleteBar.setVisibility(View.GONE);
                        topBar.setVisibility(View.VISIBLE);
                        Toast.makeText(this,
                                toDelete.size() +
                                        " events deleted successfully! ✅",
                                Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Search logic
        topBar = findViewById(R.id.topBar);
        searchBar = findViewById(R.id.searchBar);
        etSearch = findViewById(R.id.etSearch);
        btnSearchBack = findViewById(R.id.btnSearchBack);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            topBar.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);
            etSearch.requestFocus();
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(etSearch,
                    android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        });

        btnSearchBack.setOnClickListener(v -> {
            topBar.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.GONE);
            etSearch.setText("");
            filterEvents("All");
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        });

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {
                searchEvents(s.toString());
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Add button click
        btnAdd.setOnClickListener(v -> showAddEventDialog());


        btnBell=findViewById(R.id.btnBell);

        btnBell.setOnClickListener(v -> {
            Intent intent = new Intent(
                    EventListActivity.this,
                    Notifications.class);
            startActivity(intent);
        });

        // Chips
        Button chipAll = findViewById(R.id.chipAll);
        Button chipSports = findViewById(R.id.chipSports);
        Button chipAcademic = findViewById(R.id.chipAcademic);
        Button chipCultural = findViewById(R.id.chipCultural);
        Button chipOther = findViewById(R.id.chipOther);

// All selected by default
        chipAll.setBackgroundTintList(
                android.content.res.ColorStateList
                        .valueOf(0xFF1A73E8));
        chipAll.setTextColor(0xFFFFFFFF);

// Chip click listeners
        chipAll.setOnClickListener(v -> {
            filterEvents("All");
            setActiveChip(chipAll, chipSports,
                    chipAcademic, chipCultural, chipOther);
        });
        chipSports.setOnClickListener(v -> {
            filterEvents("Sports");
            setActiveChip(chipSports, chipAll,
                    chipAcademic, chipCultural, chipOther);
        });
        chipAcademic.setOnClickListener(v -> {
            filterEvents("Academic");
            setActiveChip(chipAcademic, chipAll,
                    chipSports, chipCultural, chipOther);
        });
        chipCultural.setOnClickListener(v -> {
            filterEvents("Cultural");
            setActiveChip(chipCultural, chipAll,
                    chipSports, chipAcademic, chipOther);
        });
        chipOther.setOnClickListener(v -> {
            filterEvents("Other");
            setActiveChip(chipOther, chipAll,
                    chipSports, chipAcademic, chipCultural);
        });

    }

    // Firebase se events load karo
    private void loadEvents() {
        db.collection("events")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    eventList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Event event = new Event();
                        event.setId(doc.getId());
                        event.setName(doc.getString("name"));
                        event.setDate(doc.getString("date"));
                        event.setVenue(doc.getString("venue"));
                        event.setOrganizedBy(
                                doc.getString("organizedBy"));
                        event.setCategory(
                                doc.getString("category"));
                        eventList.add(event);
                    }
                    adapter.notifyDataSetChanged();
                });
    }



    // Add Event Dialog
    private void showAddEventDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("Add New Event");

        View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_add_event, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(
                R.id.etEventName);
        EditText etDate = dialogView.findViewById(
                R.id.etEventDate);
        EditText etVenue = dialogView.findViewById(
                R.id.etEventVenue);
        EditText etOrganizer = dialogView.findViewById(
                R.id.etEventOrganizer);
        Spinner spinnerCategory = dialogView.findViewById(
                R.id.spinnerCategory);

        // Spinner data
        String[] categories = {"Sports", "Academic",
                "Cultural", "Other"};
        android.widget.ArrayAdapter<String> spinnerAdapter =
                new android.widget.ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        categories);
        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String venue = etVenue.getText().toString().trim();
            String organizer = etOrganizer.getText()
                    .toString().trim();
            String category = spinnerCategory.getSelectedItem()
                    .toString();

            if (name.isEmpty()) {
                Toast.makeText(this,
                        "Add event name!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase mein save karo
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("name", name);
            eventData.put("date", date);
            eventData.put("venue", venue);
            eventData.put("organizedBy", organizer);
            eventData.put("category", category);

            db.collection("events")
                    .add(eventData)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this,
                                "Event added successfully!",
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Single click — detail screen
    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(this,
                EventDetailActivity.class);
        intent.putExtra("eventId", event.getId());
        intent.putExtra("eventName", event.getName());
        intent.putExtra("eventDate", event.getDate());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventOrganizer",
                event.getOrganizedBy());
        intent.putExtra("eventCategory",
                event.getCategory());
        intent.putExtra("role", role);
        startActivity(intent);
    }

    // Long press delete — Admin only
   /* @Override
    public void onEventLongClick(Event event) {
        if (role.equals("admin")) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Event?")
                    .setMessage(event.getName() +
                            " delete ho jayega!")
                    .setPositiveButton("Delete",
                            (dialog, which) -> {
                                db.collection("events")
                                        .document(event.getId())
                                        .delete()
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(this,
                                                    "Event delete ho gaya!",
                                                    Toast.LENGTH_SHORT).show();
                                        });
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }*/

    @Override
    public void onEventLongClick(Event event) {

    }

    @Override
    public void onSelectionChanged(int count) {
        if (count > 0) {
            topBar.setVisibility(View.GONE);      // top bar hide
            deleteBar.setVisibility(View.VISIBLE);
            tvSelectedCount.setText(count + " selected");
        } else {
            topBar.setVisibility(View.VISIBLE);   // top bar wapas
            deleteBar.setVisibility(View.GONE);
            adapter.clearSelection();
        }
    }
    // Filter method
    private void filterEvents(String category) {
        if (category.equals("All")) {
            adapter.filterList(eventList);
        } else {
            List<Event> filtered = new ArrayList<>();
            for (Event e : eventList) {
                if (e.getCategory() != null &&
                        e.getCategory().equals(category)) {
                    filtered.add(e);
                }
            }
            adapter.filterList(filtered);
        }
    }

    private void searchEvents(String query) {
        if (query.isEmpty()) {
            adapter.filterList(eventList);
            return;
        }
        List<Event> filtered = new ArrayList<>();
        for (Event e : eventList) {
            if (e.getName() != null &&
                    e.getName().toLowerCase()
                            .contains(query.toLowerCase())) {
                filtered.add(e);
            }
        }
        adapter.filterList(filtered);
    }

    // Active chip highlight
    private void setActiveChip(Button active,
                               Button... others) {
        active.setBackgroundTintList(
                android.content.res.ColorStateList
                        .valueOf(0xFF1A73E8));
        active.setTextColor(0xFFFFFFFF);
        for (Button btn : others) {
            btn.setBackgroundTintList(
                    android.content.res.ColorStateList
                            .valueOf(0xFFE0E0E0));
            btn.setTextColor(0xFF212121);
        }
    }

    @Override
    public void onBackPressed() {
        if (searchBar.getVisibility() == View.VISIBLE) {
            topBar.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.GONE);
            etSearch.setText("");
            filterEvents("All");
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    etSearch.getWindowToken(), 0);
        } else {
            super.onBackPressed();
        }
    }
}