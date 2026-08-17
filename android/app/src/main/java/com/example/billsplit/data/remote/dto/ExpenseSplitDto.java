package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ExpenseSplitDto {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("share_amount")
    private String shareAmount;
    @SerializedName("username")
    private String username;

    public String getUserId() {
        return userId;
    }

    public double getShareAmount() {
        return shareAmount != null ? Double.parseDouble(shareAmount) : 0;
    }

    public String getUsername() {
        return username;
    }
}
