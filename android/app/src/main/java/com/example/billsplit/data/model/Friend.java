package com.example.billsplit.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Not a direct table — a derived view for the Friends / Friend Detail
 * screens: a User plus their GLOBAL balance (sum of all ledger_entries
 * between you and them, across every group AND direct expenses combined).
 * This is the "friend-scoped" balance, distinct from a group-scoped one.
 */
public class Friend {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    // Positive = they owe you. Negative = you owe them. Zero = settled up.
    @SerializedName("net_balance")
    private double netBalance;

    public Friend() {
        // Required by Gson
    }

    public Friend(String userId, String username, String email, double netBalance) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.netBalance = netBalance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(double netBalance) {
        this.netBalance = netBalance;
    }

    public boolean isOwedToYou() {
        return netBalance > 0;
    }

    public boolean isSettled() {
        return Math.abs(netBalance) < 0.01;
    }
}
