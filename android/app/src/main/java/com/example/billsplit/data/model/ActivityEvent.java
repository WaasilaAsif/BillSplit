package com.example.billsplit.data.model;

import com.google.gson.annotations.SerializedName;

public class ActivityEvent {

    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private ActivityEventType type;

    @SerializedName("actor_user_id")
    private String actorUserId;

    @SerializedName("group_id")
    private String groupId; // nullable

    @SerializedName("expense_id")
    private String expenseId; // nullable

    @SerializedName("target_user_id")
    private String targetUserId; // nullable  e.g. who a payment was made to

    @SerializedName("amount")
    private Double amount; // nullable

    @SerializedName("description")
    private String description; // nullable e.g. expense description snapshot

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("read")
    private boolean read;

    public ActivityEvent() {
        // Required by Gson
    }

    public ActivityEvent(String id, ActivityEventType type, String actorUserId, String groupId,
                          String expenseId, String targetUserId, Double amount,
                          String description, String createdAt, boolean read) {
        this.id = id;
        this.type = type;
        this.actorUserId = actorUserId;
        this.groupId = groupId;
        this.expenseId = expenseId;
        this.targetUserId = targetUserId;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ActivityEventType getType() {
        return type;
    }

    public void setType(ActivityEventType type) {
        this.type = type;
    }

    public String getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(String actorUserId) {
        this.actorUserId = actorUserId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
