package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.User;
import com.example.billsplit.data.remote.ApiErrorMapper;
import com.example.billsplit.data.remote.ApiService;
import com.example.billsplit.data.remote.RetrofitClient;
import com.example.billsplit.data.remote.dto.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService apiService = RetrofitClient.getApiService();

    /** Backs AddGroupMemberFragment's "suggested members" search — GET /users?search=. */
    public void search(String query, ApiCallback<List<User>> callback) {
        apiService.searchUsers(query).enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }
}
