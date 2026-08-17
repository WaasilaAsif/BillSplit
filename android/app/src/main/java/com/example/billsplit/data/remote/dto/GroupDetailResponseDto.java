package com.example.billsplit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Wire shape for GET /groups/:id — {group, members, expenses}. */
public class GroupDetailResponseDto {
    @SerializedName("group")
    private GroupDto group;
    @SerializedName("members")
    private List<GroupMemberDto> members;
    @SerializedName("expenses")
    private List<ExpenseDto> expenses;

    public GroupDto getGroup() {
        return group;
    }

    public List<GroupMemberDto> getMembers() {
        return members;
    }

    public List<ExpenseDto> getExpenses() {
        return expenses;
    }
}
