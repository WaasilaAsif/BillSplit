package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/** Wire shape for a row in GET /groups/:id/balances. */
public class GroupBalanceDto {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("username")
    private String username;
    @SerializedName("balance")
    private String balance; // NUMERIC comes back as a string

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance != null ? Double.parseDouble(balance) : 0;
    }
}
