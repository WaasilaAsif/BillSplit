package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.ActivityEvent;
import com.example.billsplit.local.AppDataStore;

import java.util.List;

public class ActivityRepository {

    private final AppDataStore store = AppDataStore.getInstance();

    /** All events, newest first — backs the Activity Feed. */
    public List<ActivityEvent> getFeed() {
        List<ActivityEvent> events = store.getActivityEvents();
        events.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return events;
    }

    /** Same stream, annotated with read state — backs Notifications. */
    public List<ActivityEvent> getNotifications() {
        return getFeed();
    }

    public void markAllRead() {
        store.markAllNotificationsRead();
    }
}
