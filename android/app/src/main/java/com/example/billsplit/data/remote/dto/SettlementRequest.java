package com.example.billsplit.data.remote.dto;

public class SettlementRequest {
    private final String group_id;
    private final String to_user_id;
    private final double amount;
    private final String idempotency_key;

    public SettlementRequest(String groupId, String toUserId, double amount, String idempotencyKey) {
        this.group_id = groupId;
        this.to_user_id = toUserId;
        this.amount = amount;
        this.idempotency_key = idempotencyKey;
    }
}
