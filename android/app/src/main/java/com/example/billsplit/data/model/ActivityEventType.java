package com.example.billsplit.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Kind of event shown on the Activity Feed / Notifications screens.
 */
public enum ActivityEventType {
    @SerializedName("expense_added")
    EXPENSE_ADDED,

    @SerializedName("payment_made")
    PAYMENT_MADE,

    @SerializedName("settled_up")
    SETTLED_UP,

    @SerializedName("member_invited")
    MEMBER_INVITED
}
