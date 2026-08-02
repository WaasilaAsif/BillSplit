package com.example.billsplit.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * How an expense's amount is divided among participants.
 * Matches the `split_type` column on `expenses`.
 */
public enum SplitType {
    @SerializedName("equal")
    EQUAL,

    @SerializedName("exact")
    EXACT,

    @SerializedName("percentage")
    PERCENTAGE,

    @SerializedName("itemized")
    ITEMIZED
}
