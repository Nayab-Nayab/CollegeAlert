package com.example.collegealert;

public class NotificationItem {
    private String title;
    private String body;
    private String eventId;

    public NotificationItem() {}

    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getEventId() { return eventId; }

    public void setTitle(String title) {
        this.title = title; }
    public void setBody(String body) {
        this.body = body; }
    public void setEventId(String eventId) {
        this.eventId = eventId; }
}