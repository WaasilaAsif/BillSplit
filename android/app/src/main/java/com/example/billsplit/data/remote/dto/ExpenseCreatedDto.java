package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/** Response from POST /expenses and PUT /expenses/:id — {id, amount, description, split_type}. */
public class ExpenseCreatedDto {
    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }
}
