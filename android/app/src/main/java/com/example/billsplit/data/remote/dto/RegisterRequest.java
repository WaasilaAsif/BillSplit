package com.example.billsplit.data.remote.dto;

/**
 * Matches POST /auth/register's expected body: { username, email, password }.
 *
 * NOTE — naming mismatch worth deciding on deliberately, not silently:
 * your users table (and this backend) only has "username", not a
 * separate "full name" field. SignUpFragment's mockup shows a "Full
 * name" input, but there's currently nowhere for that to actually go
 * except this username field. For now, AuthRepository maps whatever
 * the user types in "Full name" straight into this. If you want a
 * real display-name concept separate from username later, that's a
 * schema change (a new users.display_name column), not just an app fix.
 */
public class RegisterRequest {
    private final String username;
    private final String email;
    private final String password;

    public RegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}