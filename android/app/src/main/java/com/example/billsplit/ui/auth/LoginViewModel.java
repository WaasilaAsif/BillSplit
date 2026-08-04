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
        // FakeDataSource-backed for now via AuthRepository; becomes a real
        // network call once Week 1 backend auth endpoints exist.
        User user = authRepository.login(email, password);
        loading.setValue(false);

        if (user != null) {
            loginSuccess.setValue(user);
        } else {
            errorMessage.setValue("Invalid email or password");
        }
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