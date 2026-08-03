package com.example.billsplit.ui.expense;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.repository.ExpenseRepository;
import com.example.billsplit.local.AppDataStore;

public class ExpenseDetailViewModel extends ViewModel {

    private final ExpenseRepository expenseRepository = new ExpenseRepository();
    private final AppDataStore store = AppDataStore.getInstance();

    private final MutableLiveData<Expense> expense = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleted = new MutableLiveData<>();

    public LiveData<Expense> getExpense() {
        return expense;
    }

    public LiveData<Boolean> getDeleted() {
        return deleted;
    }

    public void load(String expenseId) {
        expense.setValue(expenseRepository.getExpense(expenseId));
    }

    public void delete() {
        Expense e = expense.getValue();
        if (e != null) {
            expenseRepository.deleteExpense(e.getId());
            deleted.setValue(true);
        }
    }

    /** Who should pay whom to settle this specific expense, and how much. */
    public SettleTarget resolveSettleTarget() {
        Expense e = expense.getValue();
        if (e == null || e.getSplits() == null) return null;
        String userId = AppDataStore.CURRENT_USER_ID;

        if (userId.equals(e.getPaidBy())) {
            String biggestDebtorId = null;
            double biggestShare = 0;
            for (com.example.billsplit.data.model.ExpenseSplit s : e.getSplits()) {
                if (!userId.equals(s.getUserId()) && s.getShareAmount() > biggestShare) {
                    biggestShare = s.getShareAmount();
                    biggestDebtorId = s.getUserId();
                }
            }
            if (biggestDebtorId == null) return null;
            return new SettleTarget(biggestDebtorId, userId, biggestShare);
        }

        for (com.example.billsplit.data.model.ExpenseSplit s : e.getSplits()) {
            if (userId.equals(s.getUserId())) {
                return new SettleTarget(userId, e.getPaidBy(), s.getShareAmount());
            }
        }
        return null;
    }

    public static class SettleTarget {
        public final String fromUserId;
        public final String toUserId;
        public final double amount;

        public SettleTarget(String fromUserId, String toUserId, double amount) {
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            this.amount = amount;
        }
    }
}
