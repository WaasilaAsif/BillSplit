package com.example.billsplit.data.remote;

import com.example.billsplit.data.remote.dto.AuthResponse;
import com.example.billsplit.data.remote.dto.LoginRequest;
import com.example.billsplit.data.remote.dto.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * One method per backend endpoint. As GroupRepository/ExpenseRepository/etc.
 * move off AppDataStore onto the real backend, their endpoints get added
 * here too — same interface, growing alongside the backend's routes.
 */
public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
}