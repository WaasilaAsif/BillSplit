package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/** Wire shape for GET /friends/:userId/balance — {user_id, net_balance}. */
public class FriendBalanceDto {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("net_balance")
    private String netBalance;

    public String getUserId() {
        return userId;
    }

    public double getNetBalance() {
        return netBalance != null ? Double.parseDouble(netBalance) : 0;
    }
}
