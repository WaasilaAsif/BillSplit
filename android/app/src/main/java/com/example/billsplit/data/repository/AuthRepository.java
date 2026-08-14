package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.User;
import com.example.billsplit.data.remote.ApiService;
import com.example.billsplit.data.remote.RetrofitClient;
import com.example.billsplit.data.remote.dto.AuthResponse;
import com.example.billsplit.data.remote.dto.LoginRequest;
import com.example.billsplit.data.remote.dto.RegisterRequest;
import com.example.billsplit.local.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Real implementation — replaces the FakeDataSource-backed version.
 * Async by necessity: Android throws NetworkOnMainThreadException on a
 * synchronous call here, so this uses a callback interface instead of
 * returning User directly. Retrofit's enqueue() runs the network call
 * on a background thread automatically and posts onSuccess/onError
 * back on the main thread — ViewModels can update LiveData directly
 * inside the callback with no extra threading code needed.
 */
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

    private void handleAuthResponse(Response<AuthResponse> response, AuthCallback callback) {
        if (response.isSuccessful() && response.body() != null) {
            AuthResponse body = response.body();
            TokenManager.getInstance().saveToken(body.getToken());
            callback.onSuccess(body.getUser());
        } else {
            callback.onError(extractServerErrorMessage(response));
        }
    }

    /**
     * Your Express error responses look like {"status":"error","message":"..."} —
     * this pulls the real message out instead of showing a generic failure,
     * so e.g. "An account with that email already exists" actually reaches the user.
     */
    private String extractServerErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                JSONObject json = new JSONObject(response.errorBody().string());
                if (json.has("message")) {
                    return json.getString("message");
                }
            }
        } catch (Exception e) {
            // fall through to generic message below — the raw error body wasn't valid JSON
        }
        return "Something went wrong (HTTP " + response.code() + ")";
    }

    private String networkErrorMessage(Throwable t) {
        if (t instanceof java.net.SocketTimeoutException) {
            return "Request timed out — the server may be waking up, try again in a moment";
        }
        return "Could not reach the server. Check your connection.";
    }
}