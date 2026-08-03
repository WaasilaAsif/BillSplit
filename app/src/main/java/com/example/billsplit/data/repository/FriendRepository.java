package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.ExpenseSplit;
import com.example.billsplit.data.model.Friend;
import com.example.billsplit.data.model.Settlement;
import com.example.billsplit.data.model.User;
import com.example.billsplit.local.AppDataStore;
import com.example.billsplit.util.BalanceCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FriendRepository {

    private final AppDataStore store = AppDataStore.getInstance();

    public List<Friend> getFriends() {
        String userId = AppDataStore.CURRENT_USER_ID;
        List<Expense> allExpenses = store.getAllExpenses();
        List<Settlement> allSettlements = store.getAllSettlements();

        List<Friend> friends = new ArrayList<>();
        for (String friendId : store.getFriendUserIds()) {
            User user = store.getUserById(friendId);
            if (user == null) continue;
            double balance = BalanceCalculator.pairBalance(allExpenses, allSettlements, userId, friendId);
            friends.add(new Friend(user.getId(), user.getUsername(), user.getEmail(), balance));
        }
        return friends;
    }

    public Friend getFriend(String friendId) {
        for (Friend f : getFriends()) {
            if (f.getUserId().equals(friendId)) return f;
        }
        return null;
    }

    public User getUser(String userId) {
        return store.getUserById(userId);
    }

    /** Expenses that both the current user and `friendId` participate in, newest first. */
    public List<Expense> getSharedExpenses(String friendId) {
        String userId = AppDataStore.CURRENT_USER_ID;
        List<Expense> shared = new ArrayList<>();
        for (Expense e : store.getAllExpenses()) {
            if (involves(e, userId) && involves(e, friendId)) {
                shared.add(e);
            }
        }
        shared.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return shared;
    }

    private boolean involves(Expense e, String userId) {
        if (userId.equals(e.getPaidBy())) return true;
        if (e.getSplits() == null) return false;
        for (ExpenseSplit s : e.getSplits()) {
            if (userId.equals(s.getUserId())) return true;
        }
        return false;
    }

    /** Users matching `query` by name/email, excluding self and existing friends. */
    public List<User> search(String query) {
        String needle = query.trim().toLowerCase(Locale.US);
        List<User> results = new ArrayList<>();
        if (needle.isEmpty()) return results;
        List<String> existingFriendIds = store.getFriendUserIds();
        for (User u : store.getUsers()) {
            if (u.getId().equals(AppDataStore.CURRENT_USER_ID)) continue;
            if (existingFriendIds.contains(u.getId())) continue;
            if (u.getUsername().toLowerCase(Locale.US).contains(needle)
                    || u.getEmail().toLowerCase(Locale.US).contains(needle)) {
                results.add(u);
            }
        }
        return results;
    }

    public void addFriend(String userId) {
        store.addFriend(userId);
    }

    public User addFriendByEmailOrUsername(String query) {
        return store.addFriendByEmailOrUsername(query);
    }
}
