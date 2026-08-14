package com.example.billsplit.ui.auth;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplit.data.model.User;
import com.example.billsplit.data.repository.AuthRepository;

public class SignUpViewModel extends ViewModel {

    private final AuthRepository authRepository = new AuthRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> signUpSuccess = new MutableLiveData<>();

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<User> getSignUpSuccess() {
        return signUpSuccess;
    }

    public void register(String fullName, String email, String password, String confirmPassword,
                         boolean agreedToTerms) {
        String validationError = validate(fullName, email, password, confirmPassword, agreedToTerms);
        if (validationError != null) {
            errorMessage.setValue(validationError);
            return;
        }

        loading.setValue(true);
        // fullName maps to "username" server-side — see the NOTE in
        // RegisterRequest.java about this naming mismatch.
        authRepository.register(fullName, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                loading.setValue(false);
                signUpSuccess.setValue(user);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    private String validate(String fullName, String email, String password, String confirmPassword,
                            boolean agreedToTerms) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Full name is required";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Enter a valid email";
        }
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        if (!agreedToTerms) {
            return "You must agree to the Terms & Privacy Policy";
        }
        return null;
    }
}