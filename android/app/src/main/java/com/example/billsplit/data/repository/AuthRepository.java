package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.User;
import com.example.billsplit.data.remote.ApiErrorMapper;
import com.example.billsplit.data.remote.ApiService;
import com.example.billsplit.data.remote.RetrofitClient;
import com.example.billsplit.data.remote.dto.ApiResponse;
import com.example.billsplit.data.remote.dto.AuthResponse;
import com.example.billsplit.data.remote.dto.LoginRequest;
import com.example.billsplit.data.remote.dto.RegisterRequest;
import com.example.billsplit.local.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Replace fake data with real backend calls for authentication and user data. AuthRepository handles login, registration, and fetching the current user, using Retrofit to communicate with the backend API. It also manages token storage via TokenManager.
public class AuthRepository {

    private final ApiService apiService = RetrofitClient.getApiService();

    public interface AuthCallback {

        void onSuccess(User user);

        void onError(String message);
    }

    public void login(String email, String password, AuthCallback callback) {
        apiService.login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                handleAuthResponse(response, callback);
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError(networkErrorMessage(t));
            }
        });
    }

    public void register(String username, String email, String password, AuthCallback callback) {
        apiService.register(new RegisterRequest(username, email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                handleAuthResponse(response, callback);
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError(networkErrorMessage(t));
            }
        });
    }

    public void getCurrentUser(ApiCallback<User> callback) {
        apiService.getCurrentUser().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    private void handleAuthResponse(Response<AuthResponse> response, AuthCallback callback) {
        if (response.isSuccessful() && response.body() != null) {
            AuthResponse body = response.body();
            TokenManager.getInstance().saveToken(body.getToken());
            callback.onSuccess(body.getUser());
        } else {
            callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
        }
    }

    private String networkErrorMessage(Throwable t) {
        return ApiErrorMapper.networkErrorMessage(t);
    }
}
