package com.example.billsplit.data.model;

import java.util.List;

//  The backend's GET /groups/:id returns group+members+expenses together in
//  one response there's no lighter "just the group" endpoint, so this
//  wraps all three the way the API actually shapes it.

public class GroupDetail {
    private final Group group;
    private final List<GroupMember> members;
    private final List<Expense> expenses;

    public GroupDetail(Group group, List<GroupMember> members, List<Expense> expenses) {
        this.group = group;
        this.members = members;
        this.expenses = expenses;
    }

    public Group getGroup() {
        return group;
    }

    public List<GroupMember> getMembers() {
        return members;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }
}
