package com.example.billsplit.ui.auth;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplit.data.model.User;
import com.example.billsplit.data.repository.AuthRepository;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository = new AuthRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> loginSuccess = new MutableLiveData<>();

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<User> getLoginSuccess() {
        return loginSuccess;
    }

    public void login(String email, String password) {
        String validationError = validate(email, password);
        if (validationError != null) {
            errorMessage.setValue(validationError);
            return;
        }

        loading.setValue(true);
        // Real network call now. Retrofit's enqueue() (inside AuthRepository)
        // runs this off the main thread and posts the callback back onto it —
        // safe to touch LiveData directly from onSuccess/onError below.
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                loading.setValue(false);
                loginSuccess.setValue(user);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    private String validate(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Enter a valid email";
        }
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        return null;
    }
}