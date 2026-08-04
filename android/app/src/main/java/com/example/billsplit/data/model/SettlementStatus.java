package com.example.billsplit.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Matches the `status` column on `settlements`.
 */
public enum SettlementStatus {
    @SerializedName("pending")
    PENDING,

    @SerializedName("completed")
    COMPLETED,

    @SerializedName("failed")
    FAILED
}