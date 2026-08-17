package com.example.billsplit.data.remote.dto;

/**
 * Request-side split entry for percentage/exact split types. Whichever of
 * percentage/shareAmount applies to the split_type being sent is set; the
 * other stays null and Gson omits it. Not exercised by the current UI
 * (ExpenseFormFragment only builds 'equal' splits) — included so
 * CreateExpenseRequest covers the full backend contract.
 */
public class ExpenseSplitInput {
    private final String user_id;
    private final Double percentage;
    private final Double share_amount;

    public static ExpenseSplitInput byPercentage(String userId, double percentage) {
        return new ExpenseSplitInput(userId, percentage, null);
    }

    public static ExpenseSplitInput byExactAmount(String userId, double shareAmount) {
        return new ExpenseSplitInput(userId, null, shareAmount);
    }

    private ExpenseSplitInput(String userId, Double percentage, Double shareAmount) {
        this.user_id = userId;
        this.percentage = percentage;
        this.share_amount = shareAmount;
    }
}
