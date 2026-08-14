package com.example.billsplit.data.remote.dto;

/** Matches POST /auth/login's expected body exactly. */
public class LoginRequest {
    private final String email;
    private final String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}