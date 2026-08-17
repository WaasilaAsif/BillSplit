package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/** Wire shape for a row from GET /groups/:groupId/expenses or the expenses[] in group detail. */
public class ExpenseDto {
    @SerializedName("id")
    private String id;
    @SerializedName("group_id")
    private String groupId;
    @SerializedName("paid_by")
    private String paidBy;
    @SerializedName("amount")
    private String amount;
    @SerializedName("description")
    private String description;
    @SerializedName("category")
    private String category;
    @SerializedName("split_type")
    private String splitType;
    @SerializedName("receipt_url")
    private String receiptUrl;
    @SerializedName("tax_amount")
    private String taxAmount;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("your_share")
    private String yourShare; // nullable — only present on list endpoints, see listExpensesForGroup/getGroupDetail
    @SerializedName("group_name")
    private String groupName; // nullable — only present from getSharedExpenses (friendController.js)

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public double getAmount() {
        return amount != null ? Double.parseDouble(amount) : 0;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getSplitType() {
        return splitType;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public double getTaxAmount() {
        return taxAmount != null ? Double.parseDouble(taxAmount) : 0;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    /** Null if the viewer has no split row on this expense (shouldn't normally happen within a group they're in). */
    public Double getYourShare() {
        return yourShare != null ? Double.parseDouble(yourShare) : null;
    }

    public String getGroupName() {
        return groupName;
    }
}
