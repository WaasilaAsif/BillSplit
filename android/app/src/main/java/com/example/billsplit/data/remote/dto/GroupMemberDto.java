package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/** Wire shape for a row in GroupDetailResponse.members and the POST /groups/:id/members response. */
public class GroupMemberDto {
    @SerializedName("group_id")
    private String groupId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("joined_at")
    private String joinedAt;
    @SerializedName("invited_by")
    private String invitedBy;

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public String getInvitedBy() {
        return invitedBy;
    }
}
