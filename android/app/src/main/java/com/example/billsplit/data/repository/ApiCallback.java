package com.example.billsplit.data.repository;

//Callback results
public interface ApiCallback<T> {
    void onSuccess(T result);
    void onError(String message);
}
