package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.GroupMember;
import com.example.billsplit.data.model.Settlement;
import com.example.billsplit.data.model.User;
import com.example.billsplit.local.AppDataStore;
import com.example.billsplit.util.BalanceCalculator;

import java.util.ArrayList;
import java.util.List;

public class GroupRepository {

    private final AppDataStore store = AppDataStore.getInstance();

    /** Groups with `currentUserBalance` populated from real expense/settlement data. */
    public List<Group> getGroups() {
        List<Group> groups = store.getGroups();
        String userId = AppDataStore.CURRENT_USER_ID;
        for (Group g : groups) {
            List<Expense> expenses = store.getExpensesForGroup(g.getId());
            List<Settlement> settlements = store.getSettlementsForGroup(g.getId());
            double balance = BalanceCalculator.applySettlements(
                    BalanceCalculator.userBalance(expenses, userId), settlements, userId);
            g.setCurrentUserBalance(balance);
        }
        return groups;
    }

    public Group getGroup(String groupId) {
        return store.getGroupById(groupId);
    }

    public List<GroupMember> getGroupMembers(String groupId) {
        return store.getGroupMembers(groupId);
    }

    public List<User> getGroupMemberUsers(String groupId) {
        List<User> users = new ArrayList<>();
        for (GroupMember m : store.getGroupMembers(groupId)) {
            User u = store.getUserById(m.getUserId());
            if (u != null) users.add(u);
        }
        return users;
    }

    public Group createGroup(String name) {
        return store.createGroup(name);
    }

    public void addMember(String groupId, User user) {
        store.addGroupMember(groupId, user);
    }

    public void removeMember(String groupId, String userId) {
        store.removeGroupMember(groupId, userId);
    }

    /** Per-member net balance within a single group, for the Balances tab. */
    public java.util.Map<String, Double> getMemberBalances(String groupId) {
        java.util.Map<String, Double> balances = new java.util.LinkedHashMap<>();
        List<Expense> expenses = store.getExpensesForGroup(groupId);
        List<Settlement> settlements = store.getSettlementsForGroup(groupId);
        for (GroupMember m : store.getGroupMembers(groupId)) {
            double balance = BalanceCalculator.applySettlements(
                    BalanceCalculator.userBalance(expenses, m.getUserId()), settlements, m.getUserId());
            balances.put(m.getUserId(), balance);
        }
        return balances;
    }

    public double getGroupSpendTotal(String groupId) {
        double total = 0;
        for (Expense e : store.getExpensesForGroup(groupId)) {
            total += e.getAmount();
        }
        return total;
    }

    /** Aggregate "you owe RsX / you're owed RsY" across every group, for the dashboard card. */
    public DashboardSummary getDashboardSummary() {
        String userId = AppDataStore.CURRENT_USER_ID;
        double youOwe = 0;
        double youAreOwed = 0;
        for (Group g : getGroups()) {
            double balance = g.getCurrentUserBalance();
            if (balance < 0) youOwe += -balance;
            else youAreOwed += balance;
        }
        return new DashboardSummary(youOwe, youAreOwed);
    }

    public static class DashboardSummary {
        public final double youOwe;
        public final double youAreOwed;

        public DashboardSummary(double youOwe, double youAreOwed) {
            this.youOwe = youOwe;
            this.youAreOwed = youAreOwed;
        }

        public double getNet() {
            return youAreOwed - youOwe;
        }
    }
}
