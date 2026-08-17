package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/** Wire shape for a row in GET /settlements. */
public class SettlementDto {
    //all the apis give the alternate names for the vars in the apis
    //This is a DTO class that is used to transfer data from the backend to the frontend. The model class is used to represent the data in the frontend. The DTO class is used to transfer data from the backend to the frontend. The model class is used to represent the data in the frontend.
    @SerializedName("id")
    private String id;
    @SerializedName("group_id")
    private String groupId;
    @SerializedName("from_user_id")
    private String fromUserId;
    @SerializedName("to_user_id")
    private String toUserId;
    @SerializedName("amount")
    private String amount;
    @SerializedName("status")
    private String status;
    @SerializedName("created_at")
    private String createdAt;

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public double getAmount() {
        return amount != null ? Double.parseDouble(amount) : 0;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
