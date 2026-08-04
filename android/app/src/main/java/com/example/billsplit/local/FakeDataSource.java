package com.example.billsplit.local;


import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.ExpenseItem;
import com.example.billsplit.data.model.ExpenseSplit;
import com.example.billsplit.data.model.Friend;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.GroupMember;
import com.example.billsplit.data.model.SplitType;
import com.example.billsplit.data.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FakeDataSource {


    public static final String CURRENT_USER_ID = "u1";

    public static List<User> getUsers() {
        return Arrays.asList(
                new User("u1", "waasila", "waasila@example.com", "2026-01-10T09:00:00Z"),
                new User("u2", "maira", "maira@example.com", "2026-01-10T09:05:00Z"),
                new User("u3", "ahmed", "ahmed@example.com", "2026-01-11T14:20:00Z"),
                new User("u4", "sara", "sara@example.com", "2026-01-12T08:15:00Z")
        );
    }

    public static List<Group> getGroups() {
        List<Group> groups = new ArrayList<>();

        Group roommates = new Group("g1", "Roommates", "u1", false, false, "2026-01-10T09:10:00Z");
        roommates.setCurrentUserBalance(1250.00); // you're owed

        Group trip = new Group("g2", "Murree Trip", "u1", true, false, "2026-02-01T10:00:00Z");
        trip.setCurrentUserBalance(-800.00); // you owe

        Group office = new Group("g3", "Office Lunch Squad", "u3", false, false, "2026-01-20T12:00:00Z");
        office.setCurrentUserBalance(0.00); // settled

        groups.add(roommates);
        groups.add(trip);
        groups.add(office);
        return groups;
    }

    public static List<GroupMember> getGroupMembers(String groupId) {
        switch (groupId) {
            case "g1":
                return Arrays.asList(
                        new GroupMember("g1", "u1", "2026-01-10T09:10:00Z", "waasila", "waasila@example.com"),
                        new GroupMember("g1", "u2", "2026-01-10T09:11:00Z", "maira", "maira@example.com")
                );
            case "g2":
                return Arrays.asList(
                        new GroupMember("g2", "u1", "2026-02-01T10:00:00Z", "waasila", "waasila@example.com"),
                        new GroupMember("g2", "u2", "2026-02-01T10:01:00Z", "maira", "maira@example.com"),
                        new GroupMember("g2", "u3", "2026-02-01T10:02:00Z", "ahmed", "ahmed@example.com")
                );
            case "g3":
                return Arrays.asList(
                        new GroupMember("g3", "u1", "2026-01-20T12:00:00Z", "waasila", "waasila@example.com"),
                        new GroupMember("g3", "u3", "2026-01-20T12:01:00Z", "ahmed", "ahmed@example.com"),
                        new GroupMember("g3", "u4", "2026-01-20T12:02:00Z", "sara", "sara@example.com")
                );
            default:
                return new ArrayList<>();
        }
    }

    public static List<Expense> getExpenses(String groupId) {
        List<Expense> all = new ArrayList<>();

        Expense rent = new Expense();
        rent.setId("e1");
        rent.setGroupId("g1");
        rent.setPaidBy("u1");
        rent.setAmount(2500.00);
        rent.setDescription("January Rent");
        rent.setCategory("Housing");
        rent.setSplitType(SplitType.EQUAL);
        rent.setTaxAmount(0);
        rent.setCreatedAt("2026-01-15T09:00:00Z");
        rent.setSplits(Arrays.asList(
                new ExpenseSplit("u1", 1250.00),
                new ExpenseSplit("u2", 1250.00)
        ));
        all.add(rent);

        Expense dinner = new Expense();
        dinner.setId("e2");
        dinner.setGroupId("g2");
        dinner.setPaidBy("u2");
        dinner.setAmount(4500.00);
        dinner.setDescription("Dinner at Cafe Aylanto");
        dinner.setCategory("Food");
        dinner.setSplitType(SplitType.ITEMIZED);
        dinner.setTaxAmount(300.00);
        dinner.setCreatedAt("2026-02-02T20:30:00Z");
        dinner.setItems(Arrays.asList(
                new ExpenseItem("Biryani", 1200.00, "u1"),
                new ExpenseItem("Karahi", 1800.00, "u2"),
                new ExpenseItem("Drinks", 1000.00, "u3")
        ));
        dinner.setSplits(Arrays.asList(
                new ExpenseSplit("u1", 1300.00),
                new ExpenseSplit("u2", 1900.00),
                new ExpenseSplit("u3", 1300.00)
        ));
        all.add(dinner);

        Expense lunch = new Expense();
        lunch.setId("e3");
        lunch.setGroupId("g3");
        lunch.setPaidBy("u4");
        lunch.setAmount(1200.00);
        lunch.setDescription("Team Lunch");
        lunch.setCategory("Food");
        lunch.setSplitType(SplitType.EQUAL);
        lunch.setTaxAmount(0);
        lunch.setCreatedAt("2026-01-21T13:00:00Z");
        lunch.setSplits(Arrays.asList(
                new ExpenseSplit("u1", 400.00),
                new ExpenseSplit("u3", 400.00),
                new ExpenseSplit("u4", 400.00)
        ));
        all.add(lunch);

        if (groupId == null) {
            return all;
        }
        List<Expense> filtered = new ArrayList<>();
        for (Expense e : all) {
            if (groupId.equals(e.getGroupId())) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    public static List<Friend> getFriends() {
        return Arrays.asList(
                new Friend("u2", "maira", "maira@example.com", 450.00),   // owed to you
                new Friend("u3", "ahmed", "ahmed@example.com", -300.00),  // you owe
                new Friend("u4", "sara", "sara@example.com", 0.00)        // settled
        );
    }

    public static User getCurrentUser() {
        for (User u : getUsers()) {
            if (u.getId().equals(CURRENT_USER_ID)) {
                return u;
            }
        }
        return null;
    }
}