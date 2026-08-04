package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.Settlement;
import com.example.billsplit.local.AppDataStore;

public class SettlementRepository {

    private final AppDataStore store = AppDataStore.getInstance();

    public Settlement recordSettlement(String groupId, String fromUserId, String toUserId, double amount) {
        return store.recordSettlement(groupId, fromUserId, toUserId, amount);
    }
}
