package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.Friend;
import com.example.billsplit.data.remote.ApiErrorMapper;
import com.example.billsplit.data.remote.ApiService;
import com.example.billsplit.data.remote.RetrofitClient;
import com.example.billsplit.data.remote.dto.ApiResponse;
import com.example.billsplit.data.remote.dto.ExpenseDto;
import com.example.billsplit.data.remote.dto.FriendDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRepository {

    private final ApiService apiService = RetrofitClient.getApiService();

    public void getFriends(ApiCallback<List<Friend>> callback) {
        apiService.getFriends().enqueue(new Callback<ApiResponse<List<FriendDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FriendDto>>> call, Response<ApiResponse<List<FriendDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Friend> friends = new ArrayList<>();
                    for (FriendDto dto : response.body().getData()) {
                        friends.add(new Friend(dto.getUserId(), dto.getUsername(), dto.getEmail(), dto.getNetBalance()));
                    }
                    callback.onSuccess(friends);
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FriendDto>>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    //Does not have an endpoint to get the full details of a user/friend
    public void getFriend(String friendId, ApiCallback<Friend> callback) {
        getFriends(new ApiCallback<List<Friend>>() {
            @Override
            public void onSuccess(List<Friend> friends) {
                for (Friend f : friends) {
                    if (f.getUserId().equals(friendId)) {
                        callback.onSuccess(f);
                        return;
                    }
                }
                callback.onError("Friend not found");
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void getSharedExpenses(String friendId, ApiCallback<List<Expense>> callback) {
        apiService.getSharedExpenses(friendId).enqueue(new Callback<ApiResponse<List<ExpenseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ExpenseDto>>> call, Response<ApiResponse<List<ExpenseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(ExpenseRepository.toExpenseList(response.body().getData()));
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
}
