package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/** Wire shape for a row in GET /friends. */
public class FriendDto {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("net_balance")
    private String netBalance;

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public double getNetBalance() {
        return netBalance != null ? Double.parseDouble(netBalance) : 0;
    }
}
