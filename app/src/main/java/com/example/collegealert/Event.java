package com.example.collegealert;

public class Event {
    private String id;
    private String name;
    private String date;
    private String venue;
    private String organizedBy;
    private String category;

    public Event() {}

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getVenue() { return venue; }
    public String getOrganizedBy() { return organizedBy; }
    public String getCategory() { return category; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDate(String date) { this.date = date; }
    public void setVenue(String venue) {
        this.venue = venue; }
    public void setOrganizedBy(String organizedBy) {
        this.organizedBy = organizedBy; }
    public void setCategory(String category) {
        this.category = category; }
}
