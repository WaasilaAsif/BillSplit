package com.example.billsplit.ui.activity;

import android.content.Context;

import com.example.billsplit.R;
import com.example.billsplit.data.model.ActivityEvent;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.User;
import com.example.billsplit.local.AppDataStore;
import com.example.billsplit.util.Money;

import java.time.Duration;
import java.time.Instant;

/** Turns an ActivityEvent into the display text/time both Activity Feed and Notifications need. */
public final class ActivityTextFormatter {

    private ActivityTextFormatter() {
    }

    public static String describe(Context context, ActivityEvent event) {
        AppDataStore store = AppDataStore.getInstance();
        String userId = AppDataStore.CURRENT_USER_ID;
        String actorName = nameFor(store, event.getActorUserId());
        String groupName = event.getGroupId() != null ? groupNameFor(store, event.getGroupId()) : null;

        switch (event.getType()) {
            case EXPENSE_ADDED:
                String amount = event.getAmount() != null ? Money.format(event.getAmount()) : "";
                return groupName != null
                        ? context.getString(R.string.activity_expense_added_in_group, actorName,
                                event.getDescription(), amount, groupName)
                        : context.getString(R.string.activity_expense_added, actorName,
                                event.getDescription(), amount);
            case PAYMENT_MADE:
                String targetName = nameFor(store, event.getTargetUserId());
                String paidAmount = event.getAmount() != null ? Money.format(event.getAmount()) : "";
                return context.getString(R.string.activity_payment_made, actorName, targetName, paidAmount);
            case SETTLED_UP:
                if (userId.equals(event.getTargetUserId())) {
                    return context.getString(R.string.activity_settled_up_with_you, actorName);
                }
                return context.getString(R.string.activity_settled_up_in_group, actorName,
                        groupName != null ? groupName : "");
            case MEMBER_INVITED:
                if (userId.equals(event.getTargetUserId())) {
                    return context.getString(R.string.activity_invited_you, actorName,
                            groupName != null ? groupName : "");
                }
                return context.getString(R.string.activity_invited, actorName,
                        nameFor(store, event.getTargetUserId()), groupName != null ? groupName : "");
            default:
                return "";
        }
    }

    public static String relativeTime(Context context, String createdAt) {
        Duration elapsed = Duration.between(Instant.parse(createdAt), Instant.now());
        long minutes = elapsed.toMinutes();
        if (minutes < 1) return context.getString(R.string.time_just_now);
        if (minutes < 60) return context.getString(R.string.time_minutes_ago, minutes);
        long hours = elapsed.toHours();
        if (hours < 24) return context.getString(R.string.time_hours_ago, hours);
        long days = elapsed.toDays();
        if (days == 1) return context.getString(R.string.time_yesterday);
        return context.getString(R.string.time_days_ago, days);
    }

    public static String sectionFor(Context context, String createdAt) {
        Instant createdInstant = Instant.parse(createdAt);
        long days = Duration.between(createdInstant, Instant.now()).toDays();
        if (days < 1) return context.getString(R.string.section_today);
        if (days < 2) return context.getString(R.string.section_yesterday);
        return context.getString(R.string.section_earlier);
    }

    private static String nameFor(AppDataStore store, String userId) {
        if (userId == null) return "";
        if (AppDataStore.CURRENT_USER_ID.equals(userId)) return "You";
        User user = store.getUserById(userId);
        return user != null ? user.getUsername() : "";
    }

    private static String groupNameFor(AppDataStore store, String groupId) {
        Group group = store.getGroupById(groupId);
        return group != null ? group.getName() : null;
    }
}
