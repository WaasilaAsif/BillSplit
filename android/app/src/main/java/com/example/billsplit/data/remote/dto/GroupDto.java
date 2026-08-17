package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/** Wire shape from GET/POST /groups. Mapped to the domain Group model in GroupRepository. */
public class GroupDto {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("is_temporary")
    private boolean isTemporary;
    @SerializedName("is_archived")
    private boolean isArchived;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("current_user_balance")
    private String currentUserBalance; // numeric comes back as a string from pg's NUMERIC type
    @SerializedName("member_count")
    private String memberCount; // same — COUNT(*) comes back as a string too

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public double getCurrentUserBalance() {
        return currentUserBalance != null ? Double.parseDouble(currentUserBalance) : 0;
    }

    public int getMemberCount() {
        return memberCount != null ? Integer.parseInt(memberCount) : 0;
    }
}
