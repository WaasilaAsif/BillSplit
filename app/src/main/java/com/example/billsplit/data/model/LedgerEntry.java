package com.example.billsplit.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Matches `ledger_entries` table — append-only, never updated or deleted.
 * Balances are always computed by summing these, never stored as a
 * mutable number. Either expenseId or settlementId is set, not both.
 */
public class LedgerEntry {

    @SerializedName("id")
    private String id;

    @SerializedName("from_user_id")
    private String fromUserId;

    @SerializedName("to_user_id")
    private String toUserId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("expense_id")
    private String expenseId; // nullable

    @SerializedName("settlement_id")
    private String settlementId; // nullable

    @SerializedName("created_at")
    private String createdAt;

    public LedgerEntry() {
        // Required by Gson
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(String settlementId) {
        this.settlementId = settlementId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}