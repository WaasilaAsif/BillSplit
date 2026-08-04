package com.example.billsplit.data.model;
import com.google.gson.annotations.SerializedName;

public class Group {

    //We needed the serialized names for api constructions
    //GSON needs them
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

    // --- Client-only convenience field, not part of the API contract.
    // Populated by the Repository layer after computing balances for the
    // Groups Dashboard summary (e.g. "you're owed $12.50" per group card).

    //transient is used to make it an exception from the serializtion process
    private transient double currentUserBalance;

    public Group() {
        // Required by Gson
        //Just like in prev project to hand;e json format results
    }

    public Group(String id, String name, String createdBy, boolean isTemporary, boolean isArchived, String createdAt) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.isTemporary = isTemporary;
        this.isArchived = isArchived;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getCurrentUserBalance() {
        return currentUserBalance;
    }

    public void setCurrentUserBalance(double currentUserBalance) {
        this.currentUserBalance = currentUserBalance;
    }
}