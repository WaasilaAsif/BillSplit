package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Used both ways: as a request payload (only itemName/itemPrice/assignedToUserId
 * set — id/expenseId stay null and Gson omits null fields on serialize) and as
 * a response row (all fields populated) from GET /expenses/:id when itemized.
 */
public class ExpenseItemDto {
    @SerializedName("id")
    private String id;
    @SerializedName("expense_id")
    private String expenseId;
    @SerializedName("item_name")
    private String itemName;
    @SerializedName("item_price")
    private String itemPrice;
    @SerializedName("assigned_to_user_id")
    private String assignedToUserId;

    public ExpenseItemDto() {
    }

    public ExpenseItemDto(String itemName, double itemPrice, String assignedToUserId) {
        this.itemName = itemName;
        this.itemPrice = String.valueOf(itemPrice);
        this.assignedToUserId = assignedToUserId;
    }

    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice != null ? Double.parseDouble(itemPrice) : 0;
    }

    public String getAssignedToUserId() {
        return assignedToUserId;
    }
}
