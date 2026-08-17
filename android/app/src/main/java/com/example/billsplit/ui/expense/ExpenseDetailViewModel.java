package com.example.billsplit.ui.expense;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.ExpenseSplit;
import com.example.billsplit.data.repository.ApiCallback;
import com.example.billsplit.data.repository.ExpenseRepository;
import com.example.billsplit.local.TokenManager;

public class ExpenseDetailViewModel extends ViewModel {

    private final ExpenseRepository expenseRepository = new ExpenseRepository();

    private final MutableLiveData<Expense> expense = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleted = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Expense> getExpense() {
        return expense;
    }

    public LiveData<Boolean> getDeleted() {
        return deleted;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void load(String expenseId) {
        expenseRepository.getExpense(expenseId, new ApiCallback<Expense>() {
            @Override
            public void onSuccess(Expense result) {
                expense.setValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void delete() {
        Expense e = expense.getValue();
        if (e == null) return;
        expenseRepository.deleteExpense(e.getId(), new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleted.setValue(true);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

//   Who should pay whom to settle this specific expense, and how much.
    public SettleTarget resolveSettleTarget() {
        Expense e = expense.getValue();
        String userId = TokenManager.getInstance().getCurrentUserId();
        if (e == null || e.getSplits() == null || userId == null) return null;

        if (userId.equals(e.getPaidBy())) {
            String biggestDebtorId = null;
            double biggestShare = 0;
            for (ExpenseSplit s : e.getSplits()) {
                if (!userId.equals(s.getUserId()) && s.getShareAmount() > biggestShare) {
                    biggestShare = s.getShareAmount();
                    biggestDebtorId = s.getUserId();
                }
            }
            if (biggestDebtorId == null) return null;
            return new SettleTarget(biggestDebtorId, userId, biggestShare);
        }

        for (ExpenseSplit s : e.getSplits()) {
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
