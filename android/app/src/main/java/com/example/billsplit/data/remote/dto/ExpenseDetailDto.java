package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Wire shape for GET /expenses/:id — every ExpenseDto field plus splits[] and items[]. */
public class ExpenseDetailDto extends ExpenseDto {
    @SerializedName("splits")
    private List<ExpenseSplitDto> splits;
    @SerializedName("items")
    private List<ExpenseItemDto> items;

    public List<ExpenseSplitDto> getSplits() {
        return splits;
    }

    public List<ExpenseItemDto> getItems() {
        return items;
    }
}
