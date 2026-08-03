package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.ExpenseSplit;
import com.example.billsplit.data.model.SplitType;
import com.example.billsplit.local.AppDataStore;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {

    private final AppDataStore store = AppDataStore.getInstance();

    public List<Expense> getExpensesForGroup(String groupId) {
        List<Expense> expenses = new ArrayList<>(store.getExpensesForGroup(groupId));
        expenses.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return expenses;
    }

    public Expense getExpense(String expenseId) {
        return store.getExpenseById(expenseId);
    }

    public Expense addExpense(String groupId, String paidBy, double amount, String description,
                               String category, SplitType splitType, List<ExpenseSplit> splits) {
        return store.addExpense(groupId, paidBy, amount, description, category, splitType, splits);
    }

    public void updateExpense(Expense expense) {
        store.updateExpense(expense);
    }

    public void deleteExpense(String expenseId) {
        store.deleteExpense(expenseId);
    }

    /** Equal split across the given member ids, evenly to the cent. */
    public static List<ExpenseSplit> buildEqualSplits(double amount, List<String> memberIds) {
        List<ExpenseSplit> splits = new ArrayList<>();
        if (memberIds.isEmpty()) return splits;
        double share = Math.round((amount / memberIds.size()) * 100.0) / 100.0;
        for (String userId : memberIds) {
            splits.add(new ExpenseSplit(userId, share));
        }
        return splits;
    }
}
