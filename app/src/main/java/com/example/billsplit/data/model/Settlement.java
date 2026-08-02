package com.example.billsplit.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Matches `settlements` table. `idempotencyKey` is generated client-side
 * (e.g. UUID) and sent with the Settle Up request so retries on flaky
 * network don't create duplicate settlements.
 */
public class Settlement {

    @SerializedName("id")
    private String id;

    @SerializedName("group_id")
    private String groupId;

    @SerializedName("from_user_id")
    private String fromUserId;

    @SerializedName("to_user_id")
    private String toUserId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("idempotency_key")
    private String idempotencyKey;

    @SerializedName("status")
    private SettlementStatus status;

    @SerializedName("created_at")
    private String createdAt;

    public Settlement() {
        // Required by Gson
    }

    public Settlement(String groupId, String fromUserId, String toUserId, double amount, String idempotencyKey) {
        this.groupId = groupId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.status = SettlementStatus.PENDING;
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

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public SettlementStatus getStatus() {
        return status;
    }

    public void setStatus(SettlementStatus status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
