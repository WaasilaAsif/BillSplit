package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.ExpenseItem;
import com.example.billsplit.data.model.ExpenseSplit;
import com.example.billsplit.data.model.SplitType;
import com.example.billsplit.data.remote.ApiErrorMapper;
import com.example.billsplit.data.remote.ApiService;
import com.example.billsplit.data.remote.RetrofitClient;
import com.example.billsplit.data.remote.dto.ApiResponse;
import com.example.billsplit.data.remote.dto.CreateExpenseRequest;
import com.example.billsplit.data.remote.dto.ExpenseCreatedDto;
import com.example.billsplit.data.remote.dto.ExpenseDetailDto;
import com.example.billsplit.data.remote.dto.ExpenseDto;
import com.example.billsplit.data.remote.dto.ExpenseItemDto;
import com.example.billsplit.data.remote.dto.ExpenseSplitDto;
import com.example.billsplit.data.remote.dto.ExpenseVoidedDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseRepository {

    private final ApiService apiService = RetrofitClient.getApiService();

    public void getExpensesForGroup(String groupId, ApiCallback<List<Expense>> callback) {
        apiService.getGroupExpenses(groupId).enqueue(new Callback<ApiResponse<List<ExpenseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ExpenseDto>>> call, Response<ApiResponse<List<ExpenseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(toExpenseList(response.body().getData()));
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExpenseDto>>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    public void getExpense(String expenseId, ApiCallback<Expense> callback) {
        apiService.getExpenseDetail(expenseId).enqueue(new Callback<ApiResponse<ExpenseDetailDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ExpenseDetailDto>> call, Response<ApiResponse<ExpenseDetailDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(toExpenseWithDetail(response.body().getData()));
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ExpenseDetailDto>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    /** Splits are computed server-side from participant_ids — see splitCalculator.js — not pre-built client-side. */
    public void addExpense(String groupId, String paidBy, double amount, String description,
                            String category, List<String> participantIds, ApiCallback<String> callback) {
        CreateExpenseRequest request = CreateExpenseRequest.equalSplit(groupId, paidBy, description, category, amount, participantIds);
        apiService.createExpense(request).enqueue(new Callback<ApiResponse<ExpenseCreatedDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ExpenseCreatedDto>> call, Response<ApiResponse<ExpenseCreatedDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData().getId());
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ExpenseCreatedDto>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    //ledger is append only for now
    public void updateExpense(String expenseId, String groupId, String paidBy, double amount, String description,
                               String category, List<String> participantIds, ApiCallback<String> callback) {
        CreateExpenseRequest request = CreateExpenseRequest.equalSplit(groupId, paidBy, description, category, amount, participantIds);
        apiService.updateExpense(expenseId, request).enqueue(new Callback<ApiResponse<ExpenseCreatedDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ExpenseCreatedDto>> call, Response<ApiResponse<ExpenseCreatedDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData().getId());
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ExpenseCreatedDto>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    public void deleteExpense(String expenseId, ApiCallback<Void> callback) {
        apiService.deleteExpense(expenseId).enqueue(new Callback<ApiResponse<ExpenseVoidedDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ExpenseVoidedDto>> call, Response<ApiResponse<ExpenseVoidedDto>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ExpenseVoidedDto>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    static Expense toExpense(ExpenseDto dto) {
        Expense expense = new Expense();
        expense.setId(dto.getId());
        expense.setGroupId(dto.getGroupId());
        expense.setPaidBy(dto.getPaidBy());
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setCategory(dto.getCategory());
        expense.setSplitType(parseSplitType(dto.getSplitType()));
        expense.setReceiptUrl(dto.getReceiptUrl());
        expense.setTaxAmount(dto.getTaxAmount());
        expense.setCreatedAt(dto.getCreatedAt());
        expense.setYourShare(dto.getYourShare());
        expense.setGroupName(dto.getGroupName());
        return expense;
    }

    static List<Expense> toExpenseList(List<ExpenseDto> dtos) {
        List<Expense> expenses = new ArrayList<>();
        if (dtos == null) return expenses;
        for (ExpenseDto dto : dtos) {
            expenses.add(toExpense(dto));
        }
        return expenses;
    }

    private static Expense toExpenseWithDetail(ExpenseDetailDto dto) {
        Expense expense = toExpense(dto);

        List<ExpenseSplit> splits = new ArrayList<>();
        if (dto.getSplits() != null) {
            for (ExpenseSplitDto s : dto.getSplits()) {
                ExpenseSplit split = new ExpenseSplit(s.getUserId(), s.getShareAmount());
                split.setUsername(s.getUsername());
                splits.add(split);
            }
        }
        expense.setSplits(splits);

        List<ExpenseItem> items = new ArrayList<>();
        if (dto.getItems() != null) {
            for (ExpenseItemDto i : dto.getItems()) {
                items.add(new ExpenseItem(i.getItemName(), i.getItemPrice(), i.getAssignedToUserId()));
            }
        }
        expense.setItems(items);

        return expense;
    }

    private static SplitType parseSplitType(String value) {
        if (value == null) return SplitType.EQUAL;
        try {
            return SplitType.valueOf(value.toUpperCase(java.util.Locale.US));
        } catch (IllegalArgumentException e) {
            return SplitType.EQUAL;
        }
    }
}
