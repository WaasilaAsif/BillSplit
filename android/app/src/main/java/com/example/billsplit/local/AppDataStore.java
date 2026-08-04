package com.example.billsplit.local;

import com.example.billsplit.data.model.ActivityEvent;
import com.example.billsplit.data.model.ActivityEventType;
import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.ExpenseSplit;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.GroupMember;
import com.example.billsplit.data.model.Settlement;
import com.example.billsplit.data.model.SettlementStatus;
import com.example.billsplit.data.model.SplitType;
import com.example.billsplit.data.model.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory, mutable, session-scoped data store. FakeDataSource itself
 * returns a fresh static list on every call — nothing persists across a
 * user action. This wraps that seed data in real mutable state so
 * Add Expense / Settle Up / Add Friend / Add Group Member / Create Group
 * actually stick for the session, matching a "fully wired" screen set
 * with no backend yet. Swap for a real Room/network-backed store later;
 * keep repository method signatures the same.
 */
public class AppDataStore {

    private static AppDataStore instance;

    public static final String CURRENT_USER_ID = FakeDataSource.CURRENT_USER_ID;

    private final List<User> users = new ArrayList<>();
    private final List<Group> groups = new ArrayList<>();
    private final List<GroupMember> groupMembers = new ArrayList<>();
    private final List<Expense> expenses = new ArrayList<>();
    private final List<Settlement> settlements = new ArrayList<>();
    private final List<ActivityEvent> activityEvents = new ArrayList<>();
    private final Set<String> friendUserIds = new LinkedHashSet<>();

    private final AtomicInteger idCounter = new AtomicInteger(100);

    public static synchronized AppDataStore getInstance() {
        if (instance == null) {
            instance = new AppDataStore();
        }
        return instance;
    }

    private AppDataStore() {
        seed();
    }

    private String nextId(String prefix) {
        return prefix + idCounter.incrementAndGet();
    }

    private void seed() {
        users.addAll(FakeDataSource.getUsers());
        groups.addAll(FakeDataSource.getGroups());
        for (Group g : groups) {
            groupMembers.addAll(FakeDataSource.getGroupMembers(g.getId()));
        }
        expenses.addAll(FakeDataSource.getExpenses(null));

        // Everyone the current user shares a group with starts out a friend.
        for (GroupMember m : groupMembers) {
            if (!CURRENT_USER_ID.equals(m.getUserId())) {
                friendUserIds.add(m.getUserId());
            }
        }

        seedActivityEvents();
    }

    private void seedActivityEvents() {
        long now = Instant.now().toEpochMilli();
        activityEvents.add(new ActivityEvent(nextId("act"), ActivityEventType.EXPENSE_ADDED,
                "u2", "g2", "e2", null, 45.00, "Dinner at Cafe Aylanto",
                Instant.ofEpochMilli(now - 2 * 3600_000L).toString(), false));
        activityEvents.add(new ActivityEvent(nextId("act"), ActivityEventType.PAYMENT_MADE,
                CURRENT_USER_ID, null, null, "u3", 50.00, null,
                Instant.ofEpochMilli(now - 5 * 3600_000L).toString(), true));
        activityEvents.add(new ActivityEvent(nextId("act"), ActivityEventType.SETTLED_UP,
                "u4", "g3", null, null, null, null,
                Instant.ofEpochMilli(now - 26 * 3600_000L).toString(), true));
        activityEvents.add(new ActivityEvent(nextId("act"), ActivityEventType.EXPENSE_ADDED,
                "u3", "g2", "e1", null, 2500.00, "January Rent",
                Instant.ofEpochMilli(now - 30 * 3600_000L).toString(), true));
        activityEvents.add(new ActivityEvent(nextId("act"), ActivityEventType.MEMBER_INVITED,
                "u3", "g2", null, CURRENT_USER_ID, null, null,
                Instant.ofEpochMilli(now - 3600_000L).toString(), false));
    }

    // --- Reads (defensive copies) ---

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public User getUserById(String id) {
        for (User u : users) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    public User getCurrentUser() {
        return getUserById(CURRENT_USER_ID);
    }

    public List<Group> getGroups() {
        return new ArrayList<>(groups);
    }

    public Group getGroupById(String id) {
        for (Group g : groups) {
            if (g.getId().equals(id)) return g;
        }
        return null;
    }

    public List<GroupMember> getGroupMembers(String groupId) {
        List<GroupMember> result = new ArrayList<>();
        for (GroupMember m : groupMembers) {
            if (m.getGroupId().equals(groupId)) result.add(m);
        }
        return result;
    }

    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    public List<Expense> getExpensesForGroup(String groupId) {
        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses) {
            if (groupId.equals(e.getGroupId())) result.add(e);
        }
        return result;
    }

    public Expense getExpenseById(String id) {
        for (Expense e : expenses) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    public List<Settlement> getAllSettlements() {
        return new ArrayList<>(settlements);
    }

    public List<Settlement> getSettlementsForGroup(String groupId) {
        List<Settlement> result = new ArrayList<>();
        for (Settlement s : settlements) {
            if (groupId != null && groupId.equals(s.getGroupId())) result.add(s);
        }
        return result;
    }

    public List<ActivityEvent> getActivityEvents() {
        return new ArrayList<>(activityEvents);
    }

    public List<String> getFriendUserIds() {
        return new ArrayList<>(friendUserIds);
    }

    // --- Mutations ---

    public synchronized Group createGroup(String name) {
        Group group = new Group(nextId("g"), name, CURRENT_USER_ID, false, false,
                Instant.now().toString());
        groups.add(group);
        groupMembers.add(new GroupMember(group.getId(), CURRENT_USER_ID,
                Instant.now().toString(), getCurrentUser().getUsername(), getCurrentUser().getEmail()));
        return group;
    }

    public synchronized void removeGroupMember(String groupId, String userId) {
        groupMembers.removeIf(m -> m.getGroupId().equals(groupId) && m.getUserId().equals(userId));
    }

    public synchronized void addGroupMember(String groupId, User user) {
        for (GroupMember m : groupMembers) {
            if (m.getGroupId().equals(groupId) && m.getUserId().equals(user.getId())) {
                return; // already a member
            }
        }
        groupMembers.add(new GroupMember(groupId, user.getId(), Instant.now().toString(),
                user.getUsername(), user.getEmail()));
        friendUserIds.add(user.getId());
        activityEvents.add(0, new ActivityEvent(nextId("act"), ActivityEventType.MEMBER_INVITED,
                CURRENT_USER_ID, groupId, null, user.getId(), null, null,
                Instant.now().toString(), true));
    }

    public synchronized Expense addExpense(String groupId, String paidBy, double amount,
                                            String description, String category,
                                            SplitType splitType, List<ExpenseSplit> splits) {
        Expense expense = new Expense();
        expense.setId(nextId("e"));
        expense.setGroupId(groupId);
        expense.setPaidBy(paidBy);
        expense.setAmount(amount);
        expense.setDescription(description);
        expense.setCategory(category);
        expense.setSplitType(splitType);
        expense.setTaxAmount(0);
        expense.setCreatedAt(Instant.now().toString());
        expense.setSplits(splits);
        expenses.add(expense);

        activityEvents.add(0, new ActivityEvent(nextId("act"), ActivityEventType.EXPENSE_ADDED,
                paidBy, groupId, expense.getId(), null, amount, description,
                expense.getCreatedAt(), true));
        return expense;
    }

    public synchronized void updateExpense(Expense updated) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId().equals(updated.getId())) {
                expenses.set(i, updated);
                return;
            }
        }
    }

    public synchronized void deleteExpense(String expenseId) {
        expenses.removeIf(e -> e.getId().equals(expenseId));
    }

    public synchronized Settlement recordSettlement(String groupId, String fromUserId,
                                                      String toUserId, double amount) {
        Settlement settlement = new Settlement(groupId, fromUserId, toUserId, amount,
                nextId("idem"));
        settlement.setId(nextId("s"));
        settlement.setStatus(SettlementStatus.COMPLETED);
        settlement.setCreatedAt(Instant.now().toString());
        settlements.add(settlement);

        activityEvents.add(0, new ActivityEvent(nextId("act"), ActivityEventType.SETTLED_UP,
                fromUserId, groupId, null, toUserId, amount, null,
                settlement.getCreatedAt(), true));
        return settlement;
    }

    public synchronized User addFriendByEmailOrUsername(String query) {
        for (User u : users) {
            if (u.getId().equals(CURRENT_USER_ID)) continue;
            if (u.getEmail().equalsIgnoreCase(query) || u.getUsername().equalsIgnoreCase(query)) {
                friendUserIds.add(u.getId());
                return u;
            }
        }
        return null;
    }

    public synchronized void addFriend(String userId) {
        friendUserIds.add(userId);
    }

    public synchronized void markAllNotificationsRead() {
        for (ActivityEvent e : activityEvents) {
            e.setRead(true);
        }
    }
}
