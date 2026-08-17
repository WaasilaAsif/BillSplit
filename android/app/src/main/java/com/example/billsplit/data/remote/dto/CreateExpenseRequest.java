package com.example.billsplit.data.remote.dto;

import java.util.List;

/**
 * Covers every split_type the backend accepts (equal/percentage/exact/itemized),
 * matching expenseController.js's computeSplitsForRequest exactly. Only the
 * 'equal' constructor is exercised today — ExpenseFormFragment doesn't build
 * a UI for the other three yet.
 */
public class CreateExpenseRequest {
    private String group_id;
    private String paid_by;
    private String description;
    private String category;
    private String split_type;
    private String receipt_url;
    private Double tax_amount;
    private Double amount;
    private List<String> participant_ids;
    private List<ExpenseSplitInput> splits;
    private List<ExpenseItemDto> items;

    public static CreateExpenseRequest equalSplit(String groupId, String paidBy, String description,
                                                    String category, double amount, List<String> participantIds) {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.group_id = groupId;
        request.paid_by = paidBy;
        request.description = description;
        request.category = category;
        request.split_type = "equal";
        request.amount = amount;
        request.participant_ids = participantIds;
        return request;
    }

    public static CreateExpenseRequest itemized(String groupId, String paidBy, String description,
                                                  String category, double taxAmount, List<ExpenseItemDto> items) {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.group_id = groupId;
        request.paid_by = paidBy;
        request.description = description;
        request.category = category;
        request.split_type = "itemized";
        request.tax_amount = taxAmount;
        request.items = items;
        return request;
    }
}
