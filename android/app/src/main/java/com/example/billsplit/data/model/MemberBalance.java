package com.example.billsplit.data.model;


public class MemberBalance {
    private final String userId;
    private final String username;
    private final double balance;

    public MemberBalance(String userId, String username, double balance) {
        this.userId = userId;
        this.username = username;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }
}
