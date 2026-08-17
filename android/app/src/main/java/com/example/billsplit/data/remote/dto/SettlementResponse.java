package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;


public class SettlementResponse {
    //As alwats they give the alternate names for the vars in the apis
    @SerializedName("settlement_id")
    private String settlementId;
    @SerializedName("was_already_processed")
    private boolean wasAlreadyProcessed;

    public String getSettlementId() {
        return settlementId;
    }

    public boolean wasAlreadyProcessed() {
        return wasAlreadyProcessed;
    }
}
