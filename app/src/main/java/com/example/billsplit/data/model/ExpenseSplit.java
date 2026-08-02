package com.example.billsplit.data.model;


import com.google.gson.annotations.SerializedName;

/**
 * Matches `expense_splits` table — each participant's share of an expense,
 * regardless of split_type (equal/exact/percentage/itemized all resolve
 * down to concrete share_amount rows here).
 */
public class ExpenseSplit {

    @SerializedName("id")
    private String id;

    @SerializedName("expense_id")
    private String expenseId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("share_amount")
    private double shareAmount;

    // Denormalized for display on Expense Detail's per-person breakdown.
    @SerializedName("username")
    private String username;

    public ExpenseSplit() {
        // Required by Gson
    }

    public ExpenseSplit(String userId, double shareAmount) {
        this.userId = userId;
        this.shareAmount = shareAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(double shareAmount) {
        this.shareAmount = shareAmount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}