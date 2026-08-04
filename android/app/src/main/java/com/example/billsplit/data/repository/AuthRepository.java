package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.User;
import com.example.billsplit.local.FakeDataSource;

import java.time.Instant;

/**
 * SCAFFOLDING — backed by FakeDataSource until Week 1 backend auth
 * endpoints (POST /auth/login, POST /auth/register) exist.
 *
 * Swap the method bodies for real Retrofit calls when the backend is
 * ready; keep these public method signatures the same so LoginViewModel
 * / SignUpViewModel don't need to change at all.
 */
public class AuthRepository {

    /**
     * Fake check: matches by email against FakeDataSource users,
     * ignores password entirely. Replace with a real POST /auth/login
     * call that verifies the password server-side and returns a JWT
     * (store via TokenManager) alongside the User.
     */
    public User login(String email, String password) {
        for (User user : FakeDataSource.getUsers()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Fake success — always "creates" a user. Replace with a real
     * POST /auth/register call once the backend exists.
     */
    public User register(String fullName, String email, String password) {
        String fakeId = "temp-" + System.currentTimeMillis();
        return new User(fakeId, fullName, email, Instant.now().toString());
    }
}