package com.example.billsplit.data.model;
import com.google.gson.annotations.SerializedName;


public class GroupMember {

    @SerializedName("group_id")
    private String groupId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("joined_at")
    private String joinedAt;

    // Denormalized for display — avoids a second lookup per member on
    // Group Detail / Add Expense (who to split with) screens.
    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    public GroupMember() {
        // Required by Gson
    }

    public GroupMember(String groupId, String userId, String joinedAt, String username, String email) {
        this.groupId = groupId;
        this.userId = userId;
        this.joinedAt = joinedAt;
        this.username = username;
        this.email = email;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(String joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}