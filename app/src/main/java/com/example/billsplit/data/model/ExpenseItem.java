package com.example.billsplit.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Matches `expense_items` table — one row per receipt line item,
 * used only when Expense.splitType == ITEMIZED.
 */
public class ExpenseItem {

    @SerializedName("id")
    private String id;

    @SerializedName("expense_id")
    private String expenseId;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("item_price")
    private double itemPrice;

    @SerializedName("assigned_to_user_id")
    private String assignedToUserId;

    public ExpenseItem() {
        // Required by Gson
    }

    public ExpenseItem(String itemName, double itemPrice, String assignedToUserId) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.assignedToUserId = assignedToUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(String assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }
}
