package com.example.billsplit.data.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Expense {

    @SerializedName("id")
    private String id;

    @SerializedName("group_id")
    private String groupId; // nullable

    @SerializedName("paid_by")
    private String paidBy;

    @SerializedName("amount")
    private double amount;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("split_type")
    private SplitType splitType;

    @SerializedName("receipt_url")
    private String receiptUrl; // nullable

    @SerializedName("tax_amount")
    private double taxAmount; // defaults to 0

    @SerializedName("created_at")
    private String createdAt;

    // Populated on the Expense Detail screen when split_type is ITEMIZED.
    // Not present on list endpoints — kept nullable/transient by convention.
    //May or may not have so have to look deeper in it's functionaluity requisites to create functions
    @SerializedName("items")
    private List<ExpenseItem> items;

    // Per-user share breakdown, populated on Expense Detail.
    @SerializedName("splits")
    private List<ExpenseSplit> splits;

    public Expense() {
        // Required by Gson
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<ExpenseItem> getItems() {
        return items;
    }

    public void setItems(List<ExpenseItem> items) {
        this.items = items;
    }

    public List<ExpenseSplit> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseSplit> splits) {
        this.splits = splits;
    }
}
