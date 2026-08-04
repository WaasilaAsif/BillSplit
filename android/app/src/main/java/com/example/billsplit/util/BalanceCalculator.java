package com.example.billsplit.util;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.ExpenseSplit;
import com.example.billsplit.data.model.Settlement;
import com.example.billsplit.data.model.SettlementStatus;

import java.util.List;

/**
 * Balances are derived from Expense.splits + paidBy + recorded Settlements
 * rather than the LedgerEntry table — LedgerEntry is reserved for a real
 * backend (see its own doc comment); recomputing here keeps the fake data
 * layer self-contained.
 *
 * Sign convention throughout: positive = they're owed / owed to you,
 * negative = they owe / you owe.
 */
public final class BalanceCalculator {

    private BalanceCalculator() {
    }

    /** Net balance for `userId` across the given expenses (usually one group's). */
    public static double userBalance(List<Expense> expenses, String userId) {
        double net = 0;
        for (Expense e : expenses) {
            List<ExpenseSplit> splits = e.getSplits();
            if (splits == null) continue;
            if (userId.equals(e.getPaidBy())) {
                net += e.getAmount();
            }
            for (ExpenseSplit split : splits) {
                if (userId.equals(split.getUserId())) {
                    net -= split.getShareAmount();
                }
            }
        }
        return net;
    }

    /** Applies completed settlements on top of an expense-derived balance. */
    public static double applySettlements(double expenseBalance, List<Settlement> settlements, String userId) {
        double net = expenseBalance;
        for (Settlement s : settlements) {
            if (s.getStatus() != SettlementStatus.COMPLETED) continue;
            if (userId.equals(s.getFromUserId())) {
                net += s.getAmount(); // paying down what you owed
            } else if (userId.equals(s.getToUserId())) {
                net -= s.getAmount(); // received payment, owed less now
            }
        }
        return net;
    }

    /** Net balance between exactly two users across a shared set of expenses. */
    public static double pairBalance(List<Expense> expenses, List<Settlement> settlements,
                                      String userId, String otherUserId) {
        double net = 0;
        for (Expense e : expenses) {
            List<ExpenseSplit> splits = e.getSplits();
            if (splits == null) continue;
            boolean userPaid = userId.equals(e.getPaidBy());
            boolean otherPaid = otherUserId.equals(e.getPaidBy());
            if (!userPaid && !otherPaid) continue;

            Double userShare = shareOf(splits, userId);
            Double otherShare = shareOf(splits, otherUserId);
            if (userPaid && otherShare != null) {
                net += otherShare; // they owe user their share
            }
            if (otherPaid && userShare != null) {
                net -= userShare; // user owes them their share
            }
        }
        for (Settlement s : settlements) {
            if (s.getStatus() != SettlementStatus.COMPLETED) continue;
            if (userId.equals(s.getFromUserId()) && otherUserId.equals(s.getToUserId())) {
                net += s.getAmount();
            } else if (otherUserId.equals(s.getFromUserId()) && userId.equals(s.getToUserId())) {
                net -= s.getAmount();
            }
        }
        return net;
    }

    private static Double shareOf(List<ExpenseSplit> splits, String userId) {
        for (ExpenseSplit split : splits) {
            if (userId.equals(split.getUserId())) {
                return split.getShareAmount();
            }
        }
        return null;
    }
}
