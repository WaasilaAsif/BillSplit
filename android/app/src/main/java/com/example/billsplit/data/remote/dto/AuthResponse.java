package com.example.billsplit.data.remote.dto;

import com.example.billsplit.data.model.User;

/** Matches the { token, user } shape both /auth/login and /auth/register return. */
public class AuthResponse {
    private String token;
    private User user;

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}